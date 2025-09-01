package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Table("carts")
public class Cart extends Auditable {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Transient
    private List<CartItem> items = new ArrayList<>();

    public Cart()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public List<CartItem> getItems()
    {
        return items;
    }

    public void setItems(List<CartItem> items)
    {
        this.items = items;
    }
}
