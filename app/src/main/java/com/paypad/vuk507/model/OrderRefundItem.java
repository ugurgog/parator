package com.paypad.vuk507.model;

import com.paypad.vuk507.utils.CommonUtils;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class OrderRefundItem extends RealmObject {

    @Index
    @PrimaryKey
    private String id;
    @Index
    private String orderItemId;
    @Index
    private String orderId;
    @Index
    private String refundGroupId;
    private String name;
    private double amount;              // Item Amount = 10
    private double taxAmount;           // Item Amount - Gross Amount = 10 -  9.26 = 0.74
    private double grossAmount;         // Item Amount - Tax Amount = 10 - 0.74 = 9.26
    private String note;
    private RealmList<OrderItemDiscount> discounts;
    private long productId;
    private boolean isDynamicAmount;
    private OrderItemTax orderItemTax;
    private int colorId;
    private byte[] itemImage;
    private String categoryName;
    private boolean isTransferred;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefundGroupId() {
        return refundGroupId;
    }

    public void setRefundGroupId(String refundGroupId) {
        this.refundGroupId = refundGroupId;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RealmList<OrderItemDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(RealmList<OrderItemDiscount> discounts) {
        this.discounts = discounts;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public boolean isDynamicAmount() {
        return isDynamicAmount;
    }

    public void setDynamicAmount(boolean dynamicAmount) {
        isDynamicAmount = dynamicAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderItemTax getOrderItemTax() {
        return orderItemTax;
    }

    public void setOrderItemTax(OrderItemTax orderItemTax) {
        this.orderItemTax = orderItemTax;
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
}
