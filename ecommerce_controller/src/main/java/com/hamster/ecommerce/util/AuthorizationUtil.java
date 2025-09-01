package com.hamster.ecommerce.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.hamster.ecommerce.config.AppProperties;
import com.hamster.ecommerce.config.AuthorizationResults;
import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.entity.XSharedSecret;
import com.hamster.ecommerce.repository.SessionRepository;
import com.hamster.ecommerce.service.LoginService;
import com.hamster.ecommerce.service.XSharedSecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorizationUtil
{
    private static final Logger log = LoggerFactory.getLogger(AuthorizationUtil.class);
    private static List<MACVerifier> macVerifierList = new ArrayList<>();

    private final AppProperties commonAppProperties;
    private final XSharedSecretService xSharedSecretService;
    private final LoginService loginService;
    private final SessionRepository sessionRepository;

    public AuthorizationUtil(AppProperties commonAppProperties, XSharedSecretService xSharedSecretService,
            LoginService loginService, SessionRepository sessionRepository)
    {
        this.commonAppProperties = commonAppProperties;
        this.xSharedSecretService = xSharedSecretService;
        this.loginService = loginService;
        this.sessionRepository = sessionRepository;
    }

    public AuthorizationResults processAccessToken(String accessToken)
    {
        AuthorizationResults authorizationResults = new AuthorizationResults(accessToken);
        /*------------------------------------------------------+
        |	Try to parse for a JWSObject first.  If we don't    |
        |   get that, don't bother with anything else.          |
        +------------------------------------------------------*/
        JWSObject jwsObject = parseRequestTokenHeaderForJWSObject(accessToken);
        if (jwsObject != null)
        {
            authorizationResults.setJwsObject(jwsObject);
            /*------------------------------------------------------+
            |	Get the claims of this token.  If the token         |
            |   validates we can trust its claims.                  |
            +------------------------------------------------------*/
            JWTClaimsSet jwtClaimsSet = parseRequestTokenHeaderForJWTClaimsSet(accessToken);
            if (jwtClaimsSet != null)
            {
                authorizationResults.setJwtClaimsSet(jwtClaimsSet);
                authorizationResults.setTokenIsValid(validateToken(authorizationResults.getJwsObject()));
                authorizationResults.setJwtClaimsAreValid(validateJwtClaims(authorizationResults.getJwtClaimsSet()));
            }
        }
        return authorizationResults;
    }

    private JWSObject parseRequestTokenHeaderForJWSObject(String jwtToken)
    {
		/*------------------------------------------------------+
		|	Convert the String into a JWSObject, if possible,	|
		|	and return.  If not, return null					|
		+------------------------------------------------------*/
        try
        {
            return JWSObject.parse(jwtToken);
        }
        catch (Exception e)
        {
            log.error("Unable to parse requestTokenHeader to JWSObject: {}", jwtToken);
        }
        return null;
    }

    private JWTClaimsSet parseRequestTokenHeaderForJWTClaimsSet(String requestTokenHeader)
    {
		/*------------------------------------------------------+
		|	Parse the jwt claimSet from the token string, if 	|
		|	possible, and return.  If not, return null	    	|
		+------------------------------------------------------*/
        try
        {
            return JWTParser.parse(requestTokenHeader).getJWTClaimsSet();
        }
        catch (Exception e)
        {
            log.error("Unable to parse requestTokenHeader to JWT: {}", requestTokenHeader);
        }
        return null;
    }

    private boolean validateToken(JWSObject jwsObject)
    {

        if (jwsObject == null)
            return false;

		/*------------------------------------------------------+
		|	Attempt to verify this token's signature with the	|
		|	shared secret key.  Most of the time it will be		|
		|	the first item in the macVerifierList.				|
		|	On occasion, it will be the next one because we		|
		|	are rotating the keys periodically.  So the token	|
		|	may have been generated with the previous key.		|
		+------------------------------------------------------*/
        if (iterateOverMacVerifierList(jwsObject))
            return true;

		/*------------------------------------------------------+
		|	The auth server may have just generated a new		|
		|	shared secret, or this may be the first run since	|
		|	the service was started.  Reload the list from the	|
		|	database, which is acting as a keyvault and try		|
		|	one more time to be sure.							|
		+------------------------------------------------------*/
        try
        {
            List<XSharedSecret> xSharedSecretList = xSharedSecretService.findAllXSharedSecretOrderByCreateDateTimeDesc();

            List<MACVerifier> updatedMacVerifierList = new ArrayList<>();
            for (XSharedSecret xSharedSecret : xSharedSecretList)
                updatedMacVerifierList.add(new MACVerifier(xSharedSecret.getKey()));
            macVerifierList = updatedMacVerifierList;
        }
        catch (JOSEException e)
        {
            log.error("Unable to create MACVerifier from given key", e);
        }

		/*------------------------------------------------------+
		|	After refreshing the MACVerifier list, if any keys	|
		|	validate, return true.  Otherwise return false.		|
		+------------------------------------------------------*/
        return iterateOverMacVerifierList(jwsObject);
    }

    private boolean iterateOverMacVerifierList(JWSObject jwsObject)
    {
        try
        {
            for (MACVerifier macVerifier : macVerifierList)
            {
                if (jwsObject.verify(macVerifier))
                    return true;
            }
        }
        catch (Exception e)
        {
            log.error("Unable to verify jwsObject", e);
        }
        return false;
    }

    /*------------------------------------------------------+
    |	Get the claims of this token.  If the token         |
    |   validates we can trust its claims.                  |
    +------------------------------------------------------*/
    private boolean validateJwtClaims(JWTClaimsSet jwtClaimsSet)
    {
        if (jwtClaimsSet == null)
            return false;

        /*------------------------------------------------------+
		|   By the time we get here, we should have established |
		|   that the token is trustworthy.  Now we need to      |
		|   validate the claims.  The most important one is the |
		|   expiration.                                         |
        +------------------------------------------------------*/
        Date claimedExpirationDate = jwtClaimsSet.getExpirationTime();

        /*------------------------------------------------------+
		|   java.util.Date is legacy, but that's what Nimbus    |
		|   uses.  If expiration is before now, fail validation |
        +------------------------------------------------------*/
        return claimedExpirationDate.after(new Date());

        /*------------------------------------------------------+
		|   Add additional validation as needed.                |
		+------------------------------------------------------*/
    }

    public void setUserSecurityContext(AuthorizationResults authorizationResults)
    {
        if (authorizationResults.getTokenIsValid()
                && authorizationResults.getJwtClaimsAreValid())
        {
            /*----------------------------------------------+
            |   The jwtClaimsSet subject is the username    |
            +----------------------------------------------*/
            String username = authorizationResults.getJwtClaimsSet().getSubject();

            /*--------------------------------------------------------------+
            |	This lookup occurs on every request.  It's fast, but this	|
            |	would be a great candidate for caching, like REDIS			|
            +--------------------------------------------------------------*/
            Optional<Login> optionalUser = loginService.findByUsername(username);
            if (optionalUser.isPresent())
            {
                Login login = optionalUser.get();

                /*------------------------------------------------------------------+
                |   Checking for a valid session record for the user.               |
                +------------------------------------------------------------------*/
                boolean sessionIsValidated = sessionRepository.findByAccessToken(
                        authorizationResults.getAccessToken()).isPresent();

                if (login.isEnabled() && sessionIsValidated)
                {
                    if (login.isEnabled())
                    {
                        /*------------------------------------------------------+
                        |   If the user is still enabled and there is a valid   |
                        |   session present, set the user info into the context |
                        |   so the app can access it, if needed.				|
                        +------------------------------------------------------*/
                        UsernamePasswordAuthenticationToken authToken
                                = new UsernamePasswordAuthenticationToken(login, authorizationResults.getAccessToken(), login.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }
    }
}
