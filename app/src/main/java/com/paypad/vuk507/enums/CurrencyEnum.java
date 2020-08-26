package com.paypad.vuk507.enums;

public enum CurrencyEnum {

    TL("Türk Lirası", "Turkish lira", "₺", 1),
    USD("Dolar", "Dollar", "$", 2),
    EUR("Euro", "Euro", "€", 3),
    JPY("Japon Yeni", "Japanese Yen", "¥", 4),
    AED("Birleşik Arap Emirlikleri Dirhemi", "United Arab Emirates Dirham", "د.إ", 5),
    SAR("Suudi Arabistan Riyali", "Saudi Arabian Riyal","﷼",6);

    private final String labelTr;
    private final String labelEn;
    private final String symbol;
    private final int id;

    CurrencyEnum(String labelTr, String labelEn, String symbol, int id) {
        this.labelTr = labelTr;
        this.labelEn = labelEn;
        this.symbol = symbol;
        this.id = id;
    }

    public static CurrencyEnum getById(int id) {
        for (CurrencyEnum e : values()) {
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

    public String getSymbol() {
        return symbol;
    }

    public int getId() {
        return id;
    }
}
