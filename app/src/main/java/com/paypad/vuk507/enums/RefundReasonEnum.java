package com.paypad.vuk507.enums;

public enum RefundReasonEnum {

    RETURNED_GOODS("İade Edilen Mallar", "Returned Goods"),
    ACCIDENTAL_CHARGE("Yanlışlıkla Ödeme", "Accidental Charge"),
    CANCELED_ORDER("İptal Edilen Sipariş", "Canceled Order"),
    FRADULENT_CHARGE("Hileli Ödeme", "Fradulent Charge"),
    OTHER("Diğer", "Other");

    private final String labelTr;
    private final String labelEn;

    private RefundReasonEnum(String labelTr, String labelEn) {
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
