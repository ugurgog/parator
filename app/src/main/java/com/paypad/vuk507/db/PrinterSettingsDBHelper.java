package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.PrinterSettings;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;

public class PrinterSettingsDBHelper {

    public static PrinterSettings getPrinterSetting(String userId){
        Realm realm = Realm.getDefaultInstance();
        PrinterSettings printerSettings = realm.where(PrinterSettings.class).equalTo("userId", userId).findFirst();
        return printerSettings;
    }

    public static BaseResponse updatePrinterSettings(PrinterSettings printerSettings) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(printerSettings);

                    baseResponse.setObject(printerSettings);
                    baseResponse.setMessage("PrinterSettings is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("PrinterSettings cannot be updated!");
                }
            }
        });

        return baseResponse;
    }
}
