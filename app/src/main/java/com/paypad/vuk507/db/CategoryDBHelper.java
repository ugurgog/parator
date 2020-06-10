package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.Category;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryDBHelper {

    public static RealmResults<Category> getAllCategories(String username){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Category> categories = realm.where(Category.class)
                .equalTo("createUsername", username)
                .findAll();
        //realm.close();
        return categories;
    }

    public static void deleteCategory(long id, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Category category = realm.where(Category.class).equalTo("id", id).findFirst();
                    category.deleteFromRealm();
                    baseResponse.setMessage("Category deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static Category getCategory(long id){
        Realm realm = Realm.getDefaultInstance();
        Category category = realm.where(Category.class).equalTo("id", id).findFirst();
        //realm.close();
        return category;
    }

    public static void createCategory(String categoryName, String username, CompleteCallback completeCallback) {
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
                    category.setCreateUsername(username);

                    realm.insertOrUpdate(category);

                    baseResponse.setObject(category);
                    baseResponse.setMessage("Category is saved/updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be updated!");
                }
                //realm.close();
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
                //realm.close();
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}
