package com.paypad.parator.model.pojo;

import com.paypad.parator.model.Order;
import com.paypad.parator.model.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class CancelPaymentModel implements Serializable {

    private Order order;
    private List<Transaction> transactions;
    private double remainAmount;

    public CancelPaymentModel() {
        order = new Order();
        order.setDiscounts(new RealmList<>());
        setTransactions(new ArrayList<>());
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public double getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(double remainAmount) {
        this.remainAmount = remainAmount;
    }
}
