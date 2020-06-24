package com.paypad.vuk507.charge.interfaces;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;

public interface SaleCalculateCallback {

    void onProductSelected(Product product, double amount);
    void onKeyPadClicked(int number);
    void onTaxSelected(TaxModel taxModel);
    void onDiscountSelected(Discount discount);
    void onCustomAmountReturn(double amount);
    void onItemsCleared();
    void onNewSaleCreated();
    void onCustomAmountAdded();
    void onRemoveCustomAmount(double amount);
    void onSaleNoteReturn(String note);

}
