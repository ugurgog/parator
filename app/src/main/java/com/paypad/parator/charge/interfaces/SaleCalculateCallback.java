package com.paypad.parator.charge.interfaces;

import com.paypad.parator.model.Customer;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;

public interface SaleCalculateCallback {

    void onProductSelected(Product product, double amount, boolean isDynamicAmount);
    void onKeyPadClicked(int number);
    void onDiscountSelected(Discount discount);
    void OnTaxSelected(TaxModel taxModel);
    void onCustomAmountReturn(double amount);
    void onItemsCleared();
    void onNewSaleCreated();
    void OnCustomItemAdd(int addFromValue);
    void onRemoveCustomAmount(double amount);
    void onSaleNoteReturn(String note);
    void onSaleItemEditted();
    void onCustomerAdded(Customer customer);
    void onCustomerRemoved();
    void onSaleItemDeleted();
    void OnDiscountRemoved();
    void OnTransactionCancelled();


}
