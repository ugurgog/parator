package com.paypad.vuk507.db;

import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.CustomerGroup;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.pojo.BaseResponse;

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
        Customer customer = getCustomer(customerId, userId);
        realm.beginTransaction();

        customer.setDeleted(true);

        realm.commitTransaction();

        BaseResponse baseResponse = createOrUpdateCustomer(customer);

        if(baseResponse.isSuccess()){
            BaseResponse baseResponse1 = CustomerGroupDBHelper.deleteCustomerGroupsByCustomerId(customerId, userId);
            return baseResponse1;
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
                    baseResponse.setMessage("Customer is saved!");
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
