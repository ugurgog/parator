package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;

import io.realm.Realm;

public class SaleDBHelper {

    public static void createOrUpdateSale(SaleModel saleModel, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();
        final boolean[] saleInsertFailed = {false};

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(saleModel.getSale());
                    saleInsertFailed[0] = true;
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Sale cannot be saved!");
                    completeCallback.onComplete(baseResponse);
                }
            }
        });
        if(saleInsertFailed[0] = true)
            saveSaleItems(saleModel, completeCallback);
    }

    private static void saveSaleItems(SaleModel saleModel, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);

                try{
                    for(SaleItem saleItem : saleModel.getSaleItems())
                        realm.insertOrUpdate(saleItem);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Some of Sale Items cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

}
