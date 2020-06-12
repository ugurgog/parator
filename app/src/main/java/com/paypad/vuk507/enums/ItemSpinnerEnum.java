package com.paypad.vuk507.enums;

public enum  ItemSpinnerEnum {

    PRODUCTS("All Items", "Tüm Ürünler", 0),
    DISCOUNTS("Discounts", "İndirimler", 1),
    CATEGORIES("Categories", "Kategoriler", 2);

    private final String labelTr;
    private final String labelEn;
    private final int id;

    private ItemSpinnerEnum(String labelEn, String labelTr, int id) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.id = id;
    }

    public static ItemSpinnerEnum getById(int id) {
        for (ItemSpinnerEnum e : values()) {
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
