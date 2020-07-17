package com.paypad.vuk507.enums;

public enum DayEnum {

    SUNDAY("Sunday","PAZAR", "SUNDAY", 0),
    MONDAY("Monday","PAZARTESİ", "MONDAY", 1),
    TUESDAY("Tuesday","SALI", "TUESDAY", 2),
    WEDNESDAY("Wednesday","ÇARŞAMBA", "WEDNESDAY", 3),
    THURSDAY("Thursday","PERŞEMBE", "THURSDAY", 4),
    FRIDAY("Friday","CUMA", "FRIDAY", 5),
    SATURDAY("Saturday","CUMARTESİ", "SATURDAY", 6);

    private final String code;
    private final String labelTr;
    private final String labelEn;
    private final int id;

    DayEnum(String code, String labelTr, String labelEn, int id) {
        this.code = code;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
    }

    public static DayEnum getByCode(String code) {
        for (DayEnum e : values()) {
            if (e.code.equals(code))
                return e;
        }
        return null;
    }

    public static DayEnum getById(int id) {
        for (DayEnum e : values()) {
            if (e.id == id)
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

    public int getId() {
        return id;
    }
}
