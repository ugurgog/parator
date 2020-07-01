package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;

import io.realm.Realm;

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


    public static Sale getSaleById(String saleId){
        Realm realm = Realm.getDefaultInstance();
        Sale sale = realm.where(Sale.class).equalTo("saleUuid", saleId).findFirst();
        return sale;
    }

}
