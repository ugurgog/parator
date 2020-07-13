package com.paypad.vuk507.enums;

public enum DayEnum {

    SUNDAY("Sunday","PAZAR", "SUNDAY"),
    MONDAY("Monday","PAZARTESİ", "MONDAY"),
    TUESDAY("Tuesday","SALI", "TUESDAY"),
    WEDNESDAY("Wednesday","ÇARŞAMBA", "WEDNESDAY"),
    THURSDAY("Thursday","PERŞEMBE", "THURSDAY"),
    FRIDAY("Friday","CUMA", "FRIDAY"),
    SATURDAY("Saturday","CUMARTESİ", "SATURDAY");

    private final String code;
    private final String labelTr;
    private final String labelEn;

    DayEnum(String code, String labelTr, String labelEn) {
        this.code = code;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
    }

    public static DayEnum getByCode(String code) {
        for (DayEnum e : values()) {
            if (e.code.equals(code))
                return e;
        }
        return null;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getCode() {
        return code;
    }
}
