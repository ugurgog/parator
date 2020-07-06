package com.paypad.vuk507.utils;

import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.order.OrderItemTax;

import java.util.UUID;

public class ConversionHelper {

    public static OrderItemTax convertTaxModelToOrderItemTax(TaxModel taxModel){
        OrderItemTax orderItemTax = new OrderItemTax();
        orderItemTax.setOrderItemTaxId(UUID.randomUUID().toString());
        orderItemTax.setName(taxModel.getName());
        orderItemTax.setTaxRate(taxModel.getTaxRate());
        orderItemTax.setTaxId(taxModel.getId());
        return orderItemTax;
    }
}
