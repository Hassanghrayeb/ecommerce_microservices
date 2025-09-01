package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("order_items")
public class OrderItem extends Auditable {

    @Id
    private Long id;
    @Column("order_id")
    private Long orderId;
    @Column("product_id")
    private Long productId;
    @Column("product_name")
    private String productName;
    @Column("quantity")
    private Integer quantity;
    @Column("unit_price")
    private BigDecimal unitPrice;
    @Column("line_total")
    private BigDecimal lineTotal;
    @Column("currency")
    private String currency;

    public OrderItem()
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

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal()
    {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal)
    {
        this.lineTotal = lineTotal;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }
}
