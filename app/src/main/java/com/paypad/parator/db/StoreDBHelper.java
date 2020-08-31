package com.paypad.parator.db;

import com.paypad.parator.model.Store;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;

import io.realm.Realm;

public class StoreDBHelper {

    public static Store getStoreById(String id){
        Realm realm = Realm.getDefaultInstance();
        Store store =  realm.where(Store.class)
                .equalTo("id", id)
                .findFirst();
        return store;
    }

    public static Store getStoreByUserId(String userId){
        Realm realm = Realm.getDefaultInstance();
        Store store =  realm.where(Store.class)
                .equalTo("userId", userId)
                .findFirst();
        return store;
    }

    public static BaseResponse createOrUpdateStore(Store store) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(store);

                    baseResponse.setObject(store);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Store cannot be saved!");
                }
            }
        });
        return baseResponse;
    }
}
