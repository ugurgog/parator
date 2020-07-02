package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class DynamicBoxModelDBHelper {

    public static RealmResults<DynamicBoxModel> getAllDynamicBoxes(String uuid){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DynamicBoxModel> dynamicBoxModels = realm.where(DynamicBoxModel.class)
                .equalTo("createUserId", uuid)
                .findAll();
        return dynamicBoxModels;
    }

    public static void deleteDynamicBoxByStructId(long id, String uuid, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                            .equalTo("structId", id)
                            .equalTo("createUserId", uuid)
                            .findFirst();

                    dynamicBoxModel.deleteFromRealm();
                    baseResponse.setMessage("Dynamic Box deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Dynamic Box cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void deleteDynamicBoxByStructAndItemId(long structId, long itemId, String uuid, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                            .equalTo("structId", structId)
                            .equalTo("createUserId", uuid)
                            .equalTo("itemId", itemId)
                            .findFirst();

                    dynamicBoxModel.deleteFromRealm();
                    baseResponse.setMessage("Dynamic Box deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Dynamic Box cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static DynamicBoxModel getDynamicBoxModel(long structId, long itemId, String uuid){
        Realm realm = Realm.getDefaultInstance();
        DynamicBoxModel dynamicBoxModel = realm.where(DynamicBoxModel.class)
                .equalTo("structId", structId)
                .equalTo("itemId", itemId)
                .equalTo("createUserId", uuid).findFirst();
        return dynamicBoxModel;
    }

    public static void createOrUpdateDynamicBox(DynamicBoxModel dynamicBoxModel, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(dynamicBoxModel);
                    baseResponse.setObject(dynamicBoxModel);
                    baseResponse.setMessage("Dynamic box is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Dynamic box cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}
