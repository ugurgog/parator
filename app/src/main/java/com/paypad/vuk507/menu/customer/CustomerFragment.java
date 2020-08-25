package com.paypad.vuk507.menu.customer;


import android.content.Context;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.payment.orderpayment.OrderPaymentCompletedFragment;
import com.paypad.vuk507.charge.sale.SaleListFragment;
import com.paypad.vuk507.db.CustomerDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.customer.adapters.CustomerListAdapter;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.GroupFragment;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.charge.payment.cancelpayment.CancellationPaymentCompletedFragment;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class CustomerFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.addItemImgv)
    ImageView addItemImgv;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;
    @BindView(R.id.customerRv)
    RecyclerView customerRv;
    @BindView(R.id.selectionImgv)
    ClickableImageView selectionImgv;
    @BindView(R.id.createCustomerBtn)
    Button createCustomerBtn;

    private User user;
    private Realm realm;
    private RealmResults<Customer> customers;
    private List<Customer> customerList;
    private CustomerListAdapter customerListAdapter;
    private CustomerEditFragment customerEditFragment;
    private CustomerSelectFragment customerSelectFragment;
    private CustomerViewFragment customerViewFragment;
    private String classTag;
    private ReturnCustomerCallback returnCustomerCallback;

    public CustomerFragment(String classTag, ReturnCustomerCallback callback) {
        this.classTag = classTag;
        this.returnCustomerCallback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE );

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_customer, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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
                popupMenu.inflate(R.menu.menu_customer);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.createCustomer:
                                initCustomerCreateFragment();
                                mFragmentNavigation.pushFragment(customerEditFragment);
                                break;
                            case R.id.deleteCustomers:
                                initCustomerSelectFragment(ItemProcessEnum.DELETED);
                                mFragmentNavigation.pushFragment(customerSelectFragment);
                                break;

                            case R.id.viewGroups:
                                mFragmentNavigation.pushFragment(new GroupFragment(new ReturnGroupCallback() {
                                    @Override
                                    public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {

                                    }
                                }));
                                break;

                            case R.id.addToGroup:
                                initCustomerSelectFragment(null);
                                mFragmentNavigation.pushFragment(customerSelectFragment);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        createCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCustomerCreateFragment();
                mFragmentNavigation.pushFragment(customerEditFragment);
            }
        });
    }

    private void initCustomerCreateFragment(){
        customerEditFragment = new CustomerEditFragment(null, new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                updateAdapterWithCurrentList();
            }
        });
        customerEditFragment.setReturnGroupCallback(new ReturnGroupCallback() {
            @Override
            public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {

            }
        });
    }


    private void initCustomerSelectFragment(ItemProcessEnum processType){
        customerSelectFragment = new CustomerSelectFragment(processType);
        customerSelectFragment.setCompleteCallback(new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                updateAdapterWithCurrentList();
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.customers));
        addItemImgv.setVisibility(View.GONE);

        if(classTag.equals(SaleListFragment.class.getName()) ||
                classTag.equals(OrderPaymentCompletedFragment.class.getName()) ||
                classTag.equals(CancellationPaymentCompletedFragment.class.getName()))
            selectionImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        customerRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        customers = CustomerDBHelper.getAllCustomers(user.getId());
        customerList = new ArrayList(customers);
        customerListAdapter = new CustomerListAdapter(getContext(), customerList, mFragmentNavigation, new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                initCustomerViewFragment(customer);
                mFragmentNavigation.pushFragment(customerViewFragment);
            }
        });
        customerRv.setAdapter(customerListAdapter);
    }

    private void initCustomerViewFragment(Customer customer){
        customerViewFragment = new CustomerViewFragment(customer, classTag, new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {

                if(processEnum == ItemProcessEnum.DELETED || processEnum == ItemProcessEnum.CHANGED)
                    updateAdapterWithCurrentList();
                else if(processEnum == ItemProcessEnum.SELECTED){
                    Objects.requireNonNull(customerViewFragment.getActivity()).onBackPressed();
                    returnCustomerCallback.OnReturn(customer, processEnum);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && customerListAdapter != null) {
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