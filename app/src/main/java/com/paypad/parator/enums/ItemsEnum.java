package com.paypad.parator.enums;

public enum ItemsEnum {

    ALL_ITEMS("All Items", "Tüm Ürünler", 0),
    CATEGORIES("Categories", "Kategoriler", 1),
    /*MODIFIERS("Modifiers", "Düzenleyiciler", 2),*/
    DISCOUNTS("Discounts", "İndirimler", 2),
    UNITS("Units", "Birimler", 3),
    TAXES("Taxes", "Vergiler", 4);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    private ItemsEnum(String labelEn, String labelTr, int id) {
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
