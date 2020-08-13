package com.paypad.vuk507.model;

import com.paypad.vuk507.utils.CommonUtils;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Sale extends RealmObject {

    @PrimaryKey
    @Index
    private String saleUuid;
    private int totalItemCount;
    private long customerId;
    private RealmList<OrderItemDiscount> discounts;
    private double totalAmount;            // Split dahil edilmemis Total Amount
    private double discountedAmount;         // Indirimlerin uygulanmis tutarin son hali
    private double totalDiscountAmount;        // Uygulanan toplam indirim tutari
    private boolean paymentCompleted;
    private double remainAmount;           // Parcali odeme sonrasi kalan tutar
    private Date createDate;
    private String userUuid;
    @Index
    private String deviceId;
    private String orderNum;              // julian date + year son 2 hane + 6 hane counter (25920000025) (259. gun gelen 25. i order)
    @Index
    private long zNum;
    private Double latitude;
    private Double longitude;
    private boolean isTransferred;

    public long getzNum() {
        return zNum;
    }

    public void setzNum(long zNum) {
        this.zNum = zNum;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public double getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public RealmList<OrderItemDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(RealmList<OrderItemDiscount> discounts) {
        this.discounts = discounts;
    }

    public double getTotalAmount() {
        return CommonUtils.round(totalAmount, 2);
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }

    public void setPaymentCompleted(boolean paymentCompleted) {
        this.paymentCompleted = paymentCompleted;
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

    public double getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(double discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public double getRemainAmount() {
        return CommonUtils.round(remainAmount, 2);
    }

    public void setRemainAmount(double remainAmount) {
        this.remainAmount = remainAmount;
    }

    public String getSaleUuid() {
        return saleUuid;
    }

    public void setSaleUuid(String saleUuid) {
        this.saleUuid = saleUuid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean transferred) {
        isTransferred = transferred;
    }
}
