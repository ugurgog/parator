package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SaleDBHelper {

    public static BaseResponse createOrUpdateSale(SaleModel saleModel) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(saleModel.getSale());
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

    public static List<SaleModel> getSaleModelsByUserId(String userId){

        List<SaleModel> saleModels = new ArrayList<>();

        for(Sale sale : getOrdersByUserId(userId)){

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




}
