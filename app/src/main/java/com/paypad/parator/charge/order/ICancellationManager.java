package com.paypad.parator.charge.order;

import com.paypad.parator.model.Transaction;

public interface ICancellationManager {

    Transaction getTransactionWillBePaid();
    boolean isExistNotCompletedTransaction();
    boolean isExistPaymentCompletedTransaction();
    Transaction addTransactionToOrder(double splitAmount);
    double getRemainAmount();
}
