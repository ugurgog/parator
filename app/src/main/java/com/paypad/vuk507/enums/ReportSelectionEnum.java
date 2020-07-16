package com.paypad.vuk507.enums;

public enum ReportSelectionEnum {

    ONE_D("1D", 0),
    ONE_W("1W", 1),
    ONE_M("1M", 2),
    THREE_M("3M",3),
    ONE_Y("1Y", 4),
    NOT_DEFINED("X", 5);

    private final String label;
    private final int id;

    private ReportSelectionEnum(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
