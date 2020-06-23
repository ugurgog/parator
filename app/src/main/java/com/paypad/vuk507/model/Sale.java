package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sale extends RealmObject {

    @PrimaryKey
    private long id;
    private RealmList<Long> saleIds;
    private long customerId;
    private RealmList<Long> discountIds;
    private double totalAmount;         // Split dahil edilmemis Total Amount
    private boolean paymentCompleted;
    private long paymentTypeId;         // (No Split)
    private double tipAmount;           // Kredi karti odemelerinde tip amount dolu olabilir (No Split)
    private double cashAmount;          // Cash odemelerde musterinin verdigi nakit tutari (No Split)
    private double changeAmount;        // Cash odemelerde musteriye verilen para ustu(No Split)
    private int splitCount;             // Split oldugunda bu alan dolu olacak
    private RealmList<Long> splitIds;   // Split oldugunda bu alan dolu olacak
    private Date createDate;
    private String userUuid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmList<Long> getSaleIds() {
        return saleIds;
    }

    public void setSaleIds(RealmList<Long> saleIds) {
        this.saleIds = saleIds;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public RealmList<Long> getDiscountIds() {
        return discountIds;
    }

    public void setDiscountIds(RealmList<Long> discountIds) {
        this.discountIds = discountIds;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }

    public void setPaymentCompleted(boolean paymentCompleted) {
        this.paymentCompleted = paymentCompleted;
    }

    public long getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public RealmList<Long> getSplitIds() {
        return splitIds;
    }

    public void setSplitIds(RealmList<Long> splitIds) {
        this.splitIds = splitIds;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
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
