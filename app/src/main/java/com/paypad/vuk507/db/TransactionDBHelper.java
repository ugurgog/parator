package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;

import io.realm.Realm;

public class TransactionDBHelper {

    public static BaseResponse createOrUpdateTransaction(Transaction transaction) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(transaction);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Transaction cannot be saved!");
                }
            }
        });
        return baseResponse;
    }
}
