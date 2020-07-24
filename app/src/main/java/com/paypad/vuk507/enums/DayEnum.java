package com.paypad.vuk507.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DayEnum {

    SUNDAY("Sunday","PAZAR", "SUNDAY", 0, "Sun", "Paz"),
    MONDAY("Monday","PAZARTESİ", "MONDAY", 1, "Mon", "Pzt"),
    TUESDAY("Tuesday","SALI", "TUESDAY", 2, "Tue", "Sal"),
    WEDNESDAY("Wednesday","ÇARŞAMBA", "WEDNESDAY", 3, "Wed", "Çarş"),
    THURSDAY("Thursday","PERŞEMBE", "THURSDAY", 4, "Thur", "Per"),
    FRIDAY("Friday","CUMA", "FRIDAY", 5, "Fri", "Cum"),
    SATURDAY("Saturday","CUMARTESİ", "SATURDAY", 6, "Sat", "Cmt");

    private final String code;
    private final String labelTr;
    private final String labelEn;
    private final int id;
    private final String enCode;
    private final String trCode;

    DayEnum(String code, String labelTr, String labelEn, int id, String enCode, String trCode) {
        this.code = code;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
        this.enCode = enCode;
        this.trCode = trCode;
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

    public String getEnCode() {
        return enCode;
    }

    public String getTrCode() {
        return trCode;
    }

    public static List<String> getEnCodeList(){
        List<String> enCodeList = new ArrayList<>();

        String[] enCodes = new String[]{DayEnum.SUNDAY.getEnCode(), DayEnum.MONDAY.getEnCode(), DayEnum.TUESDAY.getEnCode(),
                DayEnum.WEDNESDAY.getEnCode(), DayEnum.THURSDAY.getEnCode(), DayEnum.FRIDAY.getEnCode(), DayEnum.SATURDAY.getEnCode()};

        enCodeList.addAll(Arrays.asList(enCodes));
        return enCodeList;
    }

    public static List<String> getTrCodeList(){
        List<String> trCodeList = new ArrayList<>();

        String[] trCodes = new String[]{DayEnum.SUNDAY.getTrCode(), DayEnum.MONDAY.getTrCode(), DayEnum.TUESDAY.getTrCode(),
                DayEnum.WEDNESDAY.getTrCode(), DayEnum.THURSDAY.getTrCode(), DayEnum.FRIDAY.getTrCode(), DayEnum.SATURDAY.getTrCode()};

        trCodeList.addAll(Arrays.asList(trCodes));
        return trCodeList;
    }
}
