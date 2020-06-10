package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.UnitModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class UnitDBHelper {

    public static RealmResults<UnitModel> getAllUnits(String username){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UnitModel> unitModels = realm.where(UnitModel.class)
                .equalTo("createUsername", username)
                .findAll();
        return unitModels;
    }

    public static void deleteUnit(long id, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    UnitModel unitModel = realm.where(UnitModel.class).equalTo("id", id).findFirst();
                    unitModel.deleteFromRealm();
                    baseResponse.setMessage("Unit deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Unit cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static UnitModel getUnit(int id){
        Realm realm = Realm.getDefaultInstance();
        UnitModel unitModel = realm.where(UnitModel.class).equalTo("id", id).findFirst();
        return unitModel;
    }

    public static void createOrUpdateUnit(UnitModel unitModel, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(unitModel);

                    baseResponse.setObject(unitModel);
                    baseResponse.setMessage("Unit is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Unit cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
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
