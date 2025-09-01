package com.hamster.ecommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${shared-secret-rotation-frequency-in-days:1}")
    public Integer sharedSecretRotationFrequencyInDays;

    @Value("${access-token-lifespan-in-minutes:10}")
    public Integer accessTokenLifespanInMinutes;

    @Value("${refresh-token-lifespan-in-minutes:30}")
    public Integer refreshTokenLifespanInMinutes;

    @Value("${max-consecutive-refreshes-allowed:6}")
    public Integer maxConsecutiveRefreshesAllowed;


    public Integer getSharedSecretRotationFrequencyInDays()
    {
        return sharedSecretRotationFrequencyInDays;
    }

    public Integer getAccessTokenLifespanInMinutes()
    {
        return accessTokenLifespanInMinutes;
    }

    public Integer getRefreshTokenLifespanInMinutes()
    {
        return refreshTokenLifespanInMinutes;
    }

    public Integer getMaxConsecutiveRefreshesAllowed()
    {
        return maxConsecutiveRefreshesAllowed;
    }

}
