package com.paypad.vuk507.menu.group;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CustomerDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.customer.adapters.CustomerListAdapter;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class GroupViewFragment extends BaseFragment {

    private View mView;

    private ClickableImageView backImgv;
    private AppCompatTextView toolbarTitleTv;
    private EditText searchEdittext;
    private ImageView searchCancelImgv;
    private TextView searchResultTv;
    private ClickableImageView selectionImgv;

    private RecyclerView itemRv;
    private LinearLayout mainll;

    private CustomerListAdapter customerListAdapter;
    private ReturnGroupCallback returnGroupCallback;
    private Realm realm;
    private GroupCreateFragment groupCreateFragment;

    private RealmResults<Customer> customers;
    private List<Customer> customerList;
    private User user;
    private Group group;

    public GroupViewFragment(Group group, ReturnGroupCallback returnGroupCallback) {
        this.group = group;
        this.returnGroupCallback = returnGroupCallback;
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
            mView = inflater.inflate(R.layout.fragment_custom_no_button_with_menu, container, false);
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
        mainll = mView.findViewById(R.id.mainll);
        itemRv = mView.findViewById(R.id.itemRv);
        searchEdittext = mView.findViewById(R.id.searchEdittext);
        searchCancelImgv = mView.findViewById(R.id.searchCancelImgv);
        searchResultTv = mView.findViewById(R.id.searchResultTv);
        toolbarTitleTv = mView.findViewById(R.id.toolbarTitleTv);
        backImgv = mView.findViewById(R.id.backImgv);
        selectionImgv = mView.findViewById(R.id.selectionImgv);

        toolbarTitleTv.setText(group.getName());

        initGroupCreateFragment();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemRv.setLayoutManager(linearLayoutManager);
        //contactRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    private void initListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    updateAdapter(s.toString());
                    searchCancelImgv.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    searchCancelImgv.setVisibility(View.GONE);
                }
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
                CommonUtils.showKeyboard(getContext(),false, searchEdittext);
            }
        });

        selectionImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), selectionImgv);
                popupMenu.inflate(R.menu.menu_group_view);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editGroup:
                                mFragmentNavigation.pushFragment(groupCreateFragment);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void initGroupCreateFragment(){
        groupCreateFragment = new GroupCreateFragment(group, new ReturnGroupCallback() {
            @Override
            public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {

                if(processEnum == ItemProcessEnum.DELETED){
                    returnGroupCallback.OnGroupReturn(group, processEnum);
                    Objects.requireNonNull(groupCreateFragment.getActivity()).onBackPressed();
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }else
                    toolbarTitleTv.setText(group.getName());
            }
        });
    }

    public void updateAdapterWithCurrentList(){
        //customerList = new ArrayList(group.getCustomers());

        customerList = new ArrayList<>();
        for(Long customerId : group.getCustomerIds()){
            Customer customer = CustomerDBHelper.getCustomer(customerId);
            if(customer != null)
                customerList.add(customer);
        }

        customerListAdapter = new CustomerListAdapter(getContext(), customerList, mFragmentNavigation, new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                //TODO - view current customer
            }
        });
        itemRv.setAdapter(customerListAdapter);
    }

    public void updateAdapter(String searchText) {
        if (searchText != null) {
            customerListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (customerList != null && customerList.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}