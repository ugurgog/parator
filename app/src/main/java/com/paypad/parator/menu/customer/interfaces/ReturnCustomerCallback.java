package com.paypad.parator.menu.customer.interfaces;

import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.Customer;

public interface   ReturnCustomerCallback {
    void OnReturn(Customer customer, ItemProcessEnum processEnum);
}
