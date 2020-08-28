package com.paypad.parator.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Transaction extends RealmObject {

    @Index
    @PrimaryKey
    private String id;
    @Index
    private String orderId;
    private int transactionType;
    private long seqNumber;                 // Sequence number
    private double transactionAmount;       // Split amount tutari
    private double tipAmount;               // Tip Amount - Kredi karti odemelerinde
    private double totalAmount;             // Split + Tip Amount
    private long paymentTypeId;             // Cash/Kredi Karti/Other
    private double cashAmount;              // Cash odemelerde verilen tutar
    private double changeAmount;            // Cash odemelerde verilen para ustu
    private double totalRefundAmount;       // Toplam Iade edilen tutar
    private boolean paymentCompleted;
    private String cardNumber;
    private String cardExpireDate;
    private boolean isMailSend;
    private String mailAdress;
    private Date mailSendDate;
    private Date createDate;
    private Date cancellationDate;
    @Index
    private String userId;
    @Index
    private long zNum;
    private long fNum;
    private boolean isTransferred;
    private boolean isEODProcessed;
    private Date eodDate;

    public long getzNum() {
        return zNum;
    }

    public void setzNum(long zNum) {
        this.zNum = zNum;
    }

    public long getfNum() {
        return fNum;
    }

    public void setfNum(long fNum) {
        this.fNum = fNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public double getTotalRefundAmount() {
        return totalRefundAmount;
    }

    public void setTotalRefundAmount(double totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean transferred) {
        isTransferred = transferred;
    }

    public boolean isEODProcessed() {
        return isEODProcessed;
    }

    public void setEODProcessed(boolean EODProcessed) {
        isEODProcessed = EODProcessed;
    }

    public Date getEodDate() {
        return eodDate;
    }

    public void setEodDate(Date eodDate) {
        this.eodDate = eodDate;
    }
}
