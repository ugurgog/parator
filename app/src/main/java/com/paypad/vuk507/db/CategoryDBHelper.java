package com.paypad.vuk507.db;

import androidx.annotation.NonNull;

import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryDBHelper {

    public static RealmResults<Category> getAllCategories(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Category> categories = realm.where(Category.class)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findAll();
        //realm.close();
        return categories;
    }

    public static BaseResponse deleteCategory(long id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            Category category = getCategory(id);

            realm.beginTransaction();

            category.setDeleted(true);
            category.setDeleteDate(new Date());

            realm.copyToRealm(category);

            realm.commitTransaction();
        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }

        return baseResponse;





        /*realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                try{
                    Category category = realm.where(Category.class).equalTo("id", id).findFirst();

                    try{
                        category.deleteFromRealm();
                        baseResponse.setMessage("Category deleted successfully");
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("Category cannot be deleted");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be deleted");
                }
            }
        });
        return baseResponse;*/
    }

    public static Category getCategory(long id){
        Realm realm = Realm.getDefaultInstance();
        Category category = realm.where(Category.class)
                .equalTo("id", id)
                .equalTo("isDeleted", false)
                .findFirst();
        //realm.close();
        return category;
    }

    public static BaseResponse createOrUpdateCategory(Category category) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(category);

                    baseResponse.setObject(category);
                    baseResponse.setMessage("Category is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static int getCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Category.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }
}
