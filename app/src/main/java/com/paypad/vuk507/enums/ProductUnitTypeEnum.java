package com.paypad.vuk507.enums;

public enum  ProductUnitTypeEnum {

    PER_ITEM("Per Item", "Adet"),
    XXXX("XXXX En", "XXXX Tr"),
    YYYY("YYYY En", "YYYY Tr");

    private final String labelTr;
    private final String labelEn;

    private ProductUnitTypeEnum(String labelTr, String labelEn) {
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
