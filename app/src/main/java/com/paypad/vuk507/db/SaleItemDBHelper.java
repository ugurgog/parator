package com.paypad.vuk507.db;

import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class SaleItemDBHelper {

    public static BaseResponse saveSaleItems(SaleModel saleModel) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    for(SaleItem saleItem : saleModel.getSaleItems())
                        realm.insertOrUpdate(saleItem);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Some of Sale Items cannot be saved!");
                }
            }
        });

        return baseResponse;
    }

    public static RealmResults<SaleItem> getSaleItemsBySaleId(String saleId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SaleItem> saleItems = realm.where(SaleItem.class)
                .equalTo("saleUuid", saleId)
                .findAll();
        return saleItems;
    }

}
