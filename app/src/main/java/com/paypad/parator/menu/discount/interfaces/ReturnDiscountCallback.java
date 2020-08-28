package com.paypad.parator.menu.discount.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.Discount;

public interface ReturnDiscountCallback {
    void OnReturn(Discount discount, ItemProcessEnum processType);
}
