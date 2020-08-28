package com.paypad.parator.menu.group.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.Group;

public interface ReturnGroupCallback {
    void OnGroupReturn(Group group, ItemProcessEnum processEnum);
}
