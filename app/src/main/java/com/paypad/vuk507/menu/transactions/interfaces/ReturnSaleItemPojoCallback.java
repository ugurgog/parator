package com.paypad.vuk507.menu.transactions.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.pojo.SaleItemPojo;

public interface ReturnSaleItemPojoCallback {
    void OnReturn(SaleItemPojo saleItemPojo, ItemProcessEnum processType);
}
