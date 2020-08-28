package com.paypad.parator.enums;

public enum  TransactionTypeEnum {

    SALE(0),
    REFUND(1),
    CANCEL(2);

    private final int id;

    TransactionTypeEnum(int id) {
        this.id = id;
    }

    public static TransactionTypeEnum getById(long id) {
        for (TransactionTypeEnum e : values()) {
            if (e.id == id)
                return e;
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
