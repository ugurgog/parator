package com.paypad.parator.model.pojo;

import java.util.Date;

public class PrintRefundCancelModel {

    private String merchantName;
    private String firmName;
    private String address;
    private String phoneNumber;
    private String taxOffice;
    private String taxNumber;

    private Date receiptDate;
    private double refundAmount;
    private long fNum;
    private long zNum;
    private String orderNum;

    private int chequeNo;
    private int tableNo;
    private String mersisNo;
    private String email;

    private String merchantNum;
    private String terminalNum;
    private int approveCode;
    private int stanNum;
    private int batchNum;
    private String deviceRegisterId;
    private Date fDate;

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public long getfNum() {
        return fNum;
    }

    public void setfNum(long fNum) {
        this.fNum = fNum;
    }

    public long getzNum() {
        return zNum;
    }

    public void setzNum(long zNum) {
        this.zNum = zNum;
    }

    public int getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(int chequeNo) {
        this.chequeNo = chequeNo;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public String getMersisNo() {
        return mersisNo;
    }

    public void setMersisNo(String mersisNo) {
        this.mersisNo = mersisNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(String merchantNum) {
        this.merchantNum = merchantNum;
    }

    public String getTerminalNum() {
        return terminalNum;
    }

    public void setTerminalNum(String terminalNum) {
        this.terminalNum = terminalNum;
    }

    public int getApproveCode() {
        return approveCode;
    }

    public void setApproveCode(int approveCode) {
        this.approveCode = approveCode;
    }

    public int getStanNum() {
        return stanNum;
    }

    public void setStanNum(int stanNum) {
        this.stanNum = stanNum;
    }

    public String getDeviceRegisterId() {
        return deviceRegisterId;
    }

    public void setDeviceRegisterId(String deviceRegisterId) {
        this.deviceRegisterId = deviceRegisterId;
    }

    public Date getfDate() {
        return fDate;
    }

    public void setfDate(Date fDate) {
        this.fDate = fDate;
    }
}
