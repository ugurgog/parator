package com.paypad.vuk507.enums;

public enum DynamicStructEnum {

    PRODUCT_SET("Ürün Yerleştir", "Set Product"),
    CATEGORY_SET("Kategori Yerleştir", "Set Category"),
    DISCOUNT_SET("İndirim Yerleştir", "Set Discount"),
    FUNCTION_SET("Fonksiyon Yerleştir", "Set Function");

    private final String labelTr;
    private final String labelEn;

    private DynamicStructEnum(String labelTr, String labelEn) {
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
