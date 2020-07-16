package com.paypad.vuk507.enums;

public enum  ReportsEnum {

    SALE_REPORTS("Sale Reports", "Satış Raporları", 0),
    FINANCIAL_REPORTS("Financial Reports", "Mali Raporlar", 1);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    private ReportsEnum(String labelEn, String labelTr, int id) {
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
