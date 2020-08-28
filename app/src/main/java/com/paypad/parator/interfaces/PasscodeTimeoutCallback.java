package com.paypad.parator.interfaces;

import com.paypad.parator.enums.PasscodeTimeoutEnum;

public interface PasscodeTimeoutCallback {
    void OnTimeoutReturn(PasscodeTimeoutEnum passcodeTimeoutType);
}
