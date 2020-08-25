package com.paypad.vuk507.db;

import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class OrderRefundItemDBHelper {

    public static RealmResults<OrderRefundItem> getRefundItemsByOrderId(String orderId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrderRefundItem> refundItems = realm.where(OrderRefundItem.class)
                .equalTo("orderId", orderId)
                .findAll();
        return refundItems;
    }

    public static OrderRefundItem getRefundItemById(String id){
        Realm realm = Realm.getDefaultInstance();
        OrderRefundItem orderRefundItem = realm.where(OrderRefundItem.class).equalTo("id", id).findFirst();
        return orderRefundItem;
    }

    public static RealmResults<OrderRefundItem> getRefundItemsByRefundGroupId(String refundGroupId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrderRefundItem> refundItems = realm.where(OrderRefundItem.class)
                .equalTo("refundGroupId", refundGroupId)
                .findAll();
        return refundItems;
    }

    public static BaseResponse createOrUpdateRefundItem(OrderRefundItem orderRefundItem) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(orderRefundItem);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("OrderRefundItem cannot be saved!");
                }
            }
        });
        return baseResponse;
    }
}
