package com.paypad.parator.model.pojo;

import java.util.UUID;

public class CancelPaymentModelInstance {

    private static CancelPaymentModelInstance instance = null;
    private static CancelPaymentModel cancelPaymentModel;

    public static CancelPaymentModelInstance getInstance(){

        if(instance == null) {
            cancelPaymentModel = new CancelPaymentModel();
            cancelPaymentModel.getOrder().setId(UUID.randomUUID().toString());
            instance = new CancelPaymentModelInstance();
        }
        return instance;
    }

    public static synchronized void reset(){
        instance = null;
    }

    public void setInstance(CancelPaymentModelInstance instance) {
        CancelPaymentModelInstance.instance = instance;
    }

    public CancelPaymentModel getCancelPaymentModel() {
        return cancelPaymentModel;
    }

    public void setCancelPaymentModel(CancelPaymentModel cancelPaymentModel) {
        CancelPaymentModelInstance.cancelPaymentModel = cancelPaymentModel;
    }
}
