package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sale extends RealmObject {

    //@PrimaryKey
    //private long id;
    private String saleUuid;
    private int saleCount;
    //private RealmList<String> saleIds;
    private long customerId;
    private RealmList<Discount> discounts;
    private double totalAmount;         // Split dahil edilmemis Total Amount
    private double discountedAmount;    // Indirimlerin uygulanmis tutarin son hali
    private boolean paymentCompleted;
    //private long paymentTypeId;         // (No Split)
    //private double tipAmount;           // Kredi karti odemelerinde tip amount dolu olabilir (No Split)
    //private double cashAmount;          // Cash odemelerde musterinin verdigi nakit tutari (No Split)
    //private double changeAmount;        // Cash odemelerde musteriye verilen para ustu(No Split)
    //private int splitCount;             // Split oldugunda bu alan dolu olacak
    //private RealmList<Split> splits;    // Split oldugunda bu alan dolu olacak
    private double remainAmount;          // Parcali odeme sonrasi kalan tutar
    private Date createDate;
    private String userUuid;


    /*public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }*/

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    /*public RealmList<String> getSaleIds() {
        return saleIds;
    }

    public void setSaleIds(RealmList<String> saleIds) {
        this.saleIds = saleIds;
    }*/

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public RealmList<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(RealmList<Discount> discounts) {
        this.discounts = discounts;
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

    /*public long getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }*/

    /*public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public RealmList<Split> getSplits() {
        return splits;
    }

    public void setSplits(RealmList<Split> splits) {
        this.splits = splits;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }*/

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

    /*public double getCashAmount() {
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
    }*/

    public double getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(double discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public double getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(double remainAmount) {
        this.remainAmount = remainAmount;
    }

    public String getSaleUuid() {
        return saleUuid;
    }

    public void setSaleUuid(String saleUuid) {
        this.saleUuid = saleUuid;
    }
}
