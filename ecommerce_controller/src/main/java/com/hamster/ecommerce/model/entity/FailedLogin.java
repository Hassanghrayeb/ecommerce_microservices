package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("failed_login")
public class FailedLogin
{
    @Id
    @Column("login_id")
    private Long loginId;

    @Column("consecutive_fail_count")
    private Integer consecutiveFailCount;

    @Column("update_timestamp")
    private LocalDateTime updateTimestamp;

    public FailedLogin()
    {
    }

    public FailedLogin(Long loginId, Integer consecutiveFailCount)
    {
        this.loginId = loginId;
        this.consecutiveFailCount = consecutiveFailCount;
        updateTimestamp = LocalDateTime.now();
    }

    public void incrementConsecutiveFailedLoginCount()
    {
        consecutiveFailCount += 1;
    }

    public Long getLoginId()
    {
        return loginId;
    }

    public void setLoginId(Long loginId)
    {
        this.loginId = loginId;
    }

    public Integer getConsecutiveFailCount()
    {
        return consecutiveFailCount;
    }

    public void setConsecutiveFailCount(Integer consecutiveFailCount)
    {
        this.consecutiveFailCount = consecutiveFailCount;
    }

    public LocalDateTime getUpdateTimestamp()
    {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(LocalDateTime updateTimestamp)
    {
        this.updateTimestamp = updateTimestamp;
    }
}
