package com.paypad.vuk507.menu.reports.saleReport;

import com.paypad.vuk507.model.pojo.ReportOrderItem;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class TopCategory extends ExpandableGroup<ReportOrderItem> {

    private double totalGrossAmount;
    private long totalSaleCount;

    public TopCategory(String name, List<ReportOrderItem> items) {
        super(name, items);
    }

    public double getTotalGrossAmount() {
        return totalGrossAmount;
    }

    public void setTotalGrossAmount(double totalGrossAmount) {
        this.totalGrossAmount = totalGrossAmount;
    }

    public long getTotalSaleCount() {
        return totalSaleCount;
    }

    public void setTotalSaleCount(long totalSaleCount) {
        this.totalSaleCount = totalSaleCount;
    }
}