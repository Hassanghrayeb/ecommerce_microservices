package com.hamster.ecommerce.model.entity;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public abstract class Auditable
{
    @Column("update_login_id")
    private Long updateLoginId;

    @Column("update_timestamp")
    private LocalDateTime updateTimestamp;

    public Long getUpdateLoginId()
    {
        return updateLoginId;
    }

    public void setUpdateLoginId(Long updateLoginId)
    {
        this.updateLoginId = updateLoginId;
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
