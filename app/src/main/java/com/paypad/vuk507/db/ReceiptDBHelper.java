package com.paypad.vuk507.db;


import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Receipt;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReceiptDBHelper {

    public static RealmResults<Receipt> getAllReceiptsByUserId(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Receipt> receipts = realm.where(Receipt.class)
                .equalTo("createUserId", userId)
                .findAll();
        return receipts;
    }

    public static RealmResults<Receipt> getAllReceiptsBySaleId(String saleId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Receipt> receipts = realm.where(Receipt.class)
                .equalTo("saleId", saleId)
                .findAll();
        return receipts;
    }

    public static Receipt getReceiptByReceiptId(String receiptId){
        Realm realm = Realm.getDefaultInstance();
        Receipt receipt = realm.where(Receipt.class).equalTo("receiptId", receiptId).findFirst();
        return receipt;
    }

    public static BaseResponse createOrUpdateReceipt(Receipt receipt) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(receipt);

                    baseResponse.setObject(receipt);
                    baseResponse.setMessage("Receipt is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Receipt cannot be saved!");
                }
            }
        });
        return baseResponse;
    }
}
