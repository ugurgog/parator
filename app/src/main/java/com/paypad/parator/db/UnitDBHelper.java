package com.paypad.parator.db;

import com.paypad.parator.model.UnitModel;
import com.paypad.parator.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class UnitDBHelper {

    public static RealmResults<UnitModel> getAllUnits(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UnitModel> unitModels = realm.where(UnitModel.class)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findAll();
        return unitModels;
    }

    public static BaseResponse deleteUnit(long id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            UnitModel unitModel = getUnit(id);

            realm.beginTransaction();

            unitModel.setDeleted(true);
            unitModel.setDeleteDate(new Date());
            realm.copyToRealm(unitModel);

            realm.commitTransaction();
        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }
        return baseResponse;
    }

    public static UnitModel getUnit(long id){
        Realm realm = Realm.getDefaultInstance();
        UnitModel unitModel = realm.where(UnitModel.class)
                .equalTo("id", id)
                .equalTo("isDeleted", false)
                .findFirst();
        return unitModel;
    }

    public static BaseResponse createOrUpdateUnit(UnitModel unitModel) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(unitModel);

                    baseResponse.setObject(unitModel);
                    //baseResponse.setMessage("Unit is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Unit cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static int getCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(UnitModel.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }
}
