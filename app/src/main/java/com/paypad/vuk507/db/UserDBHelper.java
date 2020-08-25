package com.paypad.vuk507.db;

import android.content.Context;

import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;

public class UserDBHelper {

    public static User getUserByUsernameAndPassword(String username, String password){
        Realm realm = Realm.getDefaultInstance();
        User user =  realm.where(User.class)
                .equalTo("username", username)
                .equalTo("password", password)
                .findFirst();
        //realm.close();
        return user;
    }

    public static User getUserByUsername(String username){
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("username", username).findFirst();
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
                    baseResponse.setMessage("User is saved/updated!");
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
                    baseResponse.setMessage("User is updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("User cannot be updated!");
                }
            }
        });
        return baseResponse;
    }
}
