package com.paypad.vuk507.model;

import io.realm.RealmObject;

public class AmountModel extends RealmObject {

    private double baseAmpount;
    private int taxRate;
    private double totalAmount;

    public double getBaseAmpount() {
        return baseAmpount;
    }

    public void setBaseAmpount(double baseAmpount) {
        this.baseAmpount = baseAmpount;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
