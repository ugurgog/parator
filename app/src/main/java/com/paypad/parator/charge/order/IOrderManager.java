package com.paypad.parator.charge.order;

import android.content.Context;

import com.paypad.parator.model.Discount;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.OrderItemTax;
import com.paypad.parator.model.Product;

public interface IOrderManager {

    String addProductToOrder(Context context, Product product, double amount, boolean isDynamicAmount);
    String addCustomItemToOrder(OrderItem orderItem, OrderItemTax orderItemTax);
    int getOrderItemCount();
    void setDiscountedAmountOfSale();
    void addDiscountToSaleItem(OrderItem orderItem, Discount discount);
    void removeDiscountFromSaleItem(OrderItem orderItem, Discount discount);

    //void addDiscountToOrder(Discount discount);
    //void addDiscountRateToAllSaleItems(Discount discount);
}
