package com.paypad.vuk507.menu.customer;

import android.app.Activity;
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
import com.paypad.vuk507.db.GroupDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.customer.adapters.CustomerSelectListAdapter;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.SelectGroupForCustomerAddFragment;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class CustomerSelectFragment extends BaseFragment {

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
    @BindView(R.id.addBtn)
    Button addBtn;

    private User user;
    private Realm realm;
    private RealmResults<Customer> customers;
    private List<Customer> customerList;
    private CustomerSelectListAdapter customerListAdapter;
    private List<Customer> selectedCustomerList;
    private SelectGroupForCustomerAddFragment selectGroupForCustomerAddFragment;
    private CompleteCallback completeCallback;
    private ItemProcessEnum processType;

    public CustomerSelectFragment(ItemProcessEnum processEnum) {
        processType = processEnum;
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
                popupMenu.inflate(R.menu.menu_select_deselect);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.selectAll:
                                customerListAdapter.setSelectAllChecked(true);
                                selectedCustomerList = new ArrayList<>();
                                selectedCustomerList.addAll(customerList);
                                customerListAdapter.setSelectedCustomerList(selectedCustomerList);

                                customerListAdapter.notifyDataSetChanged();

                                CommonUtils.setSaveBtnEnability(true, addBtn, getContext());

                                break;
                            case R.id.deselectAll:
                                customerListAdapter.setSelectAllChecked(false);
                                selectedCustomerList = new ArrayList<>();
                                customerListAdapter.setSelectedCustomerList(selectedCustomerList);

                                customerListAdapter.notifyDataSetChanged();

                                CommonUtils.setSaveBtnEnability(false, addBtn, getContext());
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(processType != null && (processType == ItemProcessEnum.DELETED)){

                    new CustomDialogBox.Builder((Activity) getContext())
                            .setMessage(getContext().getResources().getString(R.string.sure_to_delete_selected_customers))
                            .setNegativeBtnVisibility(View.VISIBLE)
                            .setNegativeBtnText(getContext().getResources().getString(R.string.cancel))
                            .setNegativeBtnBackground(getContext().getResources().getColor(R.color.Silver, null))
                            .setPositiveBtnVisibility(View.VISIBLE)
                            .setPositiveBtnText(getContext().getResources().getString(R.string.yes))
                            .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                            .setDurationTime(0)
                            .isCancellable(true)
                            .setEdittextVisibility(View.GONE)
                            .OnPositiveClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {
                                    approveDeleteCustomers();
                                }
                            })
                            .OnNegativeClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            }).build();

                }else {
                    initSelectGroupFragment();
                    mFragmentNavigation.pushFragment(selectGroupForCustomerAddFragment);
                }
            }
        });
    }

    private void approveDeleteCustomers() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);
        for (Customer customer : selectedCustomerList) {
            GroupDBHelper.deleteCustomerFromGroups(customer.getId(), user.getUuid(), new CompleteCallback() {
                @Override
                public void onComplete(BaseResponse baseResponse) {

                }
            });
        }

        for (Customer customer : selectedCustomerList) {
            CustomerDBHelper.deleteCustomer(customer.getId());
        }

        completeCallback.onComplete(baseResponse);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.select_customers));
        addItemImgv.setVisibility(View.GONE);

        if(processType != null && (processType == ItemProcessEnum.DELETED))
            addBtn.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.delete_customers));
        else
            addBtn.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.add_to_group));

        setRvMargins();
        CommonUtils.setSaveBtnEnability(false, addBtn, getContext());
        selectedCustomerList = new ArrayList<>();
        initRecyclerView();
    }

    private void setRvMargins(){
        addBtn.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 0, 0, CommonUtils.getPaddingInPixels(getContext(), 30));
        customerRv.setLayoutParams(params);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        customerRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    private void  initSelectGroupFragment(){
        selectGroupForCustomerAddFragment = new SelectGroupForCustomerAddFragment(selectedCustomerList, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                if(baseResponse.isSuccess()){
                    CommonUtils.showToastShort(getContext(), String.valueOf(selectedCustomerList.size()).concat(" ")
                            .concat(getContext().getResources().getString(R.string.customers_added_to_group)));
                }else {
                    CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                }

                selectGroupForCustomerAddFragment.getActivity().onBackPressed();
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    public void updateAdapterWithCurrentList(){
        customers = CustomerDBHelper.getAllCustomers(user.getUuid());
        customerList = new ArrayList(customers);
        customerListAdapter = new CustomerSelectListAdapter(getContext(), customerList, selectedCustomerList,  mFragmentNavigation, new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {

                if(processEnum == ItemProcessEnum.SELECTED){
                    selectedCustomerList.add(customer);
                    CommonUtils.setSaveBtnEnability(true, addBtn, getContext());
                }else {
                    selectedCustomerList.remove(customer);

                    if(selectedCustomerList.size() == 0)
                        CommonUtils.setSaveBtnEnability(false, addBtn, getContext());
                }
            }
        });
        customerRv.setAdapter(customerListAdapter);
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

    public void setCompleteCallback(CompleteCallback completeCallback) {
        this.completeCallback = completeCallback;
    }
}