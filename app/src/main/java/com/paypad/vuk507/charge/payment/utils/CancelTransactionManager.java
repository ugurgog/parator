package com.paypad.vuk507.charge.payment.utils;

import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;

import java.util.ArrayList;
import java.util.Date;

public class CancelTransactionManager {

    public static BaseResponse cancelTransactions(){

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        baseResponse = TransactionDBHelper.deleteTransactionsOfSale(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid());

        if(!baseResponse.isSuccess())
            return baseResponse;

        SaleModelInstance.getInstance().getSaleModel().setTransactions(new ArrayList<>());

        SaleModelInstance.getInstance().getSaleModel().getSale().setRemainAmount(0);
        SaleModelInstance.getInstance().getSaleModel().getSale().setCreateDate(null);
        SaleModelInstance.getInstance().getSaleModel().getSale().setPaymentCompleted(false);

        baseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel());

        if(!baseResponse.isSuccess())
            return baseResponse;

        return baseResponse;
    }

}
