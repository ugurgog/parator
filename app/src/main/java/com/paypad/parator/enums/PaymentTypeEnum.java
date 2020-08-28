package com.paypad.parator.enums;

public enum PaymentTypeEnum {

    CASH(-1, "Cash", "Nakit"),
    CREDIT_CARD(-2, "Credit Card", "Kredi Kartı"),
    GIFT_CARD(-3, "Gift Card", "Hediye Kartı"),
    CHECK(-4,"Check", "Çek");

    private final long id;
    private final String labelTr;
    private final String labelEn;

    PaymentTypeEnum(long id, String labelEn, String labelTr) {
        this.id = id;
        this.labelTr = labelTr;
        this.labelEn = labelEn;
    }

    public static PaymentTypeEnum getById(long id) {
        for (PaymentTypeEnum e : values()) {
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
}
