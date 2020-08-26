package com.paypad.vuk507.enums;

public enum AnnualTurnoverRangeEnum {

    NOT_OPENED_YET("Not Opened Yet",1),
    RANGE_1K_120K("1K - 120K",2),
    RANGE_120K_360K("120K - 360K",3),
    RANGE_360K_600K("360K - 600K",4),
    RANGE_600K_1DOT2M("600K - 1.2M",5),
    RANGE_1DOT2M_4M("1.2M - 4M",6),
    RANGE_4M_6M("4M - 6M",7),
    RANGE_6M_12M("6M - 12M",8),
    RANGE_12M_60M("12M - 60M",9),
    RANGE_60M_120M("60M - 120M",10),
    RANGE_120M_1DOT2B("120M - 1.2B",11),
    RANGE_1DOT2B_ABOVE("1.2B and above",12);

    private final String label;
    private final int id;

    AnnualTurnoverRangeEnum(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public static AnnualTurnoverRangeEnum getById(int id) {
        for (AnnualTurnoverRangeEnum e : values()) {
            if (e.id == id)
                return e;
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
