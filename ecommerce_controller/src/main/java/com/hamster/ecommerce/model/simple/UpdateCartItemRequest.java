package com.hamster.ecommerce.model.simple;

public class UpdateCartItemRequest {
    private Integer quantity;

    public UpdateCartItemRequest() {}

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}