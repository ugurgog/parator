package com.paypad.vuk507.utils;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderItemTax;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.DiscountPojo;

import java.util.UUID;

public class ConversionHelper {

    public static DiscountPojo convertDiscountToDiscountPojo(OrderItemDiscount orderItemDiscount){
        DiscountPojo discountPojo = new DiscountPojo();

        discountPojo.setId(orderItemDiscount.getId());
        discountPojo.setAmount(orderItemDiscount.getAmount());
        discountPojo.setCreateDate(orderItemDiscount.getCreateDate());
        discountPojo.setUserId(orderItemDiscount.getUserId());
        discountPojo.setName(orderItemDiscount.getName());
        discountPojo.setRate(orderItemDiscount.getRate());
        discountPojo.setUpdateDate(orderItemDiscount.getUpdateDate());
        discountPojo.setUpdateUserId(orderItemDiscount.getUpdateUserId());

        return discountPojo;
    }

    public static OrderItemDiscount convertDiscountToOrderItemDiscount(Discount discount){
        OrderItemDiscount orderItemDiscount = new OrderItemDiscount();

        orderItemDiscount.setId(discount.getId());
        orderItemDiscount.setName(discount.getName());
        orderItemDiscount.setRate(discount.getRate());
        orderItemDiscount.setAmount(discount.getAmount());
        orderItemDiscount.setCreateDate(discount.getCreateDate());
        orderItemDiscount.setUserId(discount.getUserId());
        orderItemDiscount.setUpdateDate(discount.getUpdateDate());
        orderItemDiscount.setUpdateUserId(discount.getUpdateUserId());

        return orderItemDiscount;
    }

    public static OrderRefundItem convertSaleItemToOrderRefundItem(OrderItem orderItem){

        OrderRefundItem orderRefundItem = new OrderRefundItem();

        orderRefundItem.setId(UUID.randomUUID().toString());
        orderRefundItem.setOrderItemId(orderItem.getId());
        orderRefundItem.setOrderId(orderItem.getOrderId());
        orderRefundItem.setName(orderItem.getName());
        orderRefundItem.setAmount(orderItem.getAmount());
        orderRefundItem.setTaxAmount(orderItem.getTaxAmount());
        orderRefundItem.setGrossAmount(orderItem.getGrossAmount());
        orderRefundItem.setNote(orderItem.getNote());

        orderRefundItem.setDiscounts(orderItem.getDiscounts());

        orderRefundItem.setProductId(orderItem.getProductId());
        orderRefundItem.setDynamicAmount(orderItem.isDynamicAmount());
        orderRefundItem.setOrderItemTax(orderItem.getTaxModel());
        orderRefundItem.setColorId(orderItem.getColorId());
        orderRefundItem.setItemImage(orderItem.getItemImage());
        orderRefundItem.setCategoryName(orderItem.getCategoryName());
        orderRefundItem.setTransferred(orderItem.isTransferred());

        return orderRefundItem;
    }

    public static OrderItemTax convertTaxModelToOrderItemTax(TaxModel taxModel){
        OrderItemTax orderItemTax = new OrderItemTax();

        orderItemTax.setId(taxModel.getId());
        orderItemTax.setName(taxModel.getName());
        orderItemTax.setTaxRate(taxModel.getTaxRate());
        orderItemTax.setCreateDate(taxModel.getCreateDate());
        orderItemTax.setUserId(taxModel.getUserId());
        orderItemTax.setUpdateDate(taxModel.getUpdateDate());
        orderItemTax.setUpdateUserId(taxModel.getUpdateUserId());

        return orderItemTax;
    }

}
