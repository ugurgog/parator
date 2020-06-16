package com.paypad.vuk507.enums;

public enum DynamicStructEnum {

    PRODUCT_SET("Ürün Yerleştir", "Set Product", 1),
    CATEGORY_SET("Kategori Yerleştir", "Set Category", 2),
    DISCOUNT_SET("İndirim Yerleştir", "Set Discount", 3),
    FUNCTION_SET("Fonksiyon Yerleştir", "Set Function", 4);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    private DynamicStructEnum(String labelTr, String labelEn, int id) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
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
