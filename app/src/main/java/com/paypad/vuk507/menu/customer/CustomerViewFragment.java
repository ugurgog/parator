package com.paypad.vuk507.menu.customer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.payment.orderpayment.OrderPaymentCompletedFragment;
import com.paypad.vuk507.charge.sale.SaleListFragment;
import com.paypad.vuk507.db.CustomerDBHelper;
import com.paypad.vuk507.db.CustomerGroupDBHelper;
import com.paypad.vuk507.db.GroupDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.charge.payment.cancelpayment.CancellationPaymentCompletedFragment;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CustomerViewFragment extends BaseFragment{

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.selectionImgv)
    ClickableImageView selectionImgv;

    @BindView(R.id.editTv)
    TextView editTv;
    @BindView(R.id.nameTv)
    TextView nameTv;
    @BindView(R.id.emailll)
    LinearLayout emailll;
    @BindView(R.id.emailTv)
    TextView emailTv;
    @BindView(R.id.phoneNumberll)
    LinearLayout phoneNumberll;
    @BindView(R.id.phoneNumberTv)
    TextView phoneNumberTv;
    @BindView(R.id.companyll)
    LinearLayout companyll;
    @BindView(R.id.companyTv)
    TextView companyTv;
    @BindView(R.id.groupsll)
    LinearLayout groupsll;
    @BindView(R.id.groupsTv)
    TextView groupsTv;
    @BindView(R.id.birthdayll)
    LinearLayout birthdayll;
    @BindView(R.id.birthdayTv)
    TextView birthdayTv;
    @BindView(R.id.addressll)
    LinearLayout addressll;
    @BindView(R.id.addressTv)
    TextView addressTv;
    @BindView(R.id.otherll)
    LinearLayout otherll;
    @BindView(R.id.otherTv)
    TextView otherTv;


    private User user;
    private Customer mCustomer;
    private ReturnCustomerCallback returnCustomerCallback;
    private long customerId;
    private CustomerEditFragment customerEditFragment;
    private String classTag;

    CustomerViewFragment(Customer customer, String classTag, ReturnCustomerCallback returnCustomerCallback) {
        this.mCustomer = customer;
        this.classTag = classTag;
        this.returnCustomerCallback = returnCustomerCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mView = inflater.inflate(R.layout.fragment_customer_view, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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

    private void initListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initCustomerCreateFragment();
                mFragmentNavigation.pushFragment(customerEditFragment);

                /*mFragmentNavigation.pushFragment(new CustomerEditFragment(mCustomer, new ReturnCustomerCallback() {
                    @Override
                    public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                        mCustomer = customer;
                        fillCustomerFields();
                        returnCustomerCallback.OnReturn(customer, processEnum);
                    }
                }));*/
            }
        });

        emailll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUtils.sendEmailToCustomer(getContext(), emailTv.getText().toString());
            }
        });

        phoneNumberll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUtils.callCustomer(getContext(), phoneNumberTv.getText().toString());
            }
        });

        selectionImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), selectionImgv);
                popupMenu.inflate(R.menu.menu_customer_view);

                if(classTag.equals(SaleListFragment.class.getName()) || classTag.equals(OrderPaymentCompletedFragment.class.getName()) ||
                        classTag.equals(CancellationPaymentCompletedFragment.class.getName()))
                    popupMenu.getMenu().findItem(R.id.deleteCustomer).setVisible(false);
                else
                    popupMenu.getMenu().findItem(R.id.addToSale).setVisible(false);


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addToSale:
                                returnCustomerCallback.OnReturn(mCustomer, ItemProcessEnum.SELECTED);

                                break;
                            case R.id.editPersonalInformation:
                                initCustomerCreateFragment();
                                mFragmentNavigation.pushFragment(customerEditFragment);
                                break;
                            case R.id.deleteCustomer:

                                new CustomDialogBox.Builder((Activity) getContext())
                                        .setMessage(getContext().getResources().getString(R.string.sure_to_delete_customer))
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
                                                approveDeleteCustomer();
                                            }
                                        })
                                        .OnNegativeClicked(new CustomDialogListener() {
                                            @Override
                                            public void OnClick() {

                                            }
                                        }).build();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void initCustomerCreateFragment(){
        customerEditFragment = new CustomerEditFragment(mCustomer, new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                mCustomer = customer;
                fillCustomerFields();
                returnCustomerCallback.OnReturn(customer, processEnum);
            }
        });
        customerEditFragment.setReturnGroupCallback(new ReturnGroupCallback() {
            @Override
            public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {
                setGroupNames();
            }
        });
    }

    private void approveDeleteCustomer(){
        BaseResponse baseResponse = CustomerDBHelper.deleteCustomer(mCustomer.getId(), user.getId());

        if(baseResponse.isSuccess()){
            returnCustomerCallback.OnReturn((Customer) baseResponse.getObject(), ItemProcessEnum.DELETED);
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void initVariables() {
        toolbarTitleTv.setText((mCustomer.getName() != null ? mCustomer.getName() : "" )
                .concat(" ")
                .concat(mCustomer.getSurname() != null ? mCustomer.getSurname() : "").trim());
        fillCustomerFields();
    }

    private void fillCustomerFields() {
        customerId = mCustomer.getId();
        nameTv.setText((mCustomer.getName() != null ? mCustomer.getName() : "" )
                .concat(" ")
                .concat(mCustomer.getSurname() != null ? mCustomer.getSurname() : "").trim());

        if(mCustomer.getEmail() != null && !mCustomer.getEmail().isEmpty()){
            emailll.setVisibility(View.VISIBLE);
            emailTv.setText(mCustomer.getEmail());
        } else
            emailll.setVisibility(View.GONE);

        if(mCustomer.getPhoneNumber() != null && !mCustomer.getPhoneNumber().isEmpty()){
            phoneNumberll.setVisibility(View.VISIBLE);
            phoneNumberTv.setText(mCustomer.getPhoneNumber());
        } else
            phoneNumberll.setVisibility(View.GONE);

        if(mCustomer.getCompany() != null && !mCustomer.getCompany().isEmpty()){
            companyll.setVisibility(View.VISIBLE);
            companyTv.setText(mCustomer.getCompany());
        } else
            companyll.setVisibility(View.GONE);

        setGroupNames();

        if(mCustomer.getBirthday() != null){
            birthdayll.setVisibility(View.VISIBLE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            birthdayTv.setText(simpleDateFormat.format(mCustomer.getBirthday()));
        } else
            birthdayll.setVisibility(View.GONE);

        setAddress();

        if(mCustomer.getOtherInformation() != null && !mCustomer.getOtherInformation().isEmpty()){
            otherll.setVisibility(View.VISIBLE);
            otherTv.setText(mCustomer.getOtherInformation());
        } else
            otherll.setVisibility(View.GONE);
    }

    private void setAddress() {
        String fullAddress = "";

        addressll.setVisibility(View.VISIBLE);

        if(mCustomer.getAddress() != null && !mCustomer.getAddress().trim().isEmpty())
            fullAddress = fullAddress.concat(mCustomer.getAddress()).concat(",");

        if(mCustomer.getPostalCode() != null && !mCustomer.getPostalCode().trim().isEmpty())
            fullAddress = fullAddress.concat(mCustomer.getPostalCode()).concat(",");

        if(mCustomer.getCity() != null && !mCustomer.getCity().trim().isEmpty())
            fullAddress = fullAddress.concat(mCustomer.getCity()).concat(",");

        if(mCustomer.getCountry() != null && !mCustomer.getCountry().trim().isEmpty())
            fullAddress = fullAddress.concat(mCustomer.getCountry()).concat(",");

        if(!fullAddress.trim().isEmpty())
            addressTv.setText(fullAddress.substring(0, fullAddress.length() - 1));
        else
            addressll.setVisibility(View.GONE);
    }

    private void setGroupNames() {
        RealmResults<Group> allGroups = GroupDBHelper.getUserGroups(user.getId());
        List<Group> groups = new ArrayList<>();
        groups.addAll( new ArrayList(allGroups));

        if(groups.size() > 0){
            String groupNames = "";
            for(Group group : groups){
                RealmList<Customer> customers = CustomerGroupDBHelper.getCustomersOfGroup(group.getId(), user.getId());
                for(Customer customer : customers){
                    if(customer.getId() == mCustomer.getId()){
                        groupNames = groupNames.concat(group.getName()).concat(",");
                        break;
                    }
                }
            }

            if(groupNames.trim().isEmpty())
                groupsll.setVisibility(View.GONE);
            else
                groupsTv.setText(groupNames.substring(0, groupNames.length() - 1));
        }else
            groupsll.setVisibility(View.GONE);
    }

}