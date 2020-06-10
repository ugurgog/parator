package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.Discount;

import io.realm.Realm;
import io.realm.RealmResults;

public class DiscountDBHelper {

    public static RealmResults<Discount> getAllDiscounts(String username){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Discount> discounts = realm.where(Discount.class)
                .equalTo("createUsername", username)
                .findAll();
        return discounts;
    }

    public static void deleteDiscount(long id, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Discount discount = realm.where(Discount.class).equalTo("id", id).findFirst();
                    discount.deleteFromRealm();
                    baseResponse.setMessage("Discount deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Discount cannot be deleted");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static Discount getDiscount(int id){
        Realm realm = Realm.getDefaultInstance();
        Discount discount = realm.where(Discount.class).equalTo("id", id).findFirst();
        return discount;
    }

    public static void createOrUpdateDiscount(Discount discount, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    /*if(discount.getId() == 0){
                        Number currentIdNum = realm.where(Discount.class).max("id");
                        int nextId;
                        if(currentIdNum == null) {
                            nextId = 1;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }
                        discount.setId(nextId);
                    }*/

                    realm.insertOrUpdate(discount);

                    baseResponse.setObject(discount);
                    baseResponse.setMessage("Discount is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Discount cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static int getCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Discount.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void createDiscount(Discount discount, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Number currentIdNum = realm.where(Discount.class).max("id");
                    int nextId;
                    if(currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    discount.setId(nextId);

                    realm.insertOrUpdate(discount);

                    baseResponse.setObject(discount);
                    baseResponse.setMessage("Discount is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Category cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void updateDiscount(Discount discount, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(discount);

                    baseResponse.setObject(discount);
                    baseResponse.setMessage("Discount is updated!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Discount cannot be updated!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }
}
