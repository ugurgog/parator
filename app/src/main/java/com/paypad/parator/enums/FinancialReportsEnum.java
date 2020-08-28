package com.paypad.parator.enums;

public enum FinancialReportsEnum {

    Z_REPORT("Z Report", "Z Raporu"),
    X_REPORT("X Report", "X Raporu"),
    SALES_REPORT("Sales Reports", "Satış Raporları"),
    EKU_REPORTS("EKU Reports", "EKU Raporları"),
    FINANCIAL_REPORTS("Financial Reports", "Mali Raporlar"),
    PLU_REPORT("PLU Report", "PLU Rapor"),
    Z_TERM_REPORT("Z Term Report", "Z Dönem Raporu"),
    X_TERM_REPORT("X Term Report", "X Dönem Raporu"),


    ACCUMULATED_TERM_REPORT("Accumulated Term Report", "Birikmiş Dönem Raporu"),
    REPORT_BETWEEN_TWO_Z("Z Report Between 2 Z", "İki Z Arası Rapor"),
    REPORT_BETWEEN_TWO_DATE("Report Between 2 Date", "İki Tarih Arası Rapor"),
    REPORT_CURRENT_MONTH("Current Month Report", "Bu Aya Ait Rapor"),
    REPORT_PREVIOUS_MONTH("Previous Month Report", "Bir Önceki Aya Ait Rapor"),

    PRINT_DAILY_REPORT("Print Daily Report", "Günlük Rapor Yazdır"),
    PRINT_MONTHLY_REPORT("Print Monthly Report", "Aylık Rapor Yazdır"),
    SEND_MONTHLY_REPORT("Send Monthly Report", "Aylık Rapor Gönder"),
    DAILY_X_REPORT("Daily X Report", "Günlük X Raporu"),
    MONTHLY_X_REPORT("Monthly X Report", "Aylık X Raporu");

    private final String labelTr;
    private final String labelEn;

    private FinancialReportsEnum(String labelEn, String labelTr) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getLabelEn() {
        return labelEn;
    }
}
