package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmObject;

public class DynamicBoxModel extends RealmObject {

    private int structId;
    private long itemId;
    private String itemName;
    private Date createDate;
    private String userUuid;


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

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
