package com.paypad.vuk507.model.pojo;

import java.util.Date;

public class ReportDate{
    private Date startDate;
    private Date endDate;

    public ReportDate() {

    }

    public ReportDate(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

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
}