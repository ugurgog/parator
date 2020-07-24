package com.paypad.vuk507.model.pojo;

public class PaymentDetailModel {

    private int itemId;
    private String itemType;
    private int drawableId;
    private String itemName;
    private String itemDesc;
    private boolean isDescBold;
    private double amount;

    public int getItemId() {
        return itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public boolean isDescBold() {
        return isDescBold;
    }

    public void setDescBold(boolean descBold) {
        isDescBold = descBold;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
