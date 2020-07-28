package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class PrinterSettings extends RealmObject {

    @Index
    @PrimaryKey
    private String userId;
    private boolean isOrdersAutoPrint;
    private Date createDate;
    private String createUserId;
    private Date updateDate;
    private String updateUserId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isOrdersAutoPrint() {
        return isOrdersAutoPrint;
    }

    public void setOrdersAutoPrint(boolean ordersAutoPrint) {
        isOrdersAutoPrint = ordersAutoPrint;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
