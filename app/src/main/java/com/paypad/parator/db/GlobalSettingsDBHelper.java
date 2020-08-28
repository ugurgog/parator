package com.paypad.parator.db;

import com.paypad.parator.model.GlobalSettings;
import com.paypad.parator.model.pojo.BaseResponse;

import io.realm.Realm;

public class GlobalSettingsDBHelper {

    public static GlobalSettings getPrinterSetting(String userId){
        Realm realm = Realm.getDefaultInstance();
        GlobalSettings globalSettings = realm.where(GlobalSettings.class).equalTo("userId", userId).findFirst();
        return globalSettings;
    }

    public static BaseResponse updatePrinterSettings(GlobalSettings globalSettings) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(globalSettings);

                    baseResponse.setObject(globalSettings);
                    //baseResponse.setMessage("GlobalSettings is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("GlobalSettings cannot be updated!");
                }
            }
        });

        return baseResponse;
    }
}
