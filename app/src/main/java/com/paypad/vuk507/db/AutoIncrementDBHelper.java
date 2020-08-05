package com.paypad.vuk507.db;


import com.paypad.vuk507.model.AutoIncrement;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Receipt;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import io.realm.Realm;
import io.realm.RealmResults;

public class AutoIncrementDBHelper {

    public static AutoIncrement getAutoIncrement(String userId){
        Realm realm = Realm.getDefaultInstance();
        AutoIncrement autoIncrement = realm.where(AutoIncrement.class).equalTo("userId", userId).findFirst();
        return autoIncrement;
    }

    public static BaseResponse createOrUpdateAutoIncrement(AutoIncrement autoIncrement) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(autoIncrement);

                    baseResponse.setObject(autoIncrement);
                    baseResponse.setMessage("AutoIncrement is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("AutoIncrement cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static int getCurrentBatchNum(String userId){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(AutoIncrement.class)
                .equalTo("userId", userId)
                .max("batchNum");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;

            if(nextId > 999999)
                nextId = 1;
        }
        return nextId;
    }

    public static int getCurrentReceiptNum(String userId){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(AutoIncrement.class)
                .equalTo("userId", userId)
                .max("receiptNum");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;

            if(nextId > 999999)
                nextId = 1;
        }
        return nextId;
    }

    public static int getCurrentRetrefNumCounter(String userId){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(AutoIncrement.class)
                .equalTo("userId", userId)
                .max("retrefNumCounter");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;

            if(nextId > 9999999)
                nextId = 1;
        }

        AutoIncrement autoIncrement = AutoIncrementDBHelper.getAutoIncrement(userId);

        realm.beginTransaction();

        AutoIncrement tempAutoIncrement = realm.copyToRealm(autoIncrement);
        tempAutoIncrement.setRetrefNumCounter(nextId);

        realm.commitTransaction();

        AutoIncrementDBHelper.createOrUpdateAutoIncrement(tempAutoIncrement);

        LogUtil.logAutoIncrement(tempAutoIncrement);

        return nextId;
    }
}
