package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("x_shared_secret")
public class XSharedSecret
{
    @Id
    @Column("id")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column("key")
    private String key;

    @Column("create_datetime")
    private LocalDateTime createDateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public LocalDateTime getCreateDateTime()
    {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime)
    {
        this.createDateTime = createDateTime;
    }
}
