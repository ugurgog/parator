package com.paypad.parator.menu.transactions.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.pojo.SaleItemPojo;

public interface ReturnSaleItemPojoCallback {
    void OnReturn(SaleItemPojo saleItemPojo, ItemProcessEnum processType);
}
