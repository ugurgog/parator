package com.paypad.parator.enums;

public enum  ReceiptTypeEnum {

    SALE_CUSTOMER(0),
    SALE_MERCHANT(1),
    END_OF_DAY(2);

    private final int id;

    ReceiptTypeEnum(int id) {
        this.id = id;
    }

    public static ReceiptTypeEnum getById(long id) {
        for (ReceiptTypeEnum e : values()) {
            if (e.id == id)
                return e;
        }

        return null;
    }

    public int getId() {
        return id;
    }
}

