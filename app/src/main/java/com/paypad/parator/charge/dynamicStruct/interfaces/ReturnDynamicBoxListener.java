package com.paypad.parator.charge.dynamicStruct.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.DynamicBoxModel;

public interface ReturnDynamicBoxListener{
    void onReturn(DynamicBoxModel dynamicBoxModel, ItemProcessEnum processEnum);
}