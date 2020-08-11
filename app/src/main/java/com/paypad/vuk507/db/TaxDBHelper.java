package com.paypad.vuk507.db;


import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;

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

    public static BaseResponse deleteTax(long id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                try{
                    TaxModel taxModel = realm.where(TaxModel.class).equalTo("id", id).findFirst();

                    try{

                    }catch (Exception e){
                        taxModel.deleteFromRealm();
                        baseResponse.setMessage("Tax deleted successfully");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Tax cannot be deleted");
                }
            }
        });
        return baseResponse;
    }

    public static TaxModel getTax(long id){
        Realm realm = Realm.getDefaultInstance();
        TaxModel taxModel = realm.where(TaxModel.class).equalTo("id", id).findFirst();
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
                    baseResponse.setMessage("Tax is saved!");
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
