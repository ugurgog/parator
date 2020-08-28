package com.paypad.parator.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String id;
    private String email;
    private String password;
    private Date createDate;
    private Date updateDate;
    private String storeName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int typeOfBusinessId;
    private int numberOfLocationsId;
    private int estimatedAnnTurnoverId;
    private int tradingCurrencyId;
    private String country;
    private String deviceToken;
    private boolean isLoggedIn;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getTypeOfBusinessId() {
        return typeOfBusinessId;
    }

    public void setTypeOfBusinessId(int typeOfBusinessId) {
        this.typeOfBusinessId = typeOfBusinessId;
    }

    public int getNumberOfLocationsId() {
        return numberOfLocationsId;
    }

    public void setNumberOfLocationsId(int numberOfLocationsId) {
        this.numberOfLocationsId = numberOfLocationsId;
    }

    public int getEstimatedAnnTurnoverId() {
        return estimatedAnnTurnoverId;
    }

    public void setEstimatedAnnTurnoverId(int estimatedAnnTurnoverId) {
        this.estimatedAnnTurnoverId = estimatedAnnTurnoverId;
    }

    public int getTradingCurrencyId() {
        return tradingCurrencyId;
    }

    public void setTradingCurrencyId(int tradingCurrencyId) {
        this.tradingCurrencyId = tradingCurrencyId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
