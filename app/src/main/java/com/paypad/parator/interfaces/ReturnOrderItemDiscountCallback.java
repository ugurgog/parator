package com.paypad.parator.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.OrderItemDiscount;

public interface ReturnOrderItemDiscountCallback {
    void OnReturn(OrderItemDiscount discount, ItemProcessEnum processType);
}
