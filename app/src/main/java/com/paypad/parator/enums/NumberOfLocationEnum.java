package com.paypad.parator.enums;

public enum NumberOfLocationEnum {

    LOC_1("1",1),
    LOC_2_9("2 - 9",2),
    LOC_PLUS_10("10+",3);

    private final String label;
    private final int id;

    NumberOfLocationEnum(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public static NumberOfLocationEnum getById(int id) {
        for (NumberOfLocationEnum e : values()) {
            if (e.id == id)
                return e;
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
