package com.paypad.vuk507.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleItem extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private double amount;
    private int quantity;
    private String note;
    private RealmList<Long> discountIds;
    private long productId;
    private long taxRate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public RealmList<Long> getDiscountIds() {
        return discountIds;
    }

    public void setDiscountIds(RealmList<Long> discountIds) {
        this.discountIds = discountIds;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(long taxRate) {
        this.taxRate = taxRate;
    }
}
