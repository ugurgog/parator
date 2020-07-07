package com.paypad.vuk507.enums;

public enum  ProductUnitTypeEnum {

    PER_ITEM(-1, "Per Item", "Adet"),
    PER_GRAM(-2, "Per Gram", "Gram"),
    PER_HOUR(-3, "Per Hour", "Saat"),
    PER_KILOGRAM(-4,"Per Kilogram", "Kilogram"),
    PER_LITER(-5,"Per Liter", "Litre"),
    PER_METER(-6,"Per Meter", "Metre");

    private final int id;
    private final String labelTr;
    private final String labelEn;

    ProductUnitTypeEnum(int id, String labelEn, String labelTr) {
        this.id = id;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
    }

    public static ProductUnitTypeEnum getById(long id) {
        for (ProductUnitTypeEnum e : values()) {
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

}
