package com.paypad.parator.menu.group;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.CustomerGroupDBHelper;
import com.paypad.parator.db.GroupDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.menu.group.adapters.GroupSelectListAdapter;
import com.paypad.parator.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.parator.model.Customer;
import com.paypad.parator.model.CustomerGroup;
import com.paypad.parator.model.Group;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SelectGroupForCustomerAddFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.infoTv)
    TextView infoTv;
    @BindView(R.id.createBtn)
    Button createBtn;
    @BindView(R.id.itemRv)
    RecyclerView itemRv;

    private GroupSelectListAdapter groupSelectListAdapter;
    private Realm realm;

    private RealmResults<Group> groups;
    private List<Group> groupList;
    private User user;
    private List<Customer> selectedCustomerList;
    private Group selectedGroup;
    private CompleteCallback completeCallback;

    public SelectGroupForCustomerAddFragment(List<Customer> selectedCustomerList, CompleteCallback completeCallback) {
        this.selectedCustomerList = selectedCustomerList;
        this.completeCallback = completeCallback;
    }

    public void setSelectedCustomerList(List<Customer> selectedCustomerList) {
        this.selectedCustomerList = selectedCustomerList;
        //infoTv.setText(String.valueOf(selectedCustomerList.size()).concat(" ").concat(getContext().getResources().getString(R.string.customers_will_be_added_group)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(getContext());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_select_group_for_customer_add, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        itemRv = mView.findViewById(R.id.itemRv);
        toolbarTitleTv.setText(getResources().getString(R.string.select_group));
        saveBtn.setText(getContext().getResources().getString(R.string.done));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        infoTv.setText(String.valueOf(selectedCustomerList.size()).concat(" ").concat(getContext().getResources().getString(R.string.customers_will_be_added_group)));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    private void initListeners() {
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedGroup != null)
                    updateGroup();
            }
        });
    }

    public void updateGroup(){
        //realm.beginTransaction();

        List<Customer> addedCustomers = new ArrayList<>();

        BaseResponse baseResponse1 = new BaseResponse();
        baseResponse1.setSuccess(true);

        for(Customer customer : selectedCustomerList){
            boolean customerExist = false;
            RealmList<Customer> customers = CustomerGroupDBHelper.getCustomersOfGroup(selectedGroup.getId(), user.getId());

            for(Customer customer1 : customers){
                if(customer.getId() == customer1.getId()){
                    customerExist = true;
                    break;
                }
            }
            if(!customerExist)
                addedCustomers.add(customer);
        }

        if(addedCustomers.size() > 0){
            for(Customer customer : addedCustomers){
                CustomerGroup customerGroup = new CustomerGroup();
                customerGroup.setId(UUID.randomUUID().toString());
                customerGroup.setCustomerId(customer.getId());
                customerGroup.setGroupId(selectedGroup.getId());
                customerGroup.setUserId(user.getId());
                customerGroup.setCreateDate(new Date());

                baseResponse1 = CustomerGroupDBHelper.createOrUpdateCustomerGroup(customerGroup);

                if(!baseResponse1.isSuccess()){
                    DataUtils.showBaseResponseMessage(getContext(), baseResponse1);
                    break;
                }
            }
        }

        if(baseResponse1.isSuccess()){
            completeCallback.onComplete(baseResponse1);
        }
    }

    public void updateAdapterWithCurrentList(){
        groups = GroupDBHelper.getUserGroups(user.getId());
        groupList = new ArrayList(groups);

        groupSelectListAdapter = new GroupSelectListAdapter(groupList, new ReturnGroupCallback() {
            @Override
            public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {
                CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
                selectedGroup = group;
            }
        });
        itemRv.setAdapter(groupSelectListAdapter);
    }
}