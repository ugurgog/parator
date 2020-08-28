package com.paypad.parator.enums;

public enum PasscodeTimeoutEnum {

    NEVER(0,1),
    SECOND_30(30000,2),
    MINUTE_1(60000,3),
    MINUTE_5(300000,4);

    private final int timeout;
    private final int id;

    PasscodeTimeoutEnum(int timeout, int id) {
        this.timeout = timeout;
        this.id = id;
    }

    public static PasscodeTimeoutEnum getById(int id) {
        for (PasscodeTimeoutEnum e : values()) {
            if (e.id == id)
                return e;
        }
        return null;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getId() {
        return id;
    }
}
