package com.paypad.vuk507.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.OrderRefundItem;

public interface ReturnOrderItemCallback {
    void onReturn(OrderRefundItem orderRefundItem, ItemProcessEnum processType);
}

