package com.paypad.vuk507.charge.interfaces;

import com.paypad.vuk507.R;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;

public interface SaleCalculateCallback {

    void onProductSelected(Product product, double amount, boolean isDynamicAmount);
    void onKeyPadClicked(int number);
    void onTaxSelected(TaxModel taxModel);
    void onDiscountSelected(Discount discount);
    void onCustomAmountReturn(double amount);
    void onItemsCleared();
    void onNewSaleCreated();
    void onCustomAmountAdded(double amount, String note);
    void onRemoveCustomAmount(double amount);
    void onSaleNoteReturn(String note);
    void onSaleItemEditted();
    void onCustomerAdded(Customer customer);
    void onCustomerRemoved();
    void onSaleItemDeleted();
    void OnDiscountRemoved();

}
