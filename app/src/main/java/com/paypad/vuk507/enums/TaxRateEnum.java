package com.paypad.vuk507.enums;

public enum TaxRateEnum {

    TAX8("% 8", 8.00d, -1),
    TAX18("% 18", 18.00d, -2),
    TAX0("% 0", 0.00d, -3),
    TAX26("% 26", 26.00d, -4),
    TAX5("% 5", 5.00d, -5),
    TAX1("% 1", 1.00d, -6);

    private final String label;
    private final double rateValue;
    private final long id;

    private TaxRateEnum(String label, double rateValue, long id) {
        this.label = label;
        this.rateValue = rateValue;
        this.id = id;
    }

    public static TaxRateEnum getById(long id) {
        for (TaxRateEnum e : values()) {
            if (e.id == id)
                return e;
        }

        return null;
    }

    public String getLabel() {
        return label;
    }

    public double getRateValue() {
        return rateValue;
    }

    public long getId() {
        return id;
    }
}
