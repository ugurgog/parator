package com.paypad.vuk507.charge.dynamicStruct.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.DynamicBoxModel;

public interface ReturnDynamicBoxListener{
    void onReturn(DynamicBoxModel dynamicBoxModel, ItemProcessEnum processEnum);
}