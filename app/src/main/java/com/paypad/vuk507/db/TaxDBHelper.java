package com.paypad.vuk507.db;


import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.TaxModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaxDBHelper {

    public static RealmResults<TaxModel> getAllTaxes(String username){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TaxModel> taxModels = realm.where(TaxModel.class)
                .equalTo("createUsername", username)
                .findAll();
        return taxModels;
    }

    public static void deleteTax(long id, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    TaxModel taxModel = realm.where(TaxModel.class).equalTo("id", id).findFirst();
                    taxModel.deleteFromRealm();
                    baseResponse.setMessage("Tax deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Tax cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static TaxModel getTax(long id){
        Realm realm = Realm.getDefaultInstance();
        TaxModel taxModel = realm.where(TaxModel.class).equalTo("id", id).findFirst();
        return taxModel;
    }

    public static void createOrUpdateTax(TaxModel taxModel, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(taxModel);

                    baseResponse.setObject(taxModel);
                    baseResponse.setMessage("Tax is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Tax cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
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
