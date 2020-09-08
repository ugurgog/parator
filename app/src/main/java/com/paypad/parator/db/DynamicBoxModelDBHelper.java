package com.paypad.parator.db;

import com.paypad.parator.model.DynamicBoxModel;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.pojo.BaseResponse;

import java.util.Date;

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

        try{
            DynamicBoxModel dynamicBoxModel = getDynamicBoxModelById(id);

            realm.beginTransaction();

            dynamicBoxModel.setDeleted(true);
            dynamicBoxModel.setDeleteDate(new Date());

            realm.copyToRealm(dynamicBoxModel);

            realm.commitTransaction();
        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }

        return baseResponse;
    }

    public static DynamicBoxModel getDynamicBoxModel(long structId, long itemId, String userId){
        Realm realm = Realm.getDefaultInstance();
        DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                .equalTo("structId", structId)
                .equalTo("itemId", itemId)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false).findFirst();
        return dynamicBoxModel;
    }

    public static DynamicBoxModel getDynamicBoxModelById(String id){
        Realm realm = Realm.getDefaultInstance();
        DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                .equalTo("id", id)
                .equalTo("isDeleted", false)
                .findFirst();
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
                    //baseResponse.setMessage("Dynamic box is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Dynamic box cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static int getCurrentSequenceNumber(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(DynamicBoxModel.class).max("sequenceNumber");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }
}
