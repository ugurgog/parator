package com.paypad.vuk507.utils;

import android.util.Log;

import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.SaleModelInstance;

import java.util.List;

public class LogUtil {

    public static void logTransactions(List<Transaction> transactions){

        try{
            for(Transaction transaction : transactions){
                Log.i("Info", "::logTransactions Transactions +++++++++++++++++++++++++");
                Log.i("Info", "::logTransactions Transaction: SaleUuid         :" + transaction.getSaleUuid() );
                Log.i("Info", "::logTransactions Transaction: TransactionUuid  :" + transaction.getTransactionUuid() );
                Log.i("Info", "::logTransactions Transaction: SeqNumber        :" + transaction.getSeqNumber() );
                Log.i("Info", "::logTransactions Transaction: TransactionAmount:" + transaction.getTransactionAmount() );
                Log.i("Info", "::logTransactions Transaction: CashAmount       :" + transaction.getCashAmount() );
                Log.i("Info", "::logTransactions Transaction: ChangeAmount     :" + transaction.getChangeAmount() );
                Log.i("Info", "::logTransactions Transaction: CreateDate       :" + transaction.getCreateDate() );
                Log.i("Info", "::logTransactions Transaction: PaymentTypeId    :" + transaction.getPaymentTypeId() );
                Log.i("Info", "::logTransactions Transaction: TipAmount        :" + transaction.getTipAmount() );
                Log.i("Info", "::logTransactions Transaction: TotalAmount      :" + transaction.getTotalAmount() );
                Log.i("Info", "::logTransactions Transaction: PaymentCompleted :" + transaction.isPaymentCompleted() );
            }
        }catch (Exception e){

        }
    }

    public static void logSale(Sale sale){
        try{
            Log.i("Info", "::logSale Transactions +++++++++++++++++++++++++");
            Log.i("Info", "::logSale CustomerId:" + sale.getCustomerId() +
                    ", Discounts:" + sale.getDiscounts().toString() +
                    ", DiscountedAmount:" + sale.getDiscountedAmount() +
                    ", TotalAmount:" + sale.getTotalAmount() +
                    ", SaleCount:" + sale.getSaleCount() +
                    ", SaleUuid:" + sale.getSaleUuid() +
                    ", RemainAmount:" + sale.getRemainAmount() +
                    ", UserUuid:" + sale.getUserUuid() +
                    ", CreateDate:" + sale.getCreateDate());
        }catch (Exception e){

        }
    }

    public static void logTransaction(Transaction transaction){
        try{
            Log.i("Info", "::logTransaction +++++++++++++++++++++++++");
            Log.i("Info", "::logTransaction SaleUuid:" + transaction.getSaleUuid() +
                    ", TransactionUuid:" + transaction.getTransactionUuid() +
                    ", SeqNumber:" + transaction.getSeqNumber() +
                    ", TransactionAmount:" + transaction.getTransactionAmount() +
                    ", TotalAmount:" + transaction.getTotalAmount() +
                    ", TipAmount:" + transaction.getTipAmount() +
                    ", PaymentTypeId:" + transaction.getPaymentTypeId() +
                    ", CreateDate:" + transaction.getCreateDate() +
                    ", ChangeAmount:" + transaction.getChangeAmount() +
                    ", CashAmount:" + transaction.getCashAmount() +
                    ", UserUuid:" + transaction.getUserUuid());
        }catch (Exception e){

        }
    }
}
