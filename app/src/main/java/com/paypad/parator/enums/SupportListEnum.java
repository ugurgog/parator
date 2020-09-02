package com.paypad.parator.enums;

public enum  SupportListEnum {

    HELP("Help", "Yardım", 0),
    TOURS_AND_TUTORIALS("Tours and Tutorials", "Turlar ve Eğitimler", 1),
    ANNOUNCEMENT("Announcement", "Duyurular", 2),
    ABOUT("About", "Hakkında", 3),
    LEGAL("Legal", "Yasal", 4);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    SupportListEnum(String labelEn, String labelTr, int id) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
    }

    public static SupportListEnum getById(int id) {
        for (SupportListEnum e : values()) {
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

    public int getId() {
        return id;
    }
}
