package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.SaleItem;

import java.util.Date;
import java.util.List;

public class PrintReceiptModel {

    private String receiptId;
    private Date receiptDate;
    private double receiptAmount;
    private List<SaleItemPojo> saleItemPojos;
    private List<ReportDiscountModel> discounts;
    private double purchaseSubTotalAmount;
    private double totalAmount;
    private double subTotalAmount;                  //
    private double tipAmount;                       //
    private PaymentTypeEnum paymentType;            //
    private double taxAmount;

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public double getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public List<SaleItemPojo> getSaleItemPojos() {
        return saleItemPojos;
    }

    public void setSaleItemPojos(List<SaleItemPojo> saleItemPojos) {
        this.saleItemPojos = saleItemPojos;
    }

    public List<ReportDiscountModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<ReportDiscountModel> discounts) {
        this.discounts = discounts;
    }

    public double getPurchaseSubTotalAmount() {
        return purchaseSubTotalAmount;
    }

    public void setPurchaseSubTotalAmount(double purchaseSubTotalAmount) {
        this.purchaseSubTotalAmount = purchaseSubTotalAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getSubTotalAmount() {
        return subTotalAmount;
    }

    public void setSubTotalAmount(double subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public PaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }
}
