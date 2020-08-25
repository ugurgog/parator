package com.paypad.vuk507.db;

import com.paypad.vuk507.model.OrderItem;
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
                    for(OrderItem orderItem : saleModel.getOrderItems())
                        realm.insertOrUpdate(orderItem);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Some of Order Items cannot be saved!");
                }
            }
        });

        return baseResponse;
    }

    public static RealmResults<OrderItem> getSaleItemsBySaleId(String orderId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrderItem> orderItems = realm.where(OrderItem.class)
                .equalTo("orderId", orderId)
                .findAll();
        return orderItems;
    }

}
