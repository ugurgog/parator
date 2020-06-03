package com.paypad.vuk507.enums;

public enum CurrencyEnum {

    TL("Türk Lirası", "Turkish lira", "₺"),
    USD("Dolar", "Dollar", "$"),
    EUR("Euro", "Euro", "€"),
    JPY("Japon Yeni", "Japanese Yen", "¥"),
    AED("Birleşik Arap Emirlikleri Dirhemi", "United Arab Emirates Dirham", "د.إ"),
    SAR("Suudi Arabistan Riyali", "Saudi Arabian Riyal", "﷼");

    private final String labelTr;
    private final String labelEn;
    private final String symbol;

    private CurrencyEnum(String labelTr, String labelEn, String symbol) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.symbol = symbol;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getSymbol() {
        return symbol;
    }
}
