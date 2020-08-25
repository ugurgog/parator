package com.paypad.vuk507.db;

import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class TransactionDBHelper {

    public static BaseResponse createOrUpdateTransaction(Transaction transaction) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(transaction);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Transaction cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static BaseResponse deleteTransactionsOfSale(String orderId){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                try{
                    RealmResults<Transaction> transactions = realm.where(Transaction.class).equalTo("orderId", orderId).findAll();

                    for(Transaction transaction : transactions)
                        transaction.deleteFromRealm();

                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Some of Transactions cannot be deleted");
                }
            }
        });
        return baseResponse;
    }

    public static BaseResponse deleteTransactionById(String id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                try{
                    Transaction transaction = realm.where(Transaction.class)
                            .equalTo("id", id).findFirst();
                    transaction.deleteFromRealm();
                    baseResponse.setMessage("Transaction deleted successfully");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Transaction cannot be deleted");
                }
            }
        });
        return baseResponse;
    }

    public static RealmResults<Transaction> getTransactionsBySaleId(String orderId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Transaction> transactions = realm.where(Transaction.class)
                .equalTo("orderId", orderId)
                .findAll();
        return transactions;
    }

    public static RealmResults<Transaction> getTransactionsByZNum(long zNum){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Transaction> transactions = realm.where(Transaction.class)
                .equalTo("zNum", zNum)
                .equalTo("isEODProcessed", false)
                .findAll();
        return transactions;
    }

    public static Transaction getTransactionById(String id){
        Realm realm = Realm.getDefaultInstance();
        Transaction transaction = realm.where(Transaction.class).equalTo("id", id).findFirst();
        return transaction;
    }
}
