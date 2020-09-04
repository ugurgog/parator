package com.paypad.parator.enums;

public enum  TutorialTypeEnum {

    TUTORIAL_PAYMENT(0),
    TUTORIAL_CREATE_ITEM(1),
    TUTORIAL_CREATE_UNIT(2),
    TUTORIAL_CREATE_TAX(3),
    TUTORIAL_CREATE_DISCOUNT(4),
    TUTORIAL_CREATE_CATEGORY(5);

    private final int id;

    TutorialTypeEnum(int id) {
        this.id = id;
    }

    public static TutorialTypeEnum getById(long id) {
        for (TutorialTypeEnum e : values()) {
            if (e.id == id)
                return e;
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
