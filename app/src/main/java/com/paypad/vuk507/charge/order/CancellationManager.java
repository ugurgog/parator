package com.paypad.vuk507.charge.order;

import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CancellationManager{

    public static Transaction getTransactionWillBePaid(List<Transaction> transactions) {
        Transaction mTransaction = null;
        long seqNumber = 999;
        for(Transaction transaction : transactions){
            if(!transaction.isPaymentCompleted()){
                if(transaction.getSeqNumber() < seqNumber){
                    mTransaction = transaction;
                    seqNumber = transaction.getSeqNumber();
                }
            }
        }
        return mTransaction;
    }

    private static long getMaxSplitSequenceNumber(List<Transaction> transactions) {
        long maxValue = 0;

        if(transactions != null){
            for(Transaction transaction : transactions){
                if(transaction.getSeqNumber() > maxValue)
                    maxValue = transaction.getSeqNumber();
            }
        }
        return maxValue;
    }

    public static boolean isExistNotCompletedTransaction(List<Transaction> transactions) {
        for(Transaction transaction : transactions){
            if(!transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }

    public static boolean isExistPaymentCompletedTransaction(List<Transaction> transactions) {
        for(Transaction transaction : transactions){
            if(transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }

    public static void removeNotPayedSplits(List<Transaction> transactions){
        if(transactions != null ){
            for(Iterator<Transaction> it = transactions.iterator(); it.hasNext();) {
                Transaction transaction = it.next();

                if(!transaction.isPaymentCompleted())
                    it.remove();
            }
        }
    }

    /*public static void setRemainAmount(double amount) {
        CancelPaymentModel cancelPaymentModel = CancelPaymentModelInstance.getInstance().getCancelPaymentModel();
        cancelPaymentModel.setRemainAmount(CommonUtils.round(amount, 2));
    }*/

    public static Transaction addTransactionToOrder(double splitAmount, List<Transaction> transactions, String orderId) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setOrderId(orderId);
        transaction.setTransactionAmount(CommonUtils.round(splitAmount, 2));
        transaction.setSeqNumber(getMaxSplitSequenceNumber(transactions) + 1);
        transactions.add(transaction);
        return transaction;
    }
}
