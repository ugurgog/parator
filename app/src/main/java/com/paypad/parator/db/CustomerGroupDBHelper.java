package com.paypad.parator.db;

import com.paypad.parator.model.Customer;
import com.paypad.parator.model.CustomerGroup;
import com.paypad.parator.model.Group;
import com.paypad.parator.model.pojo.BaseResponse;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CustomerGroupDBHelper {


    public static CustomerGroup getCustomerGroupByCustomerIdAndGroupId(long customerId, long groupId, String userId){
        Realm realm = Realm.getDefaultInstance();
        CustomerGroup customerGroup = realm.where(CustomerGroup.class)
                .equalTo("customerId", customerId)
                .equalTo("groupId", groupId)
                .equalTo("userId", userId)
                .findFirst();
        return customerGroup;
    }

    public static RealmList<Group> getGroupsOfCustomer(long customerId, String userId){
        RealmResults<CustomerGroup> customerGroups = getCustomerGroupsOfCustomer(customerId, userId);

        RealmList<Group> groups = new RealmList<>();

        if(customerGroups != null && customerGroups.size() > 0){
            for(CustomerGroup customerGroup : customerGroups){
                Group group = GroupDBHelper.getGroup(customerGroup.getGroupId(), userId);
                groups.add(group);
            }
        }
        return groups;
    }

    public static RealmResults<CustomerGroup> getCustomerGroupsOfCustomer(long customerId, String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CustomerGroup> customerGroups = realm.where(CustomerGroup.class)
                .equalTo("customerId", customerId)
                .equalTo("userId", userId)
                .findAll();
        return customerGroups;
    }

    public static RealmList<Customer> getCustomersOfGroup(long groupId, String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CustomerGroup> customerGroups = getCustomerGroupsOfGroup(groupId, userId);

        RealmList<Customer> customers = new RealmList<>();

        if(customerGroups != null && customerGroups.size() > 0){
            for(CustomerGroup customerGroup : customerGroups){
                Customer customer = CustomerDBHelper.getCustomer(customerGroup.getCustomerId(), userId);
                customers.add(customer);
            }
        }
        return customers;
    }

    public static RealmResults<CustomerGroup> getCustomerGroupsOfGroup(long groupId, String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CustomerGroup> customerGroups = realm.where(CustomerGroup.class)
                .equalTo("groupId", groupId)
                .equalTo("userId", userId)
                .findAll();
        return customerGroups;
    }

    public static BaseResponse createOrUpdateCustomerGroup(CustomerGroup customerGroup) {
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                try{
                    realm.insertOrUpdate(customerGroup);

                    baseResponse.setObject(customerGroup);
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("CustomerGroup cannot be saved!");
                }
            }
        });
        return baseResponse;
    }

    public static BaseResponse deleteCustomerGroupById(String id){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    CustomerGroup customerGroup = realm.where(CustomerGroup.class).equalTo("id", id).findFirst();
                    try{
                        customerGroup.deleteFromRealm();
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("CustomerGroup could not be deleted !!");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("CustomerGroup could not be deleted !!");
                }
            }
        });
        return baseResponse;
    }

    //Customer silinmistir ve bagli oldugu grup kayitlari da silinecektir
    public static BaseResponse deleteCustomerGroupsByCustomerId(long customerId, String userId){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    try{
                        RealmResults<CustomerGroup> customerGroups = realm.where(CustomerGroup.class)
                                .equalTo("customerId", customerId)
                                .equalTo("userId", userId)
                                .findAll();

                        customerGroups.deleteAllFromRealm();
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("CustomerGroups could not be deleted !!");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("CustomerGroups could not be deleted !!");
                }
            }
        });
        return baseResponse;
    }

    //Group silinmistir ve bagli oldugu customer kayitlari da silinecektir
    public static BaseResponse deleteCustomerGroupsByGroupId(long groupId, String userId){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    try{
                        RealmResults<CustomerGroup> customerGroups = realm.where(CustomerGroup.class)
                                .equalTo("groupId", groupId)
                                .equalTo("userId", userId)
                                .findAll();

                        customerGroups.deleteAllFromRealm();
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("CustomerGroups could not be deleted !!");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("CustomerGroups could not be deleted !!");
                }
            }
        });
        return baseResponse;
    }


    //Gruptan customer iliskisi silinir
    public static BaseResponse deleteCustomerGroupByGroupIdAndCustomerId(long groupId, long customerId, String userId){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    try{
                        CustomerGroup customerGroup = getCustomerGroupByCustomerIdAndGroupId(customerId, groupId, userId);
                        customerGroup.deleteFromRealm();
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("CustomerGroup could not be deleted !!");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("CustomerGroup could not be deleted !!");
                }
            }
        });
        return baseResponse;
    }


    public static BaseResponse deleteCustomerGroupsByGivenList(RealmResults<CustomerGroup> customerGroups){
        Realm realm = Realm.getDefaultInstance();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    try{
                        customerGroups.deleteAllFromRealm();
                        //baseResponse.setMessage("CustomerGroups deleted successfully");
                    }catch (Exception e){
                        baseResponse.setSuccess(false);
                        baseResponse.setMessage("CustomerGroups could not be deleted !!");
                    }
                }catch (Exception e){
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage("CustomerGroups could not be deleted !!");
                }
            }
        });
        return baseResponse;
    }
}
