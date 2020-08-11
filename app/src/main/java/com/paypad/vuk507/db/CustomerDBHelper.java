package com.paypad.vuk507.db;

import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmResults;

public class CustomerDBHelper {

    public static RealmResults<Customer> getAllCustomers(String userUuid){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Customer> customers = realm.where(Customer.class)
                .equalTo("userUuid" , userUuid)
                .findAll();
        return customers;
    }

    public static BaseResponse deleteCustomer(long customerId){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    Customer customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

                    if(customer != null){
                        customer.deleteFromRealm();
                        baseResponse.setMessage("Customer deleted successfully");

                    }else {
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("Customer could not be found !!");
                    }

                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer cannot be deleted !!");
                }
            }
        });
        return baseResponse;
    }

    public static Customer getCustomer(long id){
        Realm realm = Realm.getDefaultInstance();
        Customer customer = realm.where(Customer.class).equalTo("id", id).findFirst();
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
