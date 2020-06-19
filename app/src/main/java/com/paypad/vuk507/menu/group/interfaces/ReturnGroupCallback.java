package com.paypad.vuk507.menu.group.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.Group;

public interface ReturnGroupCallback {
    void OnGroupReturn(Group group, ItemProcessEnum processEnum);
}
