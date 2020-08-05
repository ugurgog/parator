package com.paypad.vuk507.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AutoIncrement extends RealmObject {

    @PrimaryKey
    private String userId;
    private long batchNum;
    private long receiptNum;
    private long retrefNumCounter;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRetrefNumCounter() {
        return retrefNumCounter;
    }

    public void setRetrefNumCounter(long retrefNumCounter) {
        this.retrefNumCounter = retrefNumCounter;
    }
}
