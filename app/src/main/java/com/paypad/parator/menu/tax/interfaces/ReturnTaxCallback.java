package com.paypad.parator.menu.tax.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.TaxModel;

public interface ReturnTaxCallback {
    void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum);
}
