package com.paypad.vuk507.menu.transactions.model;

import com.paypad.vuk507.model.Transaction;

import io.realm.RealmObject;

public class RefundableTrxModel extends RealmObject {

    private Transaction transaction;
    private double maxRefundAmount;
    private double refundAmount;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public double getMaxRefundAmount() {
        return maxRefundAmount;
    }

    public void setMaxRefundAmount(double maxRefundAmount) {
        this.maxRefundAmount = maxRefundAmount;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }
}
