package com.paypad.parator.enums;

public enum DynamicStructEnum {

    PRODUCT_SET("Ürün Yerleştir", "Set Product", 1),
    CATEGORY_SET("Kategori Yerleştir", "Set Category", 2),
    TAX_SET("KDV Yerleştir", "Set Tax", 3),
    DISCOUNT_SET("İndirim Yerleştir", "Set Discount", 4),
    PAYMENT_SET("Ödeme Tipi Yerleştir", "Set Payment Type", 5);

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
