package com.paypad.vuk507.charge.payment.interfaces;

public interface PaymentStatusCallback {

    int STATUS_CONTINUE = 1;
    int STATUS_NEW_SALE = 2;

    void OnPaymentReturn(int status);
}
