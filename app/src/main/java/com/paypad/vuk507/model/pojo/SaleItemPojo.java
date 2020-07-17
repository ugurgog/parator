package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.List;

public class SaleItemPojo {

    private String uuid;
    private String saleUuid;
    private String name;
    private double amount;
    private double taxAmount;
    private double grossAmount;
    private int quantity;
    private String note;
    private List<DiscountPojo> discounts;
    private long productId;
    private boolean isDynamicAmount;
    private List<OrderItemTax> orderItemTaxes;
    private int colorId;
    private byte[] itemImage;
    private String categoryName;

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

    public List<DiscountPojo> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountPojo> discounts) {
        this.discounts = discounts;
    }

    public List<OrderItemTax> getOrderItemTaxes() {
        return orderItemTaxes;
    }

    public void setOrderItemTaxes(List<OrderItemTax> orderItemTaxes) {
        this.orderItemTaxes = orderItemTaxes;
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
}