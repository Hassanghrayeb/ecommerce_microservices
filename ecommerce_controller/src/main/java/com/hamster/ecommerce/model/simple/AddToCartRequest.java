package com.hamster.ecommerce.model.simple;

import java.math.BigDecimal;

public class AddToCartRequest {
    private Long productId;
    //private String productName;
    //private BigDecimal unitPrice;
    //private String currency;
    private Integer quantity;

    public AddToCartRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

//    public String getProductName() { return productName; }
//    public void setProductName(String productName) { this.productName = productName; }
//
//    public BigDecimal getUnitPrice() { return unitPrice; }
//    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
//
//    public String getCurrency() { return currency; }
//    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
