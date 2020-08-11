package com.paypad.vuk507.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.OrderItemDiscount;

public interface ReturnOrderItemDiscountCallback {
    void OnReturn(OrderItemDiscount discount, ItemProcessEnum processType);
}
