package com.paypad.parator.enums;

public enum  ChartSaleSelectionEnum {

    GROSS_SALES(0),
    NET_SALES(1),
    SALES_COUNT(2);

    private final int id;

    private ChartSaleSelectionEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
