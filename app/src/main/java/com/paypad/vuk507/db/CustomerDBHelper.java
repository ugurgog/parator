package com.paypad.vuk507.db;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.CustomerGroup;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.TaxModel;
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

                        deleteCustomerGroup(customerId, new CompleteCallback() {
                            @Override
                            public void onComplete(BaseResponse baseResponsex) {
                                if(baseResponsex.isSuccess()){
                                    baseResponse.setMessage("Customer deleted successfully");
                                }
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

    public static CustomerGroup getCustomerGroup(long customerId){
        Realm realm = Realm.getDefaultInstance();
        CustomerGroup customerGroup = realm.where(CustomerGroup.class).equalTo("customerId", customerId).findFirst();
        return customerGroup;
    }

    public static Group getGroup(long id){
        Realm realm = Realm.getDefaultInstance();
        Group group = realm.where(Group.class).equalTo("id", id).findFirst();
        return group;
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

    public static void createOrUpdateCustomerGroup(CustomerGroup customerGroup, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(customerGroup);

                    baseResponse.setObject(customerGroup);
                    baseResponse.setMessage("Customer Group is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer  Gropo cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void createOrUpdateGroup(Group group, CompleteCallback completeCallback) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    realm.insertOrUpdate(group);

                    baseResponse.setObject(group);
                    baseResponse.setMessage("Group is saved!");
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Gropo cannot be saved!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }

    public static void deleteCustomerGroup(long customerId, CompleteCallback completeCallback){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setSuccess(true);
                try{
                    CustomerGroup customerGroup = realm.where(CustomerGroup.class).equalTo("customerId", customerId).findFirst();

                    if(customerGroup != null){
                        customerGroup.deleteFromRealm();
                        baseResponse.setMessage("Customer Group deleted successfully");


                    }else {
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("Customer Group could not be found !!");
                    }

                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("Customer Group could not be deleted !!");
                }
                completeCallback.onComplete(baseResponse);
            }
        });
    }


}
