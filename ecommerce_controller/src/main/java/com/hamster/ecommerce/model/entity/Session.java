package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("")
public class Session
{
    @Column("create_datetime")
    protected LocalDateTime creationTime;
    @Id
    @Column("username")
    private String username;
    @Column("consecutive_refreshes")
    private int consecutiveRefreshes;
    @Column("access_token")
    private String accessToken;
    @Column("refresh_token")
    private String refreshToken;

    public void incrementConsecutiveRefreshes()
    {
        consecutiveRefreshes += 1;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public int getConsecutiveRefreshes()
    {
        return consecutiveRefreshes;
    }

    public void setConsecutiveRefreshes(int consecutiveRefreshes)
    {
        this.consecutiveRefreshes = consecutiveRefreshes;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime)
    {
        this.creationTime = creationTime;
    }
}