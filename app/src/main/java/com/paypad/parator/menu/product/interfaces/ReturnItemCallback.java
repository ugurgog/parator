package com.paypad.parator.menu.product.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.Product;

public interface ReturnItemCallback {
    void OnReturn(Product product, ItemProcessEnum processEnum);
}
