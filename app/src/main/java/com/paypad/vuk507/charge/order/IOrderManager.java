package com.paypad.vuk507.charge.order;

import android.content.Context;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.OrderItemTax;
import com.paypad.vuk507.model.Product;

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
