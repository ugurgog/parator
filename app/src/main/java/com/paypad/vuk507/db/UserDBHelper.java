package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.User;

import java.util.Date;

import io.realm.Realm;

public class UserDBHelper {

    public static User getUserByUsernameAndPassword(String username, String password){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(User.class)
                .equalTo("username", username)
                .equalTo("password", password)
                .findFirst();
    }

    public static User getUserByUsername(String username){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(User.class)
                .equalTo("username", username)
                .findFirst();
    }

    public static void createUser(String username, String password, boolean isLoggedIn, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    User user = new User();
                    user.setCreateDate(new Date());
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setLoggedIn(isLoggedIn);

                    realm.insertOrUpdate(user);

                    baseResponse.setObject(user);
                    baseResponse.setMessage("User is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("User cannot be updated!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void updateUserLoggedInStatus(String username, boolean loggedIn, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
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
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}
