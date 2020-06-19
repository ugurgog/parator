package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.CustomerGroup;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class GroupDBHelper {

    public static Group getGroup(long id){
        Realm realm = Realm.getDefaultInstance();
        Group group = realm.where(Group.class).equalTo("id", id).findFirst();
        return group;
    }

    public static RealmResults<Group> getUserGroups(String userUuid){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Group> customerGroup = realm.where(Group.class).equalTo("userUuid", userUuid).findAll();
        return customerGroup;
    }

    public static int getGroupCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Group.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }


    //******************* Creation
    public static void createOrUpdateGroup(Group group, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(group);

                    baseResponse.setObject(group);
                    baseResponse.setMessage("Group is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Gropo cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void deleteGroup(long groupId, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Group group = realm.where(Group.class).equalTo("id", groupId).findFirst();

                    try{
                        group.deleteFromRealm();
                        baseResponse.setMessage("Group deleted successfully");
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("Group could not be deleted !!");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Group could not be deleted !!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void deleteCustomerFromGroups(long customerId, CompleteCallback completeCallback){
        //TODO - customer i tum gruplardan silecegiz.
    }
}
