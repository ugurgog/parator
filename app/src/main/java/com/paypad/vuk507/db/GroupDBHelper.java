package com.paypad.vuk507.db;

import androidx.annotation.NonNull;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
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
    public static BaseResponse createOrUpdateGroup(Group group) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(group);

                    baseResponse.setObject(group);
                    baseResponse.setMessage("Group is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Group cannot be saved!");
                }
            }
        });
        return baseResponse;
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

    public static void deleteCustomerFromGroups(long customerId, String uuid, CompleteCallback completeCallback){
        RealmResults<Group> allGroups = GroupDBHelper.getUserGroups(uuid);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);

                try{
                    for(Group group : allGroups){

                        int index = 0;

                        for(Long foundCustomerId : group.getCustomerIds()){
                            if(foundCustomerId == customerId){
                                group.getCustomerIds().remove(index);
                                realm.insertOrUpdate(group);
                                break;
                            }
                            index++;
                        }
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer could not deleted in groups!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void deleteCustomerFromAGroup(long customerId, Group group, CompleteCallback completeCallback){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);

                try{
                    int index = 0;
                    for (Long customerIdFound : group.getCustomerIds()) {
                        if (customerIdFound == customerId) {
                            group.getCustomerIds().remove(index);

                            realm.insertOrUpdate(group);
                            baseResponse.setObject(group);
                            break;
                        }
                        index++;
                    }

                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer could not be deleted from group");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}
