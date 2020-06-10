package com.paypad.vuk507.menu.product.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;

public interface ReturnItemCallback {
    void OnReturn(Product product, ItemProcessEnum processEnum);
}
