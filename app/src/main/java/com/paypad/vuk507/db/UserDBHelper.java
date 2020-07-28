package com.paypad.vuk507.db;

import android.content.Context;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.User;

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
        User user = realm.where(User.class)
                .equalTo("username", username)
                .findFirst();
        //realm.close();
        return user;
    }

    public static User getUserFromCache(Context context){
        String username = LoginUtils.getUsernameFromCache(context);
        return getUserByUsername(username);
    }

    public static BaseResponse createUser(String username, String password, String uuid, boolean isLoggedIn) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    User user = realm.createObject(User.class);

                    //User user = new User();
                    user.setCreateDate(new Date());
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setLoggedIn(isLoggedIn);
                    user.setUuid(uuid);

                    //realm.insertOrUpdate(user);

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

    public static BaseResponse updateUserLoggedInStatus(String username, boolean loggedIn){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    User user = getUserByUsername(username);
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
