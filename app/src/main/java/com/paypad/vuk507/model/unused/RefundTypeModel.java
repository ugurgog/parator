package com.paypad.vuk507.model.unused;

import io.realm.RealmObject;

public class RefundTypeModel {

    private String transactionId;
    private double refundAmount;
    private double refundTipAmount;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public double getRefundTipAmount() {
        return refundTipAmount;
    }

    public void setRefundTipAmount(double refundTipAmount) {
        this.refundTipAmount = refundTipAmount;
    }
}
