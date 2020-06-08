package com.paypad.vuk507.menu.tax.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;

public interface ReturnTaxCallback {
    void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum);
}
