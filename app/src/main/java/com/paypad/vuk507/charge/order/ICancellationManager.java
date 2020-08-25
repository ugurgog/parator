package com.paypad.vuk507.charge.order;

import com.paypad.vuk507.model.Transaction;

public interface ICancellationManager {

    Transaction getTransactionWillBePaid();
    boolean isExistNotCompletedTransaction();
    boolean isExistPaymentCompletedTransaction();
    Transaction addTransactionToOrder(double splitAmount);
    double getRemainAmount();
}
