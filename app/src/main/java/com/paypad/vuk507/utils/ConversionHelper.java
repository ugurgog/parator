package com.paypad.vuk507.utils;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderItemTax;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.DiscountPojo;
import com.paypad.vuk507.model.pojo.SaleItemPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;

public class ConversionHelper {

    public static DiscountPojo convertDiscountToDiscountPojo(OrderItemDiscount orderItemDiscount){
        DiscountPojo discountPojo = new DiscountPojo();

        discountPojo.setId(orderItemDiscount.getId());
        discountPojo.setAmount(orderItemDiscount.getAmount());
        discountPojo.setCreateDate(orderItemDiscount.getCreateDate());
        discountPojo.setCreateUsername(orderItemDiscount.getCreateUsername());
        discountPojo.setName(orderItemDiscount.getName());
        discountPojo.setRate(orderItemDiscount.getRate());

        return discountPojo;
    }

    public static OrderItemDiscount convertDiscountToOrderItemDiscount(Discount discount){
        OrderItemDiscount orderItemDiscount = new OrderItemDiscount();

        orderItemDiscount.setId(discount.getId());
        orderItemDiscount.setName(discount.getName());
        orderItemDiscount.setRate(discount.getRate());
        orderItemDiscount.setAmount(discount.getAmount());
        orderItemDiscount.setCreateDate(discount.getCreateDate());
        orderItemDiscount.setCreateUsername(discount.getCreateUsername());

        return orderItemDiscount;
    }

    public static OrderRefundItem convertSaleItemToOrderRefundItem(SaleItem saleItem){

        OrderRefundItem orderRefundItem = new OrderRefundItem();

        orderRefundItem.setUuid(saleItem.getUuid());
        orderRefundItem.setSaleUuid(saleItem.getSaleUuid());
        orderRefundItem.setName(saleItem.getName());
        orderRefundItem.setAmount(saleItem.getAmount());
        orderRefundItem.setTaxAmount(saleItem.getTaxAmount());
        orderRefundItem.setGrossAmount(saleItem.getGrossAmount());
        orderRefundItem.setQuantity(saleItem.getQuantity());
        orderRefundItem.setNote(saleItem.getNote());
        orderRefundItem.setUuid(saleItem.getUuid());

        orderRefundItem.setDiscounts(saleItem.getDiscounts());

        orderRefundItem.setProductId(saleItem.getProductId());
        orderRefundItem.setDynamicAmount(saleItem.isDynamicAmount());
        orderRefundItem.setOrderItemTax(saleItem.getTaxModel());
        orderRefundItem.setColorId(saleItem.getColorId());
        orderRefundItem.setItemImage(saleItem.getItemImage());
        orderRefundItem.setCategoryName(saleItem.getCategoryName());
        orderRefundItem.setTransferred(saleItem.isTransferred());

        return orderRefundItem;
    }

    public static OrderItemTax convertTaxModelToOrderItemTax(TaxModel taxModel){

        OrderItemTax orderItemTax = new OrderItemTax();

        orderItemTax.setId(taxModel.getId());
        orderItemTax.setName(taxModel.getName());
        orderItemTax.setTaxRate(taxModel.getTaxRate());
        orderItemTax.setCreateDate(taxModel.getCreateDate());
        orderItemTax.setCreateUsername(taxModel.getCreateUsername());

        return orderItemTax;
    }

}
