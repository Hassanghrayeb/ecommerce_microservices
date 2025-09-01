package com.hamster.ecommerce.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.hamster.ecommerce.config.AppProperties;
import com.hamster.ecommerce.exception.ErrorCode;
import com.hamster.ecommerce.mapper.LoginMapper;
import com.hamster.ecommerce.model.dto.UserValidationDTO;
import com.hamster.ecommerce.model.entity.AuditHistory;
import com.hamster.ecommerce.model.entity.FailedLogin;
import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.entity.Person;
import com.hamster.ecommerce.model.entity.Role;
import com.hamster.ecommerce.model.entity.Session;
import com.hamster.ecommerce.model.simple.AuthRequest;
import com.hamster.ecommerce.model.simple.AuthResponse;
import com.hamster.ecommerce.model.simple.RefreshRequest;
import com.hamster.ecommerce.model.simple.UserExistsResponse;
import com.hamster.ecommerce.repository.AuditHistoryRepository;
import com.hamster.ecommerce.repository.FailedLoginRepository;
import com.hamster.ecommerce.repository.LoginRepository;
import com.hamster.ecommerce.repository.PersonRepository;
import com.hamster.ecommerce.repository.RoleRepository;
import com.hamster.ecommerce.repository.SessionRepository;
import com.hamster.ecommerce.service.AuthService;
import com.hamster.ecommerce.service.LoginService;
import com.hamster.ecommerce.service.UserValidationService;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.ContextUtil;
import com.hamster.ecommerce.util.EncryptionUtil;
import com.hamster.ecommerce.util.SharedSecretUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService
{
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final LoginRepository loginRepository;
    private final LoginService loginService;
    private final SessionRepository sessionRepository;
    private final FailedLoginRepository failedLoginRepository;
    private final AppProperties appProperties;
    private final EncryptionUtil encryptionUtil;
    private final SharedSecretUtil sharedSecretUtil;
    private final ContextUtil contextUtil;
    private final LoginMapper loginMapper;
    private final PersonRepository personRepository;
    private final AuditUtil auditUtil;
    private final AuditHistoryRepository auditHistoryRepository;
    private final RoleRepository roleRepository;
    private final UserValidationService userValidationService;

    public AuthServiceImpl(LoginRepository loginRepository, LoginService loginService,
            SessionRepository sessionRepository, FailedLoginRepository failedLoginRepository,
            AppProperties appProperties, EncryptionUtil encryptionUtil, SharedSecretUtil sharedSecretUtil,
            ContextUtil contextUtil, LoginMapper loginMapper, PersonRepository personRepository,
            AuditUtil auditUtil, AuditHistoryRepository auditHistoryRepository, RoleRepository roleRepository,
            UserValidationService userValidationService)
    {
        this.loginRepository = loginRepository;
        this.loginService = loginService;
        this.sessionRepository = sessionRepository;
        this.failedLoginRepository = failedLoginRepository;
        this.appProperties = appProperties;
        this.encryptionUtil = encryptionUtil;
        this.sharedSecretUtil = sharedSecretUtil;
        this.contextUtil = contextUtil;
        this.loginMapper = loginMapper;
        this.personRepository = personRepository;
        this.auditUtil = auditUtil;
        this.auditHistoryRepository = auditHistoryRepository;
        this.roleRepository = roleRepository;
        this.userValidationService = userValidationService;
    }

    @Override
    @Transactional("postgresTransactionManager")
    public AuthResponse validateUser(AuthRequest authRequest)
    {
        AuthResponse authResponse = new AuthResponse();

        validateInputParams(authRequest, authResponse);
        if (Objects.nonNull(authResponse.getHttpStatus()))
            return authResponse;

        Login login = getUserByUsername(authRequest.getUsername(), authResponse);
        if (Objects.isNull(login))
            return authResponse;

        Optional<FailedLogin> optionalFailedLogin = failedLoginRepository.findByLoginId(login.getId());
        FailedLogin failedLogin = optionalFailedLogin.orElseGet(() -> new FailedLogin(login.getId(), 0));

        processConsecutiveFailedLoginDelay(authRequest, authResponse, failedLogin);
        if (Objects.nonNull(authResponse.getHttpStatus()))
            return authResponse;

        if (!isPasswordValid(login, authRequest))
        {
            processFailedLogin(login, authResponse, failedLogin);
            return authResponse;
        }

        if (isAccountLocked(login, authResponse))
            return authResponse;

        /*----------------------------------------------------------+
        |	User validates.  Build valid auth response, create a    |
        |   new session record in the db (and purge any existing    |
        |   ones, and delete any consecutive failed login attempts. |
        |   Note that there could be a JOSEException thrown when    |
        |   building the access token.  If that happens, a 500      |
        |   should already be created for the authResponse          |
        +----------------------------------------------------------*/
        if (buildAuthResponseAndCreateSession(login, authResponse, 0))
        {
            failedLoginRepository.deleteExistingByUserId(login.getId());
        }
        return authResponse;
    }

    private void validateInputParams(AuthRequest authRequest, AuthResponse authResponse)
    {
		/*----------------------------------------------------------+
		|	First validate that we have a username and password to	|
		|	work with.  If we don't, there's no point in proceeding	|
		+----------------------------------------------------------*/
        if (Objects.isNull(authRequest.getUsername()) || Objects.isNull(authRequest.getPassword()))
        {
            authResponse.getResponseMap().put("error", "invalid_request");
            authResponse.getResponseMap().put("error_description", "username and/or password missing");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        }
    }

    private Login getUserByUsername(String username, AuthResponse authResponse)
    {
        /*----------------------------------------------------------+
		|	Look for a user with this username.  If one is not		|
		| 	found, flag with invalid credentials and return.    	|
		+----------------------------------------------------------*/
        Optional<Login> optionalLogin = loginService.findByUsername(username);
        if (optionalLogin.isEmpty())
        {
            /*----------------------------------------------------------+
            |	We didn't find the username. Invalidate and return.		|
            +----------------------------------------------------------*/
            authResponse.getResponseMap().put("error", "invalid_grant");
            authResponse.getResponseMap().put("error_description", "Invalid user credentials");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);

            return null;
        }
        /*----------------------------------------------------------+
        |   User found.  Return for the next step.                  |
        +----------------------------------------------------------*/
        return optionalLogin.get();
    }

    private void processConsecutiveFailedLoginDelay(AuthRequest authRequest, AuthResponse authResponse, FailedLogin failedLogin)
    {
        /*----------------------------------------------------------+
        |   We give the user 3 chances to login successfully.       |
        |   After that, delay logins by 5 minutes for each failed   |
        |   login, up to a max of one hour.  Subtract only 2 to     |
        |   account for 0 being a spot.                             |
        +----------------------------------------------------------*/
        int delayInMinutes = (failedLogin.getConsecutiveFailCount() - 2) * 5;
        if (delayInMinutes <= 0)
            return;

        /*----------------------------------------------------------+
        |   Get the time of the first failed login and add the      |
        |   delayInMinutes we're supposed to wait.  If now is after |
        |   the required delay, they may proceed.  If not, return   |
        |   an error indicating how much longer until they may try  |
        |   again.                                                  |
        +----------------------------------------------------------*/
        LocalDateTime dateTimeUseCanTryAgain = failedLogin.getUpdateTimestamp().plusMinutes(delayInMinutes);
        Duration remainingTimeUntilNextTry = Duration.between(LocalDateTime.now(), dateTimeUseCanTryAgain);

        //cap at 1 hour
        if (remainingTimeUntilNextTry.toMinutes() > 60)
            remainingTimeUntilNextTry = Duration.ofMinutes(60);

        //a negative numbers means the requisite delay time has passed
        if (remainingTimeUntilNextTry.isNegative())
            return;

        authResponse.getResponseMap().put("error", "account_locked");
        authResponse.getResponseMap().put("error_description", "Try again in: " +
                remainingTimeUntilNextTry.toMinutesPart() + " minute(s), " +
                remainingTimeUntilNextTry.toSecondsPart() + " second(s)");
        authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
    }

    private boolean isPasswordValid(Login login, AuthRequest authRequest)
    {
        /*----------------------------------------------------------+
        |	Confirm the submitted and encrypted passwords match     |
        +----------------------------------------------------------*/
        return encryptionUtil.matches(authRequest.getPassword(), login.getPassword());
    }

    private void processFailedLogin(Login login, AuthResponse authResponse, FailedLogin failedLogin)
    {
        /*----------------------------------------------------------+
        |   The passwords don't match.  Increment the failedLogin   |
        |   and save.  We track consecutive failed logins.          |
        +----------------------------------------------------------*/
        failedLogin.incrementConsecutiveFailedLoginCount();
        failedLogin.setUpdateTimestamp(LocalDateTime.now());
        if (failedLoginRepository.findByLoginId(login.getId()).isPresent())
            failedLoginRepository.save(failedLogin);
        else
            failedLoginRepository.saveNewItem(failedLogin.getLoginId(),
                    failedLogin.getConsecutiveFailCount(), failedLogin.getUpdateTimestamp());

        authResponse.getResponseMap().put("error", "invalid_grant");
        authResponse.getResponseMap().put("error_description", "Invalid user credentials");
        authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
    }

    private boolean isAccountLocked(Login login, AuthResponse authResponse)
    {
        if (!login.isEnabled())
        {
            authResponse.getResponseMap().put("error", "account_locked");
            authResponse.getResponseMap().put("error_description", "Account Locked.  Please contact an administrator");
            authResponse.setHttpStatus(HttpStatus.FORBIDDEN);

            return true;
        }
        return false;
    }

    private boolean buildAuthResponseAndCreateSession(Login login, AuthResponse authResponse,
            int currentConsecutiveRefreshes)
    {
        String accessToken = generateAccessToken(login, authResponse);
        if (accessToken != null)
        {
            String refreshToken = generateRefreshToken(authResponse);
            authResponse.getResponseMap().put("id", login.getId());
            authResponse.setHttpStatus(HttpStatus.OK);

            UserValidationDTO userValidationDTO = userValidationService.validateUser(login);
            authResponse.getResponseMap().put("user_validation", userValidationDTO);

            Session session = new Session();
            session.setUsername(login.getUsername());
            session.setConsecutiveRefreshes(currentConsecutiveRefreshes);
            session.setAccessToken(accessToken);
            session.setRefreshToken(refreshToken);
            session.setCreationTime(LocalDateTime.now());

            sessionRepository.deleteExistingSessionsForUser(login.getUsername());
            sessionRepository.save(session.getUsername(), session.getConsecutiveRefreshes(), session.getAccessToken(),
                    session.getRefreshToken(), session.getCreationTime());

            return true;
        }
        return false;
    }

    private String generateAccessToken(Login login, AuthResponse authResponse)
    {
        try
        {
            /*------------------------------------------------------------------+
            |   Get the accessTokenLifeSpan from the app settings. Add this     |
            |   to "now" to get the token expiration.  We need a java.util.Date |
            |   as well as longs representing seconds from epoch.               |
            +------------------------------------------------------------------*/
            Duration accessTokenLifeSpan = Duration.ofMinutes(appProperties.getAccessTokenLifespanInMinutes());
            Instant tokenExpirationInstant = LocalDateTime.now().plus(accessTokenLifeSpan)
                    .atZone(ZoneId.systemDefault()).toInstant();

            Date tokenExpirationDate = Date.from(tokenExpirationInstant);
            long secondsFromEpochToTokenExpiration = tokenExpirationInstant.getEpochSecond();
            long secondsFromEpochToNow = Instant.now().getEpochSecond();

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(login.getUsername())
                    .issuer("laitron")
                    .expirationTime(tokenExpirationDate)
                    .claim("exp", secondsFromEpochToTokenExpiration)
                    .claim("iat", secondsFromEpochToNow)
                    .claim("nbf", secondsFromEpochToNow)
                    //TODO: This will change with each env
                    .claim("iss", "hamster")
                    .claim("aud", "hamster")
                    .claim("authorities", login.getAuthorities())
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            // Apply the HMAC protection
            signedJWT.sign(sharedSecretUtil.getJWSSigner());

			/*----------------------------------------------------------------------+
			|	Serialize the signedJWT.  This will compact it to something like	|
			|	eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpbXBhY3QiLCJzdWIiOiJBTlRPSU5FIiwi	|
			|	ZXhwIjoxNjA4Njg3NjU4fQ.lutKA9eFiPrgeqz_2jHEuEza-6hnNNY3YotuOvtv9rc	|
			+----------------------------------------------------------------------*/
            String serializedJWT = signedJWT.serialize();

            //Build the response map
            authResponse.getResponseMap().put("access_token", serializedJWT);
            authResponse.getResponseMap().put("expires_in", accessTokenLifeSpan.getSeconds());
            authResponse.getResponseMap().put("token_type", "bearer");
            authResponse.getResponseMap().put("user_profile", loginMapper.entityToDTO(login));

            return serializedJWT;
        }
        catch (JOSEException e)
        {
            log.error("JOSEException", e);
            authResponse.getResponseMap().put("error", "internal server error");
            authResponse.getResponseMap().put("error_description", "Internal Server Error");
            authResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    private String generateRefreshToken(AuthResponse authResponse)
    {
        /*------------------------------------------------------------------------------------------+
        |   Generate a refresh token, which is simply a 128 byte random string, and put it in the   |
        |   response.  We also return it, so it can be used for the user's session.                  |
        +------------------------------------------------------------------------------------------*/
        String refreshTokenString = RandomStringUtils.randomAlphanumeric(128);
        authResponse.getResponseMap().put("refresh_token", refreshTokenString);
        return refreshTokenString;
    }

    @Override
    @Transactional("postgresTransactionManager")
    public AuthResponse refreshUserToken(RefreshRequest refreshRequest)
    {
        AuthResponse authResponse = new AuthResponse();
        /*----------------------------------------------------------+
        |   Make sure we get a valid username and refresh token     |
        +----------------------------------------------------------*/
        validateRefreshRequest(refreshRequest, authResponse);
        if (Objects.nonNull(authResponse.getHttpStatus()))
            return authResponse;

        /*----------------------------------------------------------+
        |   Make sure we already have a session to refresh          |
        +----------------------------------------------------------*/
        Optional<Session> optionalSession = sessionRepository.findByRefreshToken(refreshRequest.getRefreshToken());
        if (optionalSession.isEmpty())
        {
            authResponse.getResponseMap().put("error", "invalid_grant");
            authResponse.getResponseMap().put("error_description", "Invalid refresh token");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);

            return authResponse;
        }

        /*----------------------------------------------------------+
        |   If the refreshToken expiration time is before now, the  |
        |   token is stale.  Return an error to the user.           |
        +----------------------------------------------------------*/
        Session session = optionalSession.get();
        LocalDateTime refreshTokenExpiration =
                session.getCreationTime().plusMinutes(appProperties.getRefreshTokenLifespanInMinutes());

        if (refreshTokenExpiration.isBefore(LocalDateTime.now()))
        {
            authResponse.getResponseMap().put("error", "invalid_grant");
            authResponse.getResponseMap().put("error_description", "Expired refresh token");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
            sessionRepository.deleteByRefreshToken(refreshRequest.getRefreshToken());

            return authResponse;
        }

        /*----------------------------------------------------------+
        |   We allow a limited number of refreshes before we force  |
        |   the user to re-authenticate.                            |
        +----------------------------------------------------------*/
        session.incrementConsecutiveRefreshes();
        if (session.getConsecutiveRefreshes() > appProperties.getMaxConsecutiveRefreshesAllowed())
        {
            authResponse.getResponseMap().put("error", "refresh_limit");
            authResponse.getResponseMap().put("error_description", "Refresh limit reached.  Please log in again.");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
            sessionRepository.deleteByRefreshToken(refreshRequest.getRefreshToken());

            return authResponse;
        }

        /*----------------------------------------------------------+
        |   Do a sanity check to ensure the user exists             |
        +----------------------------------------------------------*/
        Optional<Login> optionalLogin = loginRepository.findByUsername(refreshRequest.getUsername());
        if (optionalLogin.isEmpty())
        {
            authResponse.getResponseMap().put("error", "invalid_grant");
            authResponse.getResponseMap().put("error_description", "Invalid user credentials");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);

            return authResponse;
        }

        /*----------------------------------------------------------+
        |   Make sure the user's account is still enabled           |
        +----------------------------------------------------------*/
        Login login = optionalLogin.get();
        if (!login.isEnabled())
        {
            authResponse.getResponseMap().put("error", "account_locked");
            authResponse.getResponseMap().put("error_description", "Account Locked.  Please contact an administrator");
            authResponse.setHttpStatus(HttpStatus.FORBIDDEN);

            return authResponse;
        }

        /*----------------------------------------------------------+
        |   We're good.  Delete the current session and generate    |
        |   a new access and refresh token on a new session         |
        +----------------------------------------------------------*/
        sessionRepository.deleteByRefreshToken(refreshRequest.getRefreshToken());
        buildAuthResponseAndCreateSession(optionalLogin.get(), authResponse, session.getConsecutiveRefreshes());

        return authResponse;
    }

    private void validateRefreshRequest(RefreshRequest refreshRequest, AuthResponse authResponse)
    {
        if (Objects.isNull(refreshRequest.getUsername()) || Objects.isNull(refreshRequest.getRefreshToken()))
        {
            authResponse.getResponseMap().put("error", "invalid_request");
            authResponse.getResponseMap().put("error_description", "Missing parameter: username or refresh token");
            authResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional("postgresTransactionManager")
    public void logUserOut()
    {
        /*----------------------------------------------------------+
        |   Simply remove the record from the session table         |
        +----------------------------------------------------------*/
        String accessToken = contextUtil.getCurrentAccessToken();
        if (Objects.nonNull(accessToken))
            sessionRepository.deleteByAccessToken(accessToken);
    }

    @Override
    public ErrorCode deleteAccount()
    {
        Long currentUserId = contextUtil.getCurrentUserId();
        Optional<Login> optionalUser = loginRepository.findById(currentUserId);
        if (optionalUser.isEmpty())
            return ErrorCode.INVALID_USER;

        Login login = optionalUser.get();

        List<Role> roleList = roleRepository.findByLoginId(login.getId());
        if (roleList.stream().anyMatch(Role::isSystemRole))
            return ErrorCode.SYSTEM_USER;

        Person person = personRepository.findByLoginId(login.getId())
                .orElse(null);

        login.setPerson(person);
        login.setUpdateTimestamp(null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(login);

        AuditHistory auditHistory = new AuditHistory();
        auditHistory.setAction('D');
        auditHistory.setTableName("login");
        auditHistory.setTablePk(login.getId());
        auditHistory.setRowData(jsonNode.toString());
        auditUtil.setAuditColumns(auditHistory);

        auditHistoryRepository.save(auditHistory);

        loginRepository.deleteById(login.getId());

        if (person != null)
            personRepository.deleteById(person.getId());

        return ErrorCode.NO_ERROR;
    }

    @Override
    public UserExistsResponse userExists(String email)
    {
        return new UserExistsResponse(loginRepository.doesUsernameExist(email));
    }

}
