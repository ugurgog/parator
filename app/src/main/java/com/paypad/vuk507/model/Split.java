package com.paypad.vuk507.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Split extends RealmObject {

    @PrimaryKey
    private long id;
    private double splitAmount;     // Split amount tutari
    private double tipAmount;       // Tip Amount
    private double totalAmount;     // Split + Tip Amount
    private long paymentTypeId;     // Cash/Kredi Karti/Other
    private double cashAmount;      // Cash odemelerde verilen tutar
    private double changeAmount;    // Cash odemelerde verilen para ustu

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(double splitAmount) {
        this.splitAmount = splitAmount;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }
}
