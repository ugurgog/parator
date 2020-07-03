package com.paypad.vuk507.model.order;

import io.realm.RealmObject;

public class OrderItemDiscount extends RealmObject {

    private String orderItemDiscountId;
    private String orderItemId;
    private long discountId;
    private String name;
    private double rate;
    private double amount;

    public String getOrderItemDiscountId() {
        return orderItemDiscountId;
    }

    public void setOrderItemDiscountId(String orderItemDiscountId) {
        this.orderItemDiscountId = orderItemDiscountId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(long discountId) {
        this.discountId = discountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
