package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class SaleModel implements Serializable {

    private Sale sale;
    private List<SaleItem> saleItems;
    private List<Transaction> transactions;

    public SaleModel() {
        sale = new Sale();
        sale.setDiscounts(new RealmList<>());
        setTransactions(new ArrayList<>());
        setSaleItems(new ArrayList<>());
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
