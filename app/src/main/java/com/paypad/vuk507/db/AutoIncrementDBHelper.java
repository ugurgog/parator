package com.paypad.vuk507.db;


import com.paypad.vuk507.model.AutoIncrement;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import java.util.Date;

import io.realm.Realm;

public class AutoIncrementDBHelper {

    public static AutoIncrement getAutoIncrementByUserId(String userId){
        Realm realm = Realm.getDefaultInstance();
        AutoIncrement autoIncrement = realm.where(AutoIncrement.class).equalTo("userId", userId).findFirst();
        return autoIncrement;
    }

    public static BaseResponse updateAutoIncrement(AutoIncrement autoIncrement) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(autoIncrement);

                    baseResponse.setObject(autoIncrement);
                    //baseResponse.setMessage("AutoIncrement is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("AutoIncrement cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static BaseResponse updateZnumByNextValue(AutoIncrement autoIncrement){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        AutoIncrement autoIncrement1 = realm.copyFromRealm(autoIncrement);

        if(autoIncrement1.getzNum() < 999999)
            autoIncrement1.setzNum(autoIncrement1.getzNum() + 1);
        else
            autoIncrement1.setzNum(1);

        autoIncrement1.setfNum(1); //Gunsonu alindiktan sonra sira no 1 den baslar

        realm.commitTransaction();

        return updateAutoIncrement(autoIncrement1);
    }

    public static BaseResponse updateFnumByNextValue(AutoIncrement autoIncrement){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        AutoIncrement autoIncrement1 = realm.copyFromRealm(autoIncrement);

        if(autoIncrement1.getfNum() < 999999)
            autoIncrement1.setfNum(autoIncrement1.getfNum() + 1);
        else
            autoIncrement1.setfNum(1);

        realm.commitTransaction();

        return updateAutoIncrement(autoIncrement1);
    }

    public static BaseResponse updateOrderNumByNextValue(AutoIncrement autoIncrement){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        AutoIncrement autoIncrement1 = realm.copyFromRealm(autoIncrement);

        if(autoIncrement1.getOrderNumCounter() < 999999)
            autoIncrement1.setOrderNumCounter(autoIncrement1.getOrderNumCounter() + 1);
        else
            autoIncrement1.setOrderNumCounter(1);

        realm.commitTransaction();

        return updateAutoIncrement(autoIncrement1);
    }
}
