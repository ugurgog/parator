package com.paypad.parator.interfaces;

import com.paypad.parator.enums.RefundReasonEnum;

public interface RefundReasonTypeCallback {
    void OnRefundReasonReturn(RefundReasonEnum refundReasonType);
}
