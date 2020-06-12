package com.paypad.vuk507.db;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class DynamicBoxModelDBHelper {

    public static RealmResults<DynamicBoxModel> getAllDynamicBoxes(String uuid){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DynamicBoxModel> dynamicBoxModels = realm.where(DynamicBoxModel.class)
                .equalTo("userUuid", uuid)
                .findAll();
        return dynamicBoxModels;
    }
}
