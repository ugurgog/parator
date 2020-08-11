package com.paypad.vuk507.model;

import com.paypad.vuk507.utils.CommonUtils;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleItem extends RealmObject {

    @PrimaryKey
    private String uuid;
    private String saleUuid;
    private String name;
    private double amount;              // Item Amount = 10
    private double taxAmount;           // Item Amount - Gross Amount = 10 -  9.26 = 0.74
    private double grossAmount;         // Item Amount - Tax Amount = 10 - 0.74 = 9.26
    private double totalDiscountAmount;
    private int quantity;
    private String note;
    private RealmList<OrderItemDiscount> discounts;
    private long productId;
    private boolean isDynamicAmount;
    private OrderItemTax taxModel;
    private int colorId;
    private byte[] itemImage;
    private String categoryName;
    private boolean isTransferred;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return CommonUtils.round(amount, 2);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTaxAmount() {
        return CommonUtils.round(taxAmount, 2);
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isDynamicAmount() {
        return isDynamicAmount;
    }

    public void setDynamicAmount(boolean dynamicAmount) {
        isDynamicAmount = dynamicAmount;
    }

    public String getSaleUuid() {
        return saleUuid;
    }

    public void setSaleUuid(String saleUuid) {
        this.saleUuid = saleUuid;
    }

    public RealmList<OrderItemDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(RealmList<OrderItemDiscount> discounts) {
        this.discounts = discounts;
    }

    public OrderItemTax getTaxModel() {
        return taxModel;
    }

    public void setTaxModel(OrderItemTax taxModel) {
        this.taxModel = taxModel;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public byte[] getItemImage() {
        return itemImage;
    }

    public void setItemImage(byte[] itemImage) {
        this.itemImage = itemImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isTransferred() {
        return isTransferred;
    }

    public void setTransferred(boolean transferred) {
        isTransferred = transferred;
    }

    public double getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }
}
