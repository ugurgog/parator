package com.paypad.parator.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Store extends RealmObject {

    @PrimaryKey
    private String id;
    @Index
    private String userId;
    private String storeName;
    private int typeOfBusiness;
    private int numberOfLocations;
    private int estimatedAnnTurnover;
    private int tradingCurrency;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public void setTypeOfBusiness(int typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    public int getNumberOfLocations() {
        return numberOfLocations;
    }

    public void setNumberOfLocations(int numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
    }

    public int getEstimatedAnnTurnover() {
        return estimatedAnnTurnover;
    }

    public void setEstimatedAnnTurnover(int estimatedAnnTurnover) {
        this.estimatedAnnTurnover = estimatedAnnTurnover;
    }

    public int getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(int tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
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
