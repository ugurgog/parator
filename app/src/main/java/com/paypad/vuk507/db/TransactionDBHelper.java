package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;

import io.realm.Realm;

public class TransactionDBHelper {

    public static void createOrUpdateTransaction(Transaction transaction, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(transaction);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Transaction cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}
