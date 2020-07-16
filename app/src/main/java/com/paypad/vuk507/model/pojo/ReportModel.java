package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.model.Discount;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportModel {

    //Sales overview
    private Date startDate;
    private Date endDate;
    private double grossSalesAmount;
    private double refundsAmount;
    private double discountAmount;
    private Map<Long, ReportDiscountModel> discounts;
    private double netSalesAmount;
    private double taxAmount;
    private double tipsAmount;
    private double refundsByAmount;
    private double totalAmount;
    private long saleCount;
    private double averageSaleAmount;

    //Sales by payment types
    private double cashAmount;
    private double cardAmount;
    private double feeAmount;


    //private List<ReportOrderItem> reportOrderItems;


    private Map<String, List<ReportOrderItem>> reportOrderItems;
    private List<ReportOrderItem> topItems;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getGrossSalesAmount() {
        return grossSalesAmount;
    }

    public void setGrossSalesAmount(double grossSalesAmount) {
        this.grossSalesAmount = grossSalesAmount;
    }

    public double getRefundsAmount() {
        return refundsAmount;
    }

    public void setRefundsAmount(double refundsAmount) {
        this.refundsAmount = refundsAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Map<Long, ReportDiscountModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Map<Long, ReportDiscountModel> discounts) {
        this.discounts = discounts;
    }

    public double getNetSalesAmount() {
        return netSalesAmount;
    }

    public void setNetSalesAmount(double netSalesAmount) {
        this.netSalesAmount = netSalesAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTipsAmount() {
        return tipsAmount;
    }

    public void setTipsAmount(double tipsAmount) {
        this.tipsAmount = tipsAmount;
    }

    public double getRefundsByAmount() {
        return refundsByAmount;
    }

    public void setRefundsByAmount(double refundsByAmount) {
        this.refundsByAmount = refundsByAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(long saleCount) {
        this.saleCount = saleCount;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public double getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(double cardAmount) {
        this.cardAmount = cardAmount;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Map<String, List<ReportOrderItem>> getReportOrderItems() {
        return reportOrderItems;
    }

    public void setReportOrderItems(Map<String, List<ReportOrderItem>> reportOrderItems) {
        this.reportOrderItems = reportOrderItems;
    }

    public double getAverageSaleAmount() {
        return averageSaleAmount;
    }

    public void setAverageSaleAmount(double averageSaleAmount) {
        this.averageSaleAmount = averageSaleAmount;
    }

    public List<ReportOrderItem> getTopItems() {
        return topItems;
    }

    public void setTopItems(List<ReportOrderItem> topItems) {
        this.topItems = topItems;
    }
}
