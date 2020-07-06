package com.paypad.vuk507.model;

import com.paypad.vuk507.model.order.OrderItemTax;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleItem extends RealmObject {

    private String name;
    private double amount;
    private double amountIncludingTax;
    private int quantity;
    private String note;
    private RealmList<Discount> discounts;
    private long productId;
    private boolean isDynamicAmount;
    private String uuid;
    private String saleUuid;
    private RealmList<OrderItemTax> orderItemTaxes;


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

    public double getAmountIncludingTax() {
        return amountIncludingTax;
    }

    public void setAmountIncludingTax(double amountIncludingTax) {
        this.amountIncludingTax = amountIncludingTax;
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

    public RealmList<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(RealmList<Discount> discounts) {
        this.discounts = discounts;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isDynamicAmount() {
        return isDynamicAmount;
    }

    public void setDynamicAmount(boolean dynamicAmount) {
        isDynamicAmount = dynamicAmount;
    }

    public String getSaleUuid() {
        return saleUuid;
    }

    public void setSaleUuid(String saleUuid) {
        this.saleUuid = saleUuid;
    }

    public RealmList<OrderItemTax> getOrderItemTaxes() {
        return orderItemTaxes;
    }

    public void setOrderItemTaxes(RealmList<OrderItemTax> orderItemTaxes) {
        this.orderItemTaxes = orderItemTaxes;
    }
}
