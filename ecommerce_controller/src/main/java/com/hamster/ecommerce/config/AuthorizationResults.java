package com.hamster.ecommerce.config;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;

public class AuthorizationResults
{
    private final String accessToken;
    private JWSObject jwsObject;
    private JWTClaimsSet jwtClaimsSet;
    private boolean tokenIsValid;
    private boolean jwtClaimsAreValid;

    public AuthorizationResults(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public JWSObject getJwsObject()
    {
        return jwsObject;
    }

    public void setJwsObject(JWSObject jwsObject)
    {
        this.jwsObject = jwsObject;
    }

    public JWTClaimsSet getJwtClaimsSet()
    {
        return jwtClaimsSet;
    }

    public void setJwtClaimsSet(JWTClaimsSet jwtClaimsSet)
    {
        this.jwtClaimsSet = jwtClaimsSet;
    }

    public boolean getTokenIsValid()
    {
        return tokenIsValid;
    }

    public void setTokenIsValid(boolean tokenIsValid)
    {
        this.tokenIsValid = tokenIsValid;
    }

    public boolean getJwtClaimsAreValid()
    {
        return jwtClaimsAreValid;
    }

    public void setJwtClaimsAreValid(boolean jwtClaimsAreValid)
    {
        this.jwtClaimsAreValid = jwtClaimsAreValid;
    }

    public boolean tokenAndClaimsAreValid()
    {
        return tokenIsValid && jwtClaimsAreValid;
    }
}
