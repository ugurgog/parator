package com.paypad.vuk507.model;

import com.paypad.vuk507.utils.CommonUtils;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Sale extends RealmObject {

    @PrimaryKey
    @Index
    private String saleUuid;
    private int saleCount;
    private long customerId;
    private RealmList<Discount> discounts;
    private double totalAmount;         // Split dahil edilmemis Total Amount
    private double subTotalAmount;    // Indirimlerin uygulanmis tutarin son hali
    private boolean paymentCompleted;
    private double remainAmount;          // Parcali odeme sonrasi kalan tutar
    private Date createDate;
    private String userUuid;
    @Index
    private String deviceId;
    private boolean isEndOfDayProcessed;
    private long batchNum;
    private long receiptNum;

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }


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
        return CommonUtils.round(totalAmount, 2);
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

    public double getSubTotalAmount() {
        return subTotalAmount;
    }

    public void setSubTotalAmount(double subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public double getRemainAmount() {
        return CommonUtils.round(remainAmount, 2);
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isEndOfDayProcessed() {
        return isEndOfDayProcessed;
    }

    public void setEndOfDayProcessed(boolean endOfDayProcessed) {
        isEndOfDayProcessed = endOfDayProcessed;
    }

    public long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(long batchNum) {
        this.batchNum = batchNum;
    }

    public long getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(long receiptNum) {
        this.receiptNum = receiptNum;
    }
}
