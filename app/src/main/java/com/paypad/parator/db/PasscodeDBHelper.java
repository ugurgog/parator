package com.paypad.parator.db;

import com.paypad.parator.model.Discount;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.pojo.BaseResponse;

import io.realm.Realm;

public class PasscodeDBHelper {

    public static Passcode getPasscodeById(String id){
        Realm realm = Realm.getDefaultInstance();
        Passcode passcode = realm.where(Passcode.class)
                .equalTo("id", id)
                .findFirst();
        return passcode;
    }

    public static Passcode getPasscodeByUserId(String userId){
        Realm realm = Realm.getDefaultInstance();
        Passcode passcode = realm.where(Passcode.class)
                .equalTo("userId", userId)
                .findFirst();
        return passcode;
    }

    public static BaseResponse createOrUpdatePasscode(Passcode passcode) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(passcode);
                    baseResponse.setObject(passcode);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Passcode cannot be saved!");
                }
            }
        });
        return baseResponse;
    }
}
