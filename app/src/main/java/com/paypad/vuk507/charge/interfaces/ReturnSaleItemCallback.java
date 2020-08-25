package com.paypad.vuk507.charge.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.OrderItem;

public interface ReturnSaleItemCallback {
    void onReturn(OrderItem orderItem, ItemProcessEnum processType);
}
