package com.paypad.vuk507.utils;

import android.util.Log;

import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.SaleModelInstance;

public class LogUtil {

    public static void logTransactions(){

        try{
            for(Transaction transaction : SaleModelInstance.getInstance().getSaleModel().getTransactions()){
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
            }
        }catch (Exception e){

        }

    }
}
