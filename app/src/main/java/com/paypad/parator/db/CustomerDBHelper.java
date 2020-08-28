package com.paypad.parator.db;

import com.paypad.parator.model.Customer;
import com.paypad.parator.model.pojo.BaseResponse;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CustomerDBHelper {

    public static RealmResults<Customer> getAllCustomers(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Customer> customers = realm.where(Customer.class)
                .equalTo("userId" , userId)
                .equalTo("isDeleted" , false)
                .findAll();
        return customers;
    }

    public static BaseResponse deleteCustomer(long customerId, String userId){

        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            Customer customer = getCustomer(customerId, userId);
            realm.beginTransaction();

            customer.setDeleted(true);
            customer.setDeleteDate(new Date());

            realm.copyToRealm(customer);

            realm.commitTransaction();

            baseResponse = CustomerGroupDBHelper.deleteCustomerGroupsByCustomerId(customerId, userId);

        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Unexpected error:" + e.getMessage());
        }

        return baseResponse;
    }

    public static Customer getCustomer(long id, String userId){
        Realm realm = Realm.getDefaultInstance();
        Customer customer = realm.where(Customer.class)
                .equalTo("id", id)
                .equalTo("isDeleted", false)
                .equalTo("userId", userId)
                .findFirst();
        return customer;
    }

    public static BaseResponse createOrUpdateCustomer(Customer customer) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(customer);

                    baseResponse.setObject(customer);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static int getCustomerCurrentPrimaryKeyId(){
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Customer.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

}
