package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Refund extends RealmObject {

    @PrimaryKey
    @Index
    private String id;
    @Index
    private String transactionId;
    @Index
    private String orderId;
    private boolean isRefundByAmount;
    private double refundAmount;
    private RealmList<OrderRefundItem> refundItems;
    private String refundReason;
    private boolean isSuccessful;
    private Date createDate;
    @Index
    private long zNum;
    private long fNum;
    private boolean isTransferred;
    private boolean isEODProcessed;
    private Date eodDate;

    public long getzNum() {
        return zNum;
    }

    public void setzNum(long zNum) {
        this.zNum = zNum;
    }

    public long getfNum() {
        return fNum;
    }

    public void setfNum(long fNum) {
        this.fNum = fNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isRefundByAmount() {
        return isRefundByAmount;
    }

    public void setRefundByAmount(boolean refundByAmount) {
        isRefundByAmount = refundByAmount;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public RealmList<OrderRefundItem> getRefundItems() {
        return refundItems;
    }

    public void setRefundItems(RealmList<OrderRefundItem> refundItems) {
        this.refundItems = refundItems;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean transferred) {
        isTransferred = transferred;
    }

    public boolean isEODProcessed() {
        return isEODProcessed;
    }

    public void setEODProcessed(boolean EODProcessed) {
        isEODProcessed = EODProcessed;
    }

    public Date getEodDate() {
        return eodDate;
    }

    public void setEodDate(Date eodDate) {
        this.eodDate = eodDate;
    }
}
