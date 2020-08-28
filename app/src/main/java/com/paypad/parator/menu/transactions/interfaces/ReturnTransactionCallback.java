package com.paypad.parator.menu.transactions.interfaces;

import com.paypad.parator.model.Transaction;

public interface ReturnTransactionCallback {
    void OnTransactionReturn(Transaction transaction, double returnAmount);
}
