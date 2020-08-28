package com.paypad.parator.db;

import android.content.Context;

import com.paypad.parator.login.utils.LoginUtils;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;

import io.realm.Realm;

public class UserDBHelper {

    public static User getUserByEmailAndPassword(String email, String password){
        Realm realm = Realm.getDefaultInstance();
        User user =  realm.where(User.class)
                .equalTo("email", email)
                .equalTo("password", password)
                .findFirst();
        //realm.close();
        return user;
    }

    public static User getUserByEmail(String email){
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("email", email).findFirst();
        return user;
    }

    public static User getUserById(String id){
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("id", id).findFirst();
        return user;
    }

    public static User getUserFromCache(Context context){
        String userId = LoginUtils.getUserIdFromCache(context);
        return getUserById(userId);
    }

    public static BaseResponse createOrUpdateUser(User user) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(user);

                    baseResponse.setObject(user);
                    //baseResponse.setMessage("User is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("User cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static BaseResponse updateUserLoggedInStatus(String userId, boolean loggedIn){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    User user = getUserById(userId);
                    user.setLoggedIn(loggedIn);

                    realm.insertOrUpdate(user);

                    baseResponse.setObject(user);
                    //baseResponse.setMessage("User is updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("User cannot be updated!");
                }
            }
        });
        return baseResponse;
    }
}
