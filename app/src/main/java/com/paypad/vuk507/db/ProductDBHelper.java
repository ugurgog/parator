package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.Product;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProductDBHelper {

    public static RealmResults<Product> getAllProducts(String userUuid){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("userUuid", userUuid)
                .findAll();
        return products;
    }

    public static RealmResults<Product> getProductsByCategoryId(long categoryId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("categoryId", categoryId)
                .findAll();
        return products;
    }


    public static void deleteProduct(long id, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Product product = realm.where(Product.class).equalTo("id", id).findFirst();
                    product.deleteFromRealm();
                    baseResponse.setMessage("Product deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Product cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static Product getProduct(long id){
        Realm realm = Realm.getDefaultInstance();
        Product product = realm.where(Product.class).equalTo("id", id).findFirst();
        return product;
    }

    public static void createOrUpdateProduct(Product product, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(product);

                    baseResponse.setObject(product);
                    baseResponse.setMessage("Product is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Product cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static int getCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Product.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }
}
