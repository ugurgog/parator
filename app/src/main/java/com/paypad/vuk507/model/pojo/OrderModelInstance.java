package com.paypad.vuk507.model.pojo;

public class OrderModelInstance {

    private static OrderModelInstance instance = null;
    private static OrderModel orderModel;

    public static OrderModelInstance getInstance(){

        if(instance == null) {
            orderModel = new OrderModel();
            instance = new OrderModelInstance();
        }
        return instance;
    }

    public static synchronized void reset(){
        instance = null;
    }

    public static void setInstance(OrderModelInstance instance) {
        OrderModelInstance.instance = instance;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public static void setOrderModel(OrderModel orderModel) {
        OrderModelInstance.orderModel = orderModel;
    }
}
