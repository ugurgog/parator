package com.paypad.vuk507.db;

import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class DynamicBoxModelDBHelper {

    public static RealmResults<DynamicBoxModel> getAllDynamicBoxes(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DynamicBoxModel> dynamicBoxModels = realm.where(DynamicBoxModel.class)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findAll();
        return dynamicBoxModels;
    }

    public static BaseResponse deleteDynamicBoxById(String id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                            .equalTo("id", id)
                            .findFirst();

                    dynamicBoxModel.deleteFromRealm();
                    baseResponse.setMessage("Dynamic Box deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Dynamic Box cannot be deleted");
                }
            }
        });
        return baseResponse;
    }

    public static DynamicBoxModel getDynamicBoxModel(long structId, long itemId, String userId){
        Realm realm = Realm.getDefaultInstance();
        DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                .equalTo("structId", structId)
                .equalTo("itemId", itemId)
                .equalTo("userId", userId).findFirst();
        return dynamicBoxModel;
    }

    public static DynamicBoxModel getDynamicBoxModelById(String id){
        Realm realm = Realm.getDefaultInstance();
        DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                .equalTo("id", id).findFirst();
        return dynamicBoxModel;
    }

    public static BaseResponse createOrUpdateDynamicBox(DynamicBoxModel dynamicBoxModel) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(dynamicBoxModel);
                    baseResponse.setObject(dynamicBoxModel);
                    baseResponse.setMessage("Dynamic box is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Dynamic box cannot be saved!");
                }
            }
        });
        return baseResponse;
    }
}
