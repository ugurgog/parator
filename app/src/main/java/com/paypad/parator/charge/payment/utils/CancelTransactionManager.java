package com.paypad.parator.charge.payment.utils;

import com.paypad.parator.db.SaleDBHelper;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModelInstance;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.RealmList;

public class CancelTransactionManager {

    public static BaseResponse cancelTransactionsOfOrder(){

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        baseResponse = TransactionDBHelper.deleteTransactionsOfSale(SaleModelInstance.getInstance().getSaleModel().getOrder().getId());

        if(!baseResponse.isSuccess())
            return baseResponse;

        SaleModelInstance.getInstance().getSaleModel().setTransactions(new ArrayList<>());

        SaleModelInstance.getInstance().getSaleModel().getOrder().setRemainAmount(0);
        SaleModelInstance.getInstance().getSaleModel().getOrder().setCreateDate(null);
        SaleModelInstance.getInstance().getSaleModel().getOrder().setPaymentCompleted(false);

        baseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel().getOrder());

        if(!baseResponse.isSuccess())
            return baseResponse;

        return baseResponse;
    }

    public static BaseResponse cancelTransactionsOfCancelProcess(RealmList<Transaction> transactions){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext(); ) {
            Transaction transaction = it.next();
            if (!transaction.isPaymentCompleted())
                it.remove();
        }

        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext(); ) {
            Transaction transaction = it.next();
            BaseResponse baseResponse1 = TransactionDBHelper.deleteTransactionById(transaction.getId());
            if (baseResponse1.isSuccess())
                it.remove();
            else {
                baseResponse = baseResponse1;
                break;
            }
        }

        return baseResponse;
    }

}
