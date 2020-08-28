package com.paypad.parator.db;

import android.annotation.SuppressLint;
import android.util.Log;

import com.paypad.parator.model.Order;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModel;
import com.paypad.parator.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SaleDBHelper {

    public static BaseResponse createOrUpdateSale(Order order) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(order);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Order cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static Order getSaleById(String id){
        Realm realm = Realm.getDefaultInstance();
        Order order = realm.where(Order.class).equalTo("id", id).findFirst();
        return order;
    }

    public static RealmResults<Order> getOrdersByUserId(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Order> orderList = realm.where(Order.class).equalTo("userId", userId).findAll();
        return orderList;
    }

    public static RealmResults<Order> getOrdersByZNum(long zNum){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Order> orderList = realm.where(Order.class).equalTo("zNum", zNum).findAll();
        return orderList;
    }

    public static RealmResults<Order> getOrdersByUserIdAndDeviceId(String userId, String deviceId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Order> orderList = realm.where(Order.class)
                .equalTo("userId", userId)
                .equalTo("deviceId", deviceId)
                .findAll();
        return orderList;
    }

    public static SaleModel getSaleModelBySaleId(String saleId){
        SaleModel saleModel = new SaleModel();

        saleModel.setOrder(getSaleById(saleId));

        List<OrderItem> orderItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
        saleModel.setOrderItems(orderItems);

        List<Transaction> transactions = TransactionDBHelper.getTransactionsBySaleId(saleId);
        saleModel.setTransactions(transactions);

        return saleModel;
    }

    public static List<SaleModel> getSaleModelsForTransactionList(String userId){

        List<SaleModel> saleModels = new ArrayList<>();
        List<Order> orders = getOrdersByUserId(userId);

        for(Order order : orders){

            LogUtil.logSale(order);

            if(order.isPaymentCompleted()){
                SaleModel saleModel = new SaleModel();

                String saleId = order.getId();

                saleModel.setOrder(order);

                List<OrderItem> orderItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
                saleModel.setOrderItems(orderItems);

                List<Transaction> transactions = TransactionDBHelper.getTransactionsBySaleId(saleId);
                saleModel.setTransactions(transactions);

                saleModels.add(saleModel);
            }
        }
        return saleModels;
    }

    public static List<SaleModel> getSaleModelsForReport(String userId, String deviceId, Date startDate, Date endDate){

        List<SaleModel> saleModels = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Order> orderList;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Log.i("Info", "getSaleModelsForReport   "
                + simpleDateFormat.format(startDate) + "  " + simpleDateFormat.format(endDate) );

        if(deviceId != null){
            orderList = realm.where(Order.class)
                    .equalTo("userId", userId)
                    .equalTo("deviceId", deviceId)
                    .between("createDate", startDate, endDate)
                    .findAll();
        }else{
            orderList = realm.where(Order.class)
                    .equalTo("userId", userId)
                    .between("createDate", startDate, endDate)
                    .findAll();
        }

        for(Order order : orderList){

            LogUtil.logSale(order);

            if(order.isPaymentCompleted()){
                SaleModel saleModel = new SaleModel();

                String saleId = order.getId();

                saleModel.setOrder(order);

                List<OrderItem> orderItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
                saleModel.setOrderItems(orderItems);

                List<Transaction> transactions = TransactionDBHelper.getTransactionsBySaleId(saleId);
                saleModel.setTransactions(transactions);

                saleModels.add(saleModel);
            }
        }
        return saleModels;
    }


    public static List<SaleModel> getSaleModelsNotProcessedEOD(String userId){

        List<SaleModel> saleModels = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Order> orderList;

        orderList = realm.where(Order.class)
                .equalTo("userId", userId)
                .equalTo("isEndOfDayProcessed", false)
                .equalTo("paymentCompleted", true)
                .findAll();

        for(Order order : orderList){

            LogUtil.logSale(order);

            if(order.isPaymentCompleted()){
                SaleModel saleModel = new SaleModel();

                String saleId = order.getId();

                saleModel.setOrder(order);

                List<OrderItem> orderItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
                saleModel.setOrderItems(orderItems);

                List<Transaction> transactions = TransactionDBHelper.getTransactionsBySaleId(saleId);
                saleModel.setTransactions(transactions);

                saleModels.add(saleModel);
            }
        }
        return saleModels;
    }

    public static void deleteAllOrders(){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    RealmResults<Order> orders = realm.where(Order.class).findAll();
                    orders.deleteAllFromRealm();
                }catch (Exception e){

                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    RealmResults<OrderItem> orderItems = realm.where(OrderItem.class).findAll();
                    orderItems.deleteAllFromRealm();
                }catch (Exception e){

                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    RealmResults<Transaction> transactions = realm.where(Transaction.class).findAll();
                    transactions.deleteAllFromRealm();
                }catch (Exception e){

                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    RealmResults<Refund> refunds = realm.where(Refund.class).findAll();
                    refunds.deleteAllFromRealm();
                }catch (Exception e){

                }
            }
        });
    }
}
