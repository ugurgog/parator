package com.paypad.vuk507.model.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportOrderItem implements Parcelable {

    private long productId;
    private String itemName;
    private double grossAmount;
    private long saleCount;

    public ReportOrderItem() {
    }

    protected ReportOrderItem(Parcel in) {
        productId = in.readLong();
        itemName = in.readString();
        grossAmount = in.readDouble();
        saleCount = in.readLong();
    }

    public static final Creator<ReportOrderItem> CREATOR = new Creator<ReportOrderItem>() {
        @Override
        public ReportOrderItem createFromParcel(Parcel in) {
            return new ReportOrderItem(in);
        }

        @Override
        public ReportOrderItem[] newArray(int size) {
            return new ReportOrderItem[size];
        }
    };

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(long saleCount) {
        this.saleCount = saleCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(productId);
        parcel.writeString(itemName);
        parcel.writeDouble(grossAmount);
        parcel.writeLong(saleCount);
    }
}
