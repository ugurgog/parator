package com.paypad.vuk507.charge.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.SaleItem;

public interface ReturnSaleItemCallback {
    void onReturn(SaleItem saleItem, ItemProcessEnum processType);
}
