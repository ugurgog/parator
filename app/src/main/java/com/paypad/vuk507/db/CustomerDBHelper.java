package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.CustomerGroup;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.List;

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

    public static void deleteCustomer(long customerId, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    Customer customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

                    if(customer != null){
                        customer.deleteFromRealm();


                        GroupDBHelper.deleteCustomerFromGroups(customerId, new CompleteCallback() {
                            @Override
                            public void onComplete(BaseResponse baseResponse) {
                                //TODO - musteriler silindi mi?
                            }
                        });

                    }else {
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("Customer could not be found !!");
                    }

                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer cannot be deleted !!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static Customer getCustomer(long id){
        Realm realm = Realm.getDefaultInstance();
        Customer customer = realm.where(Customer.class).equalTo("id", id).findFirst();
        return customer;
    }

    public static void createOrUpdateCustomer(Customer customer, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(customer);

                    baseResponse.setObject(customer);
                    baseResponse.setMessage("Customer is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
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
