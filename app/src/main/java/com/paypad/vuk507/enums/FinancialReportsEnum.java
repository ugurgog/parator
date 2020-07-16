package com.paypad.vuk507.enums;

public enum FinancialReportsEnum {

    X_REPORT("X Report", "X Raporu", 0),
    Z_REPORT("Z Report", "Z Raporu", 1),
    DAILY_REPORT("Daily Report", "Günlük Rapor", 2),
    MONTHLY_REPORT("Monthly Report", "Aylık Rapor", 3),
    DAILY_X_REPORT("Daily X Report", "Günlük X Raporu", 4),
    MONTHLY_X_REPORT("Monthly X Report", "Aylık X Raporu", 5),
    TWO_Z_REPORT("Report Between Two Z", "İki Z Arası Rapor", 6),
    TWO_DATE_REPORT("Report Between Two Date", "İki Tarih Arası Rapor", 7),
    CUMULATIVE_SUMMARY("Cumulative Summary", "Kümülatif Özet", 8);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    private FinancialReportsEnum(String labelEn, String labelTr, int id) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public int getId() {
        return id;
    }
}
