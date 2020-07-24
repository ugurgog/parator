package com.paypad.vuk507.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<String> getEnCodeList(){
        List<String> enCodeList = new ArrayList<>();

        String[] enCodes = new String[]{MonthEnum.JANUARY.getEngCode(), MonthEnum.FEBRUARY.getEngCode(), MonthEnum.MARCH.getEngCode(),
                MonthEnum.APRIL.getEngCode(), MonthEnum.MAY.getEngCode(), MonthEnum.JUNE.getEngCode(),
                MonthEnum.JULY.getEngCode(), MonthEnum.AUGUST.getEngCode(), MonthEnum.SEPTEMBER.getEngCode(),
                MonthEnum.OCTOBER.getEngCode(), MonthEnum.NOVEMBER.getEngCode(), MonthEnum.DECEMBER.getEngCode()};

        enCodeList.addAll(Arrays.asList(enCodes));
        return enCodeList;
    }

    public static List<String> getTrCodeList(){
        List<String> trCodeList = new ArrayList<>();

        String[] trCodes = new String[]{MonthEnum.JANUARY.getTrCode(), MonthEnum.FEBRUARY.getTrCode(), MonthEnum.MARCH.getTrCode(),
                MonthEnum.APRIL.getTrCode(), MonthEnum.MAY.getTrCode(), MonthEnum.JUNE.getTrCode(),
                MonthEnum.JULY.getTrCode(), MonthEnum.AUGUST.getTrCode(), MonthEnum.SEPTEMBER.getTrCode(),
                MonthEnum.OCTOBER.getTrCode(), MonthEnum.NOVEMBER.getTrCode(), MonthEnum.DECEMBER.getTrCode()};

        trCodeList.addAll(Arrays.asList(trCodes));
        return trCodeList;
    }
}
