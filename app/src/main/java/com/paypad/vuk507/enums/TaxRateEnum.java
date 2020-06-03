package com.paypad.vuk507.enums;

public enum TaxRateEnum {

    TAX8("%8"),
    TAX18("%18");

    private final String label;

    private TaxRateEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
