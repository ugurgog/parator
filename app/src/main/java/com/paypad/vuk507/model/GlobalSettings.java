package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class GlobalSettings extends RealmObject {

    @Index
    @PrimaryKey
    private String userId;
    private boolean isCustomerAutoPrint;
    private boolean isMerchantAutoPrint;
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

    public boolean isCustomerAutoPrint() {
        return isCustomerAutoPrint;
    }

    public void setCustomerAutoPrint(boolean customerAutoPrint) {
        isCustomerAutoPrint = customerAutoPrint;
    }

    public boolean isMerchantAutoPrint() {
        return isMerchantAutoPrint;
    }

    public void setMerchantAutoPrint(boolean merchantAutoPrint) {
        isMerchantAutoPrint = merchantAutoPrint;
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
