package com.ecom.util;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum OrderStatus {

    IN_PROGRESS(1 , "In Progress"),
    ORDER_RECEIVED(2, "Order Received"),
    PRODUCT_PACKED(3,"Product Packed"),
    OUT_FOR_DELIVERY(4, "Out for Delivery"),
    DELIVERED(5,"Delivered"),
    CANCELLED(6, "Cancelled"),
    SUCCESS(7, "Success");

    private Integer id;
    @Setter
    private String name;

    OrderStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

}
