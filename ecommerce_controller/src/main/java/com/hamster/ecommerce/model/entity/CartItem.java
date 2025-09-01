package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("cart_items")
public class CartItem extends Auditable {
    @Id
    private Long id;
    @Column("cart_id")
    private Long cartId;
    @Column("product_id")
    private Long productId;
    @Column("product_name")
    private String productName;
    @Column("unit_price")
    private BigDecimal unitPrice;
    @Column("currency")
    private String currency;
    @Column("quantity")
    private Integer quantity;

    public CartItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getCartId()
    {
        return cartId;
    }

    public void setCartId(Long cartId)
    {
        this.cartId = cartId;
    }
}
