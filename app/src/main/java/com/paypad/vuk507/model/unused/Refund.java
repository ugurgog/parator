package com.paypad.vuk507.model.unused;

import com.paypad.vuk507.model.SaleItem;

import java.util.Date;
import java.util.List;

public class Refund {

    private String saleId;
    private String refundId;
    private String refundReason;
    private List<SaleItem> saleItems;
    private Date refundDate;
    private boolean isRefundedByAmount;
    private List<RefundTypeModel> refundTypes;
    private boolean isCompleted;

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public boolean isRefundedByAmount() {
        return isRefundedByAmount;
    }

    public void setRefundedByAmount(boolean refundedByAmount) {
        isRefundedByAmount = refundedByAmount;
    }

    public List<RefundTypeModel> getRefundTypes() {
        return refundTypes;
    }

    public void setRefundTypes(List<RefundTypeModel> refundTypes) {
        this.refundTypes = refundTypes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
