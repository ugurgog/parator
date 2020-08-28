package com.paypad.parator.model.pojo;

import com.paypad.parator.model.Order;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class SaleModel implements Serializable {

    private Order order;
    private List<OrderItem> orderItems;
    private List<Transaction> transactions;

    public SaleModel() {
        order = new Order();
        order.setDiscounts(new RealmList<>());
        setTransactions(new ArrayList<>());
        setOrderItems(new ArrayList<>());
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
