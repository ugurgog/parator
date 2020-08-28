package com.paypad.parator.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class CustomerGroup extends RealmObject {

    @PrimaryKey
    private String id;
    @Index
    private long customerId;
    @Index
    private long groupId;
    private Date createDate;
    @Index
    private String userId;
    private Date updateDateDate;
    private String updateUserId;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public Date getUpdateDateDate() {
        return updateDateDate;
    }

    public void setUpdateDateDate(Date updateDateDate) {
        this.updateDateDate = updateDateDate;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
