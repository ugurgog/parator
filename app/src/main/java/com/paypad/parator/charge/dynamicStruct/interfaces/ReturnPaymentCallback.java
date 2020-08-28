package com.paypad.parator.charge.dynamicStruct.interfaces;

import com.paypad.parator.enums.PaymentTypeEnum;

public interface ReturnPaymentCallback {
    void onReturn(PaymentTypeEnum paymentType);
}
