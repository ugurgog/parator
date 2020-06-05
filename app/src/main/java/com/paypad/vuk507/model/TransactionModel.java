package com.paypad.vuk507.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TransactionModel extends RealmObject {

    @PrimaryKey
    private long id;

    private int saleCount;
    private double totalAmount;

    private RealmList<Product> products;
    private RealmList<Discount> discounts;
    private RealmList<AmountModel> amounts;


    private Date createDate;
    private String createUsername;
    private boolean paymentStatus;



}
