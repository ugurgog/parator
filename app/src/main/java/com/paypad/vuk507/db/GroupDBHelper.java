package com.paypad.vuk507.db;

import androidx.annotation.NonNull;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class GroupDBHelper {

    public static Group getGroup(long id, String userId){
        Realm realm = Realm.getDefaultInstance();
        Group group = realm.where(Group.class)
                .equalTo("id", id)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findFirst();
        return group;
    }

    public static RealmResults<Group> getUserGroups(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Group> customerGroup = realm.where(Group.class)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findAll();
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
                    //baseResponse.setMessage("Group is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Group cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static BaseResponse deleteGroup(long groupId, String userId){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            Group group = getGroup(groupId, userId);

            realm.beginTransaction();

            group.setDeleted(true);
            group.setDeleteDate(new Date());
            realm.copyToRealm(group);

            realm.commitTransaction();

            baseResponse = CustomerGroupDBHelper.deleteCustomerGroupsByGroupId(groupId, userId);
        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }
        return baseResponse;
    }
}
