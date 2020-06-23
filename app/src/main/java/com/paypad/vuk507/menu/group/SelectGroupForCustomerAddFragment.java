package com.paypad.vuk507.menu.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.MainActivity;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.GroupDBHelper;
import com.paypad.vuk507.db.UnitDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.login.LoginActivity;
import com.paypad.vuk507.menu.group.adapters.GroupListAdapter;
import com.paypad.vuk507.menu.group.adapters.GroupSelectListAdapter;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        realm.beginTransaction();

        Group tempGroup = realm.copyToRealm(selectedGroup);
        RealmList<Long> addedCustomers = new RealmList<>();


        for(Customer customer : selectedCustomerList){

            boolean customerExist = false;

            for(Long customerId : selectedGroup.getCustomerIds()){
                if(customer.getId() == customerId){
                    customerExist = true;
                    break;
                }
            }
            if(!customerExist)
                addedCustomers.add(customer.getId());
        }

        tempGroup.getCustomerIds().addAll(addedCustomers);

        realm.commitTransaction();

        GroupDBHelper.createOrUpdateGroup(tempGroup, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                if(baseResponse.isSuccess()){
                    completeCallback.onComplete(baseResponse);
                    //Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
    }

    public void updateAdapterWithCurrentList(){
        groups = GroupDBHelper.getUserGroups(user.getUuid());
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