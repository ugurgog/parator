package com.paypad.vuk507.enums;

public enum PrinterStatusEnum {

    RUNNING(1, "Printer is running", "Yazıcı çalışıyor", true),
    INITIALIZING(2, "Printer found but still initializing", "Yazıcı bulundu, ancak hala başlatılıyor", false),
    NEEDS_TO_BE_REPRINTED(3, "Printer hardware interface is abnormal and needs to be reprinted", "Yazıcı donanım arayüzü anormal ve yeniden yazdırılması gerekiyor", false),
    OUT_OF_PAPER(4, "Printer is out of paper", "Yazıcıda kağıt bitti", false),
    OVERHEATING(5, "Printer is overheating", "Yazıcı aşırı ısınıyor", false),
    NOT_CLOSED(6, "Printer's cover is not closed", "Yazıcının kapağı kapalı değil", false),
    CUTTER_IS_ABNORMAL(7, "Printer's cutter is abnormal", "Yazıcının kesici anormal", false),
    CUTTER_IS_NORMAL(8, "Printer's cutter is normal", "Yazıcının kesici normal", true),
    NOT_FOUND_BLACK_PAPER(8, "Not found black mark paper", "Kara leke kağıdı bulunamadı", false),
    NOT_EXIST(505, "Printer does not exist", "Yazıcı mevcut değil", false);


    private final long id;
    private final String labelTr;
    private final String labelEn;
    private final boolean isNormal;

    PrinterStatusEnum(long id, String labelEn, String labelTr, boolean isNormal) {
        this.id = id;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.isNormal = isNormal;
    }

    public static PrinterStatusEnum getById(long id) {
        for (PrinterStatusEnum e : values()) {
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

    public long getId() {
        return id;
    }

    public boolean isNormal() {
        return isNormal;
    }
}
