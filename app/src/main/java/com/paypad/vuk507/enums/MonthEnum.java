package com.paypad.vuk507.enums;

public enum  MonthEnum {

    JANUARY(1,"OCAK", "JANUARY", "Jan", "Oca"),
    FEBRUARY(2,"ŞUBAT", "FEBRUARY", "Feb", "Şub"),
    MARCH(3,"MART", "MARCH", "Mar", "Mar"),
    APRIL(4,"NİSAN", "APRIL", "Apr", "Nis"),
    MAY(5,"MAYIS", "MAY", "May", "May"),
    JUNE(6,"HAZİRAN", "JUNE", "Jun", "Haz"),
    JULY(7,"TEMMUZ", "JULY", "Jul", "Tem"),
    AUGUST(8,"AĞUSTOS", "AUGUST", "Aug", "Agu"),
    SEPTEMBER(9,"EYLÜL", "SEPTEMBER", "Sep", "Eyl"),
    OCTOBER(10, "EKİM", "OCTOBER", "Oct", "Eki"),
    NOVEMBER(11,"KASIM", "NOVEMBER", "Nov", "Kas"),
    DECEMBER(12,"ARALIK", "DECEMBER", "Dec", "Ara");

    private final int id;
    private final String labelTr;
    private final String labelEn;
    private final String engCode;
    private final String trCode;

    MonthEnum(int id, String labelTr, String labelEn, String engCode, String trCode) {
        this.id = id;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.engCode = engCode;
        this.trCode = trCode;
    }

    public static MonthEnum getById(long id) {
        for (MonthEnum e : values()) {
            if (e.id == id)
                return e;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getEngCode() {
        return engCode;
    }

    public String getTrCode() {
        return trCode;
    }
}
