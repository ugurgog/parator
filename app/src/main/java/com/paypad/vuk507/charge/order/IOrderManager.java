package com.paypad.vuk507.charge.order;

import android.content.Context;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.OrderItemTax;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;

public interface IOrderManager {

    String addProductToOrder(Context context, Product product, double amount, boolean isDynamicAmount);
    String addCustomItemToOrder(SaleItem saleItem, OrderItemTax orderItemTax);
    void setOrderItemTaxForProduct(SaleItem saleItem, Product product);
    void setOrderItemTaxForCustomItem(SaleItem saleItem, OrderItemTax orderItemTax);
    int getOrderItemCount();
    Transaction getTransactionWillBePaid();
    double getDiscountedAmountByAddingCustomItem(SaleItem saleItem);
    long getMaxSplitSequenceNumber();
    boolean isExistNotCompletedTransaction();
    boolean isExistPaymentCompletedTransaction();
    boolean isDiscountInSale(Discount discount);
    boolean isSaleItemInSale(SaleItem saleItem);
    void setRemainAmount(double amount);
    void setUserIdToOrder(String userId);
    void setDeviceIdToOrder(Context context);
    void setRemainAmountByDiscountedAmount();
    void setDiscountedAmountOfSale();
    Transaction addTransactionToOrder(double splitAmount);
    double getRemainAmount();
    double getTotalDiscountAmountOfSale();
    void addDiscountToSaleItem(String saleItemUuid, Discount discount);
    void removeDiscountFromSaleItem(String saleItemUuid, Discount discount);
    void addDiscount(Discount discount);

    //void addDiscountToOrder(Discount discount);
    //void addDiscountRateToAllSaleItems(Discount discount);
}
