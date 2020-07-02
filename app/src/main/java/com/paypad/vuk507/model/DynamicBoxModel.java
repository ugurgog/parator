package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmObject;

public class DynamicBoxModel extends RealmObject {

    private int structId;
    private long itemId;
    private Date createDate;
    private String createUserId;

    public int getStructId() {
        return structId;
    }

    public void setStructId(int structId) {
        this.structId = structId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
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
}
