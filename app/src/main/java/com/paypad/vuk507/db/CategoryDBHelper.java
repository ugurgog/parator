package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryDBHelper {

    public static RealmResults<Category> getAllCategories(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Category> categories = realm.where(Category.class).findAll();
        return categories;
    }

    public static void deleteCategory(int id){
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Category> results = realm.where(Category.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Category category = results.get(id);
                category.deleteFromRealm();
            }
        });
    }

    public static Category getCategory(int id){
        Realm realm = Realm.getDefaultInstance();
        Category category = realm.where(Category.class).equalTo("id", id).findFirst();
        return category;
    }

    public static void createCategory(String categoryName, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Number currentIdNum = realm.where(Category.class).max("id");
                    int nextId;
                    if(currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    Category category = new Category();
                    category.setId(nextId);
                    category.setName(categoryName);

                    realm.insertOrUpdate(category);

                    baseResponse.setObject(category);
                    baseResponse.setMessage("Category is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be updated!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void updateCategory(Category category, String name, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    category.setName(name);
                    realm.insertOrUpdate(category);

                    baseResponse.setObject(category);
                    baseResponse.setMessage("Category is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be updated!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}