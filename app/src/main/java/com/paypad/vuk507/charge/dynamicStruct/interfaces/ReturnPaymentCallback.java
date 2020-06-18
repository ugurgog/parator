package com.paypad.vuk507.charge.dynamicStruct.interfaces;

import com.paypad.vuk507.enums.PaymentTypeEnum;

public interface ReturnPaymentCallback {
    void onReturn(PaymentTypeEnum paymentType);
}
