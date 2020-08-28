package com.paypad.parator.menu.unit.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.UnitModel;

public interface ReturnUnitCallback {
    void OnReturn(UnitModel unitModel, ItemProcessEnum processEnum);
}
