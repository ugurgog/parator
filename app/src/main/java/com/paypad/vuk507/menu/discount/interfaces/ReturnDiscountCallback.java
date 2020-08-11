package com.paypad.vuk507.menu.discount.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.Discount;

public interface ReturnDiscountCallback {
    void OnReturn(Discount discount, ItemProcessEnum processType);
}
