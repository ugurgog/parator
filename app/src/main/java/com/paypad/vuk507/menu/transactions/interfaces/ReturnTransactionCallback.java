package com.paypad.vuk507.menu.transactions.interfaces;

import com.paypad.vuk507.model.Transaction;

public interface ReturnTransactionCallback {
    void OnTransactionReturn(Transaction transaction, double returnAmount);
}
