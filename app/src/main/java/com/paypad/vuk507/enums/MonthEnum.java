package com.paypad.vuk507.enums;

public enum  MonthEnum {

    JANUARY(1,"OCAK", "JANUARY"),
    FEBRUARY(2,"ŞUBAT", "FEBRUARY"),
    MARCH(3,"MART", "MARCH"),
    APRIL(4,"NİSAN", "APRIL"),
    MAY(5,"MAYIS", "MAY"),
    JUNE(6,"HAZİRAN", "JUNE"),
    JULY(7,"TEMMUZ", "JULY"),
    AUGUST(8,"AĞUSTOS", "AUGUST"),
    SEPTEMBER(9,"EYLÜL", "SEPTEMBER"),
    OCTOBER(10, "EKİM", "OCTOBER"),
    NOVEMBER(11,"KASIM", "NOVEMBER"),
    DECEMBER(12,"ARALIK", "DECEMBER");

    private final int id;
    private final String labelTr;
    private final String labelEn;

    MonthEnum(int id, String labelTr, String labelEn) {
        this.id = id;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
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
}
