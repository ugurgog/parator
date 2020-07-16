package com.paypad.vuk507.utils;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.DiscountPojo;

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
    public static DiscountPojo convertDiscountToDiscountPojo(Discount discount){
        DiscountPojo discountPojo = new DiscountPojo();
        discountPojo.setId(discount.getId());
        discountPojo.setAmount(discount.getAmount());
        discountPojo.setCreateDate(discount.getCreateDate());
        discountPojo.setCreateUsername(discount.getCreateUsername());
        discountPojo.setName(discount.getName());
        discountPojo.setRate(discount.getRate());
        return discountPojo;
    }
}
