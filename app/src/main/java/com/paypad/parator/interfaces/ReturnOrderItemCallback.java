package com.paypad.parator.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.OrderRefundItem;

public interface ReturnOrderItemCallback {
    void onReturn(OrderRefundItem orderRefundItem, ItemProcessEnum processType);
}

