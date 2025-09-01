package com.hamster.ecommerce.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.hamster.ecommerce.config.AppProperties;
import com.hamster.ecommerce.model.entity.XSharedSecret;
import com.hamster.ecommerce.service.XSharedSecretService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class SharedSecretUtil
{
    private static final Logger log = LoggerFactory.getLogger(SharedSecretUtil.class);
    private static JWSSigner jwsSigner;

    private final AppProperties appProperties;
    private final XSharedSecretService xSharedSecretService;

    public SharedSecretUtil(AppProperties appProperties, XSharedSecretService xSharedSecretService)
    {
        this.appProperties = appProperties;
        this.xSharedSecretService = xSharedSecretService;
    }

    public JWSSigner getJWSSigner()
    {
        return jwsSigner;
    }

    @PostConstruct
    private void processXSharedSecretOnStartup()
    {
        XSharedSecret xSharedSecret = rotateSharedSecretIfNecessary();
        rebuildJWSSigner(xSharedSecret);
    }

    //Run once / day at 3am
    @Scheduled(cron = "0 0 3 ? * *")
    protected XSharedSecret rotateSharedSecretIfNecessary()
    {
        /*----------------------------------------------------------------------+
		|	Get the most recent key.  If it's more than one day old, we need	|
		|	to generate a new one and insert it.  We must make sure to keep any	|
		|	existing one because a user may have logged in with the prior key.	|
		+----------------------------------------------------------------------*/
        XSharedSecret currentSharedSecret = xSharedSecretService.getMostRecentSharedSecret();

        if (currentSharedSecret == null || currentSharedSecret.getCreateDateTime()
                .plusDays(appProperties.getSharedSecretRotationFrequencyInDays())
                .isBefore(LocalDateTime.now()))
        {
            currentSharedSecret = generateAndSaveNewSecretKey();
        }
        log.info("==> rotateSharedSecretIfNecessary ran");
        return currentSharedSecret;
    }

    private XSharedSecret generateAndSaveNewSecretKey()
    {
        String secretKey = UUID.randomUUID().toString();
        XSharedSecret xSharedSecret = new XSharedSecret();
        xSharedSecret.setKey(secretKey);
        xSharedSecret.setCreateDateTime(LocalDateTime.now());

        return xSharedSecretService.save(xSharedSecret);
    }

    public void rebuildJWSSigner(XSharedSecret xSharedSecret)
    {
        try
        {
            //Create or update the HMAC signer.  Every token will be signed with this.
            jwsSigner = new MACSigner(xSharedSecret.getKey());
        }
        catch (JOSEException e)
        {
            log.error("JOSEException", e);
        }
    }
}
