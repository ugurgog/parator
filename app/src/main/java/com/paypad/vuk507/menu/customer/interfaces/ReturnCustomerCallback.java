package com.paypad.vuk507.menu.customer.interfaces;

import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.Customer;

public interface   ReturnCustomerCallback {
    void OnReturn(Customer customer, ItemProcessEnum processEnum);
}
