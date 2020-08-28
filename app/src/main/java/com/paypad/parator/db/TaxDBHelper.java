package com.paypad.parator.db;


import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaxDBHelper {

    public static RealmResults<TaxModel> getAllTaxes(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TaxModel> taxModels = realm.where(TaxModel.class)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findAll();
        return taxModels;
    }

    public static BaseResponse deleteTax(long id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            TaxModel taxModel = getTax(id);

            realm.beginTransaction();

            taxModel.setDeleted(true);
            taxModel.setDeleteDate(new Date());
            realm.copyToRealm(taxModel);

            realm.commitTransaction();
        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }
        return baseResponse;
    }

    public static TaxModel getTax(long id){
        Realm realm = Realm.getDefaultInstance();
        TaxModel taxModel = realm.where(TaxModel.class)
                .equalTo("id", id)
                .equalTo("isDeleted", false)
                .findFirst();
        return taxModel;
    }

    public static BaseResponse createOrUpdateTax(TaxModel taxModel) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(taxModel);

                    baseResponse.setObject(taxModel);
                    //baseResponse.setMessage("Tax is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Tax cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static int getCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(TaxModel.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }
}
