package com.paypad.vuk507.charge.order;

import android.content.Context;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;

public interface IOrderManager {

    String addProductToOrder(Context context, Product product, double amount, boolean isDynamicAmount);
    String addCustomItemToOrder(SaleItem saleItem, TaxModel taxModel);
    void setOrderItemTaxForProduct(SaleItem saleItem, Product product);
    void setOrderItemTaxForCustomItem(SaleItem saleItem, TaxModel taxModel);
    int getOrderItemCount();
    Transaction getTransactionWillBePaid();
    double getDiscountedAmountByAddingCustomItem(SaleItem saleItem);
    long getMaxSplitSequenceNumber();
    boolean isExistNotCompletedTransaction();
    boolean isExistPaymentCompletedTransaction();
    boolean isDiscountInSale(Discount discount);
    boolean isSaleItemInSale(SaleItem saleItem);
    void setRemainAmount(double amount);
    void addDiscountToOrder(Discount discount);
    void setUserIdToOrder(String userId);
    void setDeviceIdToOrder(Context context);
    void setRemainAmountByDiscountedAmount();
    void addDiscountRateToAllSaleItems(Discount discount);
    void setDiscountedAmountOfSale();
    Transaction addTransactionToOrder(double splitAmount);
    double getRemainAmount();
    double getTotalDiscountAmountOfSale();
    void addDiscountToSaleItem(String saleItemUuid, Discount discount);
    void removeDiscountFromSaleItem(String saleItemUuid, Discount discount);
}
