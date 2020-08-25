package com.paypad.vuk507.db;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class DiscountDBHelper {

    public static RealmResults<Discount> getAllDiscounts(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Discount> discounts = realm.where(Discount.class)
                .equalTo("userId", userId)
                .findAll();
        return discounts;
    }

    public static BaseResponse deleteDiscount(long id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    Discount discount = realm.where(Discount.class).equalTo("id", id).findFirst();
                    discount.deleteFromRealm();
                    baseResponse.setMessage("Discount deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Discount cannot be deleted");
                }
            }
        });
        return baseResponse;
    }

    public static Discount getDiscountById(long id){
        Realm realm = Realm.getDefaultInstance();
        Discount discount = realm.where(Discount.class).equalTo("id", id).findFirst();
        return discount;
    }

    public static BaseResponse createOrUpdateDiscount(Discount discount) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(discount);

                    baseResponse.setObject(discount);
                    baseResponse.setMessage("Discount is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Discount cannot be saved!");
                }
            }
        });
        return baseResponse;
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
}
