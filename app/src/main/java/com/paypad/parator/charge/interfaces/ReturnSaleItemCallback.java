package com.paypad.parator.charge.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.OrderItem;

public interface ReturnSaleItemCallback {
    void onReturn(OrderItem orderItem, ItemProcessEnum processType);
}
