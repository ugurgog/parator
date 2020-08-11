package com.paypad.vuk507.db;

import android.annotation.SuppressLint;
import android.util.Log;

import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SaleDBHelper {

    public static BaseResponse createOrUpdateSale(Sale sale) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(sale);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Sale cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static Sale getSaleById(String saleId){
        Realm realm = Realm.getDefaultInstance();
        Sale sale = realm.where(Sale.class).equalTo("saleUuid", saleId).findFirst();
        return sale;
    }

    public static RealmResults<Sale> getOrdersByUserId(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Sale> saleList = realm.where(Sale.class).equalTo("userUuid", userId).findAll();
        return saleList;
    }

    public static RealmResults<Sale> getOrdersByZNum(long zNum){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Sale> saleList = realm.where(Sale.class).equalTo("zNum", zNum).findAll();
        return saleList;
    }

    public static RealmResults<Sale> getOrdersByUserIdAndDeviceId(String userId, String deviceId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Sale> saleList = realm.where(Sale.class)
                .equalTo("userUuid", userId)
                .equalTo("deviceId", deviceId)
                .findAll();
        return saleList;
    }

    public static SaleModel getSaleModelBySaleId(String saleId){
        SaleModel saleModel = new SaleModel();

        saleModel.setSale(getSaleById(saleId));

        List<SaleItem> saleItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
        saleModel.setSaleItems(saleItems);

        List<Transaction> transactions = TransactionDBHelper.getTransactionsBySaleId(saleId);
        saleModel.setTransactions(transactions);

        return saleModel;
    }

    public static List<SaleModel> getSaleModelsForTransactionList(String userId){

        List<SaleModel> saleModels = new ArrayList<>();
        List<Sale> sales = getOrdersByUserId(userId);

        for(Sale sale : sales){

            LogUtil.logSale(sale);

            if(sale.isPaymentCompleted()){
                SaleModel saleModel = new SaleModel();

                String saleId = sale.getSaleUuid();

                saleModel.setSale(sale);

                List<SaleItem> saleItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
                saleModel.setSaleItems(saleItems);

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
        RealmResults<Sale> saleList;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Log.i("Info", "getSaleModelsForReport   "
                + simpleDateFormat.format(startDate) + "  " + simpleDateFormat.format(endDate) );

        if(deviceId != null){
            saleList = realm.where(Sale.class)
                    .equalTo("userUuid", userId)
                    .equalTo("deviceId", deviceId)
                    .between("createDate", startDate, endDate)
                    .findAll();
        }else{
            saleList = realm.where(Sale.class)
                    .equalTo("userUuid", userId)
                    .between("createDate", startDate, endDate)
                    .findAll();
        }

        for(Sale sale : saleList){

            LogUtil.logSale(sale);

            if(sale.isPaymentCompleted()){
                SaleModel saleModel = new SaleModel();

                String saleId = sale.getSaleUuid();

                saleModel.setSale(sale);

                List<SaleItem> saleItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
                saleModel.setSaleItems(saleItems);

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
        RealmResults<Sale> saleList;

        saleList = realm.where(Sale.class)
                .equalTo("userUuid", userId)
                .equalTo("isEndOfDayProcessed", false)
                .equalTo("paymentCompleted", true)
                .findAll();

        for(Sale sale : saleList){

            LogUtil.logSale(sale);

            if(sale.isPaymentCompleted()){
                SaleModel saleModel = new SaleModel();

                String saleId = sale.getSaleUuid();

                saleModel.setSale(sale);

                List<SaleItem> saleItems = SaleItemDBHelper.getSaleItemsBySaleId(saleId);
                saleModel.setSaleItems(saleItems);

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
                    RealmResults<Sale> sales = realm.where(Sale.class).findAll();
                    sales.deleteAllFromRealm();
                }catch (Exception e){

                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    RealmResults<SaleItem> saleItems = realm.where(SaleItem.class).findAll();
                    saleItems.deleteAllFromRealm();
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
