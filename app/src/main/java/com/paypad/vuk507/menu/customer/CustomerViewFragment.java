package com.paypad.vuk507.menu.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.contact.ContactDialogFragment;
import com.paypad.vuk507.contact.interfaces.ReturnContactListener;
import com.paypad.vuk507.db.CustomerDBHelper;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.GroupDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.interfaces.ReturnObjectCallback;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.GroupFragment;
import com.paypad.vuk507.menu.group.SelectMultiGroupFragment;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.Contact;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.PermissionModule;
import com.paypad.vuk507.utils.PhoneNumberTextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;
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
    private boolean deleteCustomer = false;

    CustomerViewFragment(Customer customer, ReturnCustomerCallback returnCustomerCallback) {
        this.mCustomer = customer;
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

                mFragmentNavigation.pushFragment(new CustomerCreateFragment(mCustomer, new ReturnCustomerCallback() {
                    @Override
                    public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                        mCustomer = customer;
                        fillCustomerFields();
                        returnCustomerCallback.OnReturn(customer, processEnum);
                    }
                }));
            }
        });

        emailll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        phoneNumberll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        selectionImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), selectionImgv);
                popupMenu.inflate(R.menu.menu_customer_view);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editPersonalInformation:

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
                                        .setEditTextVisibility(View.GONE)
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

    private void approveDeleteCustomer(){

        CustomerDBHelper.deleteCustomer(mCustomer.getId(), new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                if(baseResponse.isSuccess()){
                    deleteCustomer = true;
                }
            }
        });

        if(deleteCustomer)
            deleteCustomerFromGroups();
    }

    private void deleteCustomerFromGroups(){
        GroupDBHelper.deleteCustomerFromGroups(customerId, user.getUuid(), new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                if(baseResponse.isSuccess()){
                    returnCustomerCallback.OnReturn((Customer) baseResponse.getObject(), ItemProcessEnum.DELETED);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
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

        if(mCustomer.getEmail() != null && !mCustomer.getEmail().isEmpty())
            emailTv.setText(mCustomer.getEmail());
        else
            emailll.setVisibility(View.GONE);

        if(mCustomer.getPhoneNumber() != null && !mCustomer.getPhoneNumber().isEmpty())
            phoneNumberTv.setText(mCustomer.getPhoneNumber());
        else
            phoneNumberll.setVisibility(View.GONE);

        if(mCustomer.getCompany() != null && !mCustomer.getCompany().isEmpty())
            companyTv.setText(mCustomer.getCompany());
        else
            companyll.setVisibility(View.GONE);

        setGroupNames();

        if(mCustomer.getBirthday() != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            birthdayTv.setText(simpleDateFormat.format(mCustomer.getBirthday()));
        } else
            birthdayll.setVisibility(View.GONE);

        setAddress();

        if(mCustomer.getOtherInformation() != null && !mCustomer.getOtherInformation().isEmpty())
            otherTv.setText(mCustomer.getOtherInformation());
        else
            otherll.setVisibility(View.GONE);
    }

    private void setAddress() {
        String fullAddress = "";

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
        RealmResults<Group> allGroups = GroupDBHelper.getUserGroups(user.getUuid());
        List<Group> groups = new ArrayList<>();
        groups.addAll( new ArrayList(allGroups));

        if(groups.size() > 0){
            String groupNames = "";
            for(Group group : groups){
                for(Customer customer1 : group.getCustomers()){
                    if(customer1.getId() == mCustomer.getId()){
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