package com.paypad.vuk507.model.order;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderItemTax extends RealmObject {

    private String orderItemTaxId;
    long taxId;
    private String name;
    private double taxRate;

    public String getOrderItemTaxId() {
        return orderItemTaxId;
    }

    public void setOrderItemTaxId(String orderItemTaxId) {
        this.orderItemTaxId = orderItemTaxId;
    }

    public long getTaxId() {
        return taxId;
    }

    public void setTaxId(long taxId) {
        this.taxId = taxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
}