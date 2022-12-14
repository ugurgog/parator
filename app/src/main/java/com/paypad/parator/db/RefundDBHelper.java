package com.paypad.parator.db;

import com.paypad.parator.model.Refund;
import com.paypad.parator.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class RefundDBHelper {

    public static RealmResults<Refund> getAllRefundsOfTransaction(String transactionId, boolean isSuccessful){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Refund> refunds = realm.where(Refund.class)
                .equalTo("transactionId", transactionId)
                .equalTo("isSuccessful", isSuccessful)
                .findAll();
        return refunds;
    }

    public static RealmResults<Refund> getAllRefundsOfOrder(String orderId, boolean isSuccessful){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Refund> refunds = realm.where(Refund.class)
                .equalTo("orderId", orderId)
                .equalTo("isSuccessful", isSuccessful)
                .findAll();
        return refunds;
    }

    public static RealmResults<Refund> getAllRefundsByRefundGroupId(String refundGroupId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Refund> refunds = realm.where(Refund.class)
                .equalTo("refundGroupId", refundGroupId)
                .equalTo("isSuccessful", true)
                .findAll();
        return refunds;
    }

    public static RealmResults<Refund> getRefundsByZNum(long zNum){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Refund> transactions = realm.where(Refund.class)
                .equalTo("zNum", zNum)
                .equalTo("isEODProcessed", false)
                .findAll();
        return transactions;
    }

    public static BaseResponse createOrUpdateRefund(Refund refund) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(refund);

                    baseResponse.setObject(refund);
                    //baseResponse.setMessage("Refund is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Refund cannot be saved!");
                }
            }
        });

        return baseResponse;
    }
}
