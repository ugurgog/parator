package com.paypad.vuk507.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AutoIncrement extends RealmObject {

    @PrimaryKey
    private String userId;
    private long zNum;
    private long fNum;
    private long orderNumCounter;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public long getOrderNumCounter() {
        return orderNumCounter;
    }

    public void setOrderNumCounter(long orderNumCounter) {
        this.orderNumCounter = orderNumCounter;
    }
}
