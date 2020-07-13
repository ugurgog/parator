package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.model.Sale;

import java.util.ArrayList;
import java.util.UUID;

public class SaleModelInstance {

    private static SaleModelInstance instance = null;
    private static SaleModel saleModel;

    public static SaleModelInstance getInstance(){

        if(instance == null) {
            saleModel = new SaleModel();
            saleModel.getSale().setSaleUuid(UUID.randomUUID().toString());
            instance = new SaleModelInstance();
        }
        return instance;
    }

    public static synchronized void reset(){
        instance = null;
    }

    public static void setInstance(SaleModelInstance instance) {
        SaleModelInstance.instance = instance;
    }

    public SaleModel getSaleModel() {
        return saleModel;
    }

    public static void setSaleModel(SaleModel saleModel) {
        SaleModelInstance.saleModel = saleModel;
    }
}
