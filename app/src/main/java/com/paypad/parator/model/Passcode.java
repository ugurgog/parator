package com.paypad.parator.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Passcode extends RealmObject {

    @PrimaryKey
    @Index
    private String id;
    @Index
    private String userId;
    private boolean isEnabled;
    private String passcodeVal;
    private boolean isAfterEachSaleEnabled;
    private boolean isBackOutOfSaleEnabled;
    private int timeOutId;
    private Date createDate;
    private Date updateDate;
    private String updateUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getPasscodeVal() {
        return passcodeVal;
    }

    public void setPasscodeVal(String passcodeVal) {
        this.passcodeVal = passcodeVal;
    }

    public boolean isAfterEachSaleEnabled() {
        return isAfterEachSaleEnabled;
    }

    public void setAfterEachSaleEnabled(boolean afterEachSaleEnabled) {
        isAfterEachSaleEnabled = afterEachSaleEnabled;
    }

    public boolean isBackOutOfSaleEnabled() {
        return isBackOutOfSaleEnabled;
    }

    public void setBackOutOfSaleEnabled(boolean backOutOfSaleEnabled) {
        isBackOutOfSaleEnabled = backOutOfSaleEnabled;
    }

    public int getTimeOutId() {
        return timeOutId;
    }

    public void setTimeOutId(int timeOutId) {
        this.timeOutId = timeOutId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
