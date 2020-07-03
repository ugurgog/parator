package com.paypad.vuk507.model.order;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class OrderItem extends RealmObject {

    private String orderId;
    private String orderItemId;
    private double amount;
    private int quantity;
    private String note;
    private long productId;
    private boolean isDynamicAmount;
    private RealmList<OrderItemDiscount> orderItemDiscounts;
    private RealmList<OrderItemTax> orderItemTaxes;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public boolean isDynamicAmount() {
        return isDynamicAmount;
    }

    public void setDynamicAmount(boolean dynamicAmount) {
        isDynamicAmount = dynamicAmount;
    }

    public RealmList<OrderItemDiscount> getOrderItemDiscounts() {
        return orderItemDiscounts;
    }

    public void setOrderItemDiscounts(RealmList<OrderItemDiscount> orderItemDiscounts) {
        this.orderItemDiscounts = orderItemDiscounts;
    }

    public RealmList<OrderItemTax> getOrderItemTaxes() {
        return orderItemTaxes;
    }

    public void setOrderItemTaxes(RealmList<OrderItemTax> orderItemTaxes) {
        this.orderItemTaxes = orderItemTaxes;
    }
}
