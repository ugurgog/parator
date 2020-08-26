package com.paypad.vuk507.db;

import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProductDBHelper {

    public static RealmResults<Product> getAllProducts(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("userId", userId)
                .equalTo("isDeleted", false)
                .findAll();
        return products;
    }

    public static RealmResults<Product> getProductsByCategoryId(long categoryId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("categoryId", categoryId)
                .equalTo("isDeleted", false)
                .findAll();
        return products;
    }

    public static RealmResults<Product> getProductsByUnitId(long unitId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("unitId", unitId)
                .equalTo("isDeleted", false)
                .findAll();
        return products;
    }

    public static RealmResults<Product> getProductsByTaxId(long taxId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("taxId", taxId)
                .equalTo("isDeleted", false)
                .findAll();
        return products;
    }

    public static BaseResponse deleteProduct(long id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            Product product = getProduct(id);

            realm.beginTransaction();

            product.setDeleted(true);
            product.setDeleteDate(new Date());
            realm.copyToRealm(product);

            realm.commitTransaction();
        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }
        return baseResponse;
    }

    public static Product getProduct(long id){
        Realm realm = Realm.getDefaultInstance();
        Product product = realm.where(Product.class)
                .equalTo("id", id)
                .equalTo("isDeleted", false).findFirst();
        return product;
    }

    public static BaseResponse createOrUpdateProduct(Product product) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(product);

                    baseResponse.setObject(product);
                    //baseResponse.setMessage("Product is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Product cannot be saved!");
                }
            }
        });

        return baseResponse;
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
