package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class CustomerGroup extends RealmObject {

    private long customerId;
    private RealmList<Group> groups;
    private Date createDate;
    private String userUuid;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public RealmList<Group> getGroups() {
        return groups;
    }

    public void setGroups(RealmList<Group> groups) {
        this.groups = groups;
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
}
