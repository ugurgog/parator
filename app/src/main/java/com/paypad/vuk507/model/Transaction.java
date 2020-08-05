package com.paypad.vuk507.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmModule;
import io.realm.annotations.RealmNamingPolicy;

public class Transaction extends RealmObject {

    @Index
    @PrimaryKey
    private String transactionUuid;
    @Index
    private String saleUuid;
    private String retrefNum;
    private int transactionType;
    private long seqNumber;                 // Sequence number
    private double transactionAmount;       // Split amount tutari
    private double tipAmount;               // Tip Amount - Kredi karti odemelerinde
    private double totalAmount;             // Split + Tip Amount
    private long paymentTypeId;             // Cash/Kredi Karti/Other
    private double cashAmount;              // Cash odemelerde verilen tutar
    private double changeAmount;            // Cash odemelerde verilen para ustu
    private boolean paymentCompleted;
    private String cardNumber;
    private String cardExpireDate;
    private boolean isMailSend;
    private String mailAdress;
    private Date mailSendDate;
    private Date createDate;
    private Date refundOrCancelDate;
    @Index
    private String userUuid;

    public String getTransactionUuid() {
        return transactionUuid;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public String getRetrefNum() {
        return retrefNum;
    }

    public void setRetrefNum(String retrefNum) {
        this.retrefNum = retrefNum;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public void setTransactionUuid(String transactionUuid) {
        this.transactionUuid = transactionUuid;
    }

    public Date getRefundOrCancelDate() {
        return refundOrCancelDate;
    }

    public void setRefundOrCancelDate(Date refundOrCancelDate) {
        this.refundOrCancelDate = refundOrCancelDate;
    }

    public String getSaleUuid() {
        return saleUuid;
    }

    public void setSaleUuid(String saleUuid) {
        this.saleUuid = saleUuid;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }

    public void setPaymentCompleted(boolean paymentCompleted) {
        this.paymentCompleted = paymentCompleted;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(String cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
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

    public long getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }

    public boolean isMailSend() {
        return isMailSend;
    }

    public void setMailSend(boolean mailSend) {
        isMailSend = mailSend;
    }

    public String getMailAdress() {
        return mailAdress;
    }

    public void setMailAdress(String mailAdress) {
        this.mailAdress = mailAdress;
    }

    public Date getMailSendDate() {
        return mailSendDate;
    }

    public void setMailSendDate(Date mailSendDate) {
        this.mailSendDate = mailSendDate;
    }
}
