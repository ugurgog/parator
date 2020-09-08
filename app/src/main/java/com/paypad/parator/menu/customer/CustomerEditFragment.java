package com.paypad.parator.menu.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.contact.ContactDialogFragment;
import com.paypad.parator.contact.interfaces.ReturnContactListener;
import com.paypad.parator.db.CustomerDBHelper;
import com.paypad.parator.db.CustomerGroupDBHelper;
import com.paypad.parator.db.GroupDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CountrySelectListener;
import com.paypad.parator.interfaces.ReturnObjectCallback;
import com.paypad.parator.login.utils.Validation;
import com.paypad.parator.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.parator.menu.group.SelectMultiGroupFragment;
import com.paypad.parator.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.parator.model.Customer;
import com.paypad.parator.model.CustomerGroup;
import com.paypad.parator.model.Group;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.Contact;
import com.paypad.parator.model.pojo.Country;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.PermissionModule;
import com.paypad.parator.utils.PhoneNumberTextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CustomerEditFragment extends BaseFragment
        implements CountrySelectListener {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.mainll)
    LinearLayout mainll;

    @BindView(R.id.btnAddFromAddress)
    Button btnAddFromAddress;
    @BindView(R.id.firstNameEt)
    EditText firstNameEt;
    @BindView(R.id.lastNameEt)
    EditText lastNameEt;
    @BindView(R.id.emailEt)
    EditText emailEt;
    @BindView(R.id.phoneNumberEt)
    EditText phoneNumberEt;
    @BindView(R.id.countryEt)
    EditText countryEt;
    @BindView(R.id.cityEt)
    EditText cityEt;
    @BindView(R.id.addressEt)
    EditText addressEt;
    @BindView(R.id.postalCodeEt)
    EditText postalCodeEt;
    @BindView(R.id.groupDescTv)
    TextView groupDescTv;
    @BindView(R.id.companyEt)
    EditText companyEt;
    @BindView(R.id.birthDayEt)
    EditText birthDayEt;
    @BindView(R.id.otherInfoEt)
    EditText otherInfoEt;
    @BindView(R.id.groupSelectll)
    LinearLayout groupSelectll;

    private User user;
    private ReturnCustomerCallback returnCustomerCallback;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private CountrySelectFragment countrySelectFragment;
    private ContactDialogFragment contactDialogFragment;
    private PermissionModule permissionModule;
    private List<Group> selectedGroupList;
    private List<Group> initialSelectedGroupList;
    private Realm realm;
    private Customer customer;
    private ReturnGroupCallback returnGroupCallback;

    CustomerEditFragment(Customer customer, ReturnCustomerCallback returnCustomerCallback) {
        this.customer = customer;
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
            mView = inflater.inflate(R.layout.fragment_customer_edit, container, false);
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
        setBirthDayDataSetListener();

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        birthDayEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthDayClicked();
            }
        });

        countryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countrySelectFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), countrySelectFragment.getTag());
            }
        });

        groupSelectll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new SelectMultiGroupFragment(selectedGroupList, new ReturnObjectCallback() {
                    @Override
                    public void OnReturn(Object object) {
                        selectedGroupList = (List<Group>) object;
                        setGroupTvDescription();
                    }
                }));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidCustomer();
            }
        });

        btnAddFromAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContactList();
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        permissionModule = new PermissionModule(getActivity());

        countrySelectFragment = new CountrySelectFragment();
        countrySelectFragment.setCountryListener(this);

        contactDialogFragment = new ContactDialogFragment(new ReturnContactListener() {
            @Override
            public void OnReturn(Contact contact) {
                setInfoFromContact(contact);
            }
        });

        selectedGroupList = new ArrayList<>();
        initialSelectedGroupList = new ArrayList<>();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.create_customer));
        
        if(customer != null){
            btnAddFromAddress.setVisibility(View.GONE);
            toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.edit_customer));
            fillCustomerFields();
        }else {
            customer = new Customer();
        }
        
        PhoneNumberUtil util = PhoneNumberUtil.createInstance(Objects.requireNonNull(getContext()));
        phoneNumberEt.addTextChangedListener(new PhoneNumberTextWatcher(phoneNumberEt, util));
    }

    private void fillCustomerFields() {
        firstNameEt.setText(customer.getName());

        if(customer.getSurname() != null && !customer.getSurname().isEmpty())
            lastNameEt.setText(customer.getSurname());

        if(customer.getEmail() != null && !customer.getEmail().isEmpty())
            emailEt.setText(customer.getEmail());

        if(customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty())
            phoneNumberEt.setText(customer.getPhoneNumber());

        if(customer.getCountry() != null && !customer.getCountry().isEmpty())
            countryEt.setText(customer.getCountry());

        if(customer.getCity() != null && !customer.getCity().isEmpty())
            cityEt.setText(customer.getCity());

        if(customer.getAddress() != null && !customer.getAddress().isEmpty())
            addressEt.setText(customer.getAddress());

        if(customer.getPostalCode() != null && !customer.getPostalCode().isEmpty())
            postalCodeEt.setText(customer.getPostalCode());

        if(customer.getCompany() != null && !customer.getCompany().isEmpty())
            companyEt.setText(customer.getCompany());

        if(customer.getOtherInformation() != null && !customer.getOtherInformation().isEmpty())
            otherInfoEt.setText(customer.getOtherInformation());

        if(customer.getBirthday() != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            birthDayEt.setText(simpleDateFormat.format(customer.getBirthday()));
        }

        setGroupNames();
    }

    private void setGroupNames() {
        RealmResults<Group> allGroups = GroupDBHelper.getUserGroups(user.getId());
        List<Group> groups = new ArrayList<>();
        groups.addAll( new ArrayList(allGroups));

        if(groups.size() > 0){
            String groupNames = "";
            for(Group group : groups){

                RealmList<Customer> customers = CustomerGroupDBHelper.getCustomersOfGroup(group.getId(), user.getId());

                for(Customer customer1 : customers){
                    if(customer1.getId() == customer.getId()){
                        groupNames = groupNames.concat(group.getName()).concat(",");
                        selectedGroupList.add(group);
                        initialSelectedGroupList.add(group);
                        break;
                    }
                }
            }

            if(groupNames.trim().isEmpty())
                groupDescTv.setText(getResources().getString(R.string.groups));
            else
                groupDescTv.setText(groupNames.substring(0, groupNames.length() - 1));

        }else
            groupDescTv.setText(getResources().getString(R.string.groups));
    }

    private void setInfoFromContact(Contact contact){
        if(contact != null){
            if(contact.getName() != null){
                String[] names = contact.getName().split(" ");

                int index = 0;
                String lastName = "";
                for(String name : names){
                    if(index == 0)
                        firstNameEt.setText(name);
                    else
                        lastName = lastName.concat(name);
                    index++;
                }
                lastNameEt.setText(lastName);
            }

            if(contact.getPhoneNumber() != null){
                phoneNumberEt.setText(contact.getPhoneNumber());
            }
        }
    }

    private void setBirthDayDataSetListener() {
        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = month + "/" + day + "/" + year;
            birthDayEt.setText(date);
        };
    }

    private void birthDayClicked() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Objects.requireNonNull(getActivity()),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setGroupTvDescription() {
        String groupNameInfo = "";

        if(selectedGroupList.size() != 0){
            for(Group group : selectedGroupList){
                if(group.getName() != null){
                    groupNameInfo = groupNameInfo.concat(group.getName()).concat(",");
                }
            }
            groupNameInfo = groupNameInfo.substring(0, groupNameInfo.length() -1 );
        }else
            groupNameInfo = getContext().getResources().getString(R.string.groups);

        groupDescTv.setText(groupNameInfo);
    }

    private void checkValidCustomer() {
        if(emailEt.getText() != null && !emailEt.getText().toString().isEmpty()){
            boolean checkEmail = Validation.getInstance().isValidEmail(getContext(), emailEt.getText().toString());
            if(!checkEmail){
                CommonUtils.snackbarDisplay(mainll,
                        Objects.requireNonNull(getContext()), Validation.getInstance().getErrorMessage());
                return;
            }
        }

        if(firstNameEt.getText() == null || firstNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(mainll,
                    Objects.requireNonNull(getContext()), getResources().getString(R.string.please_type_first_name));
            return;
        }

        createCustomer();
    }

    private void createCustomer() {
        boolean inserted = false;
        realm.beginTransaction();

        if(customer.getId() == 0){
            customer.setId(CustomerDBHelper.getCustomerCurrentPrimaryKeyId());
            customer.setCreateDate(new Date());
            customer.setUserId(user.getId());
            customer.setColorId(CommonUtils.getDarkRandomColor(getContext()));
            customer.setDeleted(false);
            inserted = true;
        }else {
            customer.setUpdateDate(new Date());
            customer.setUpdateUserId(user.getId());
        }

        customer.setName(firstNameEt.getText().toString());
        customer.setSurname(lastNameEt.getText().toString());
        customer.setEmail(emailEt.getText().toString());
        customer.setPhoneNumber(phoneNumberEt.getText().toString());
        customer.setCountry(countryEt.getText().toString());
        customer.setCity(cityEt.getText().toString());
        customer.setAddress(addressEt.getText().toString());
        customer.setPostalCode(postalCodeEt.getText().toString());
        customer.setCompany(companyEt.getText().toString());
        customer.setOtherInformation(otherInfoEt.getText().toString());

        try {
            if(birthDayEt.getText() != null && !birthDayEt.getText().toString().isEmpty()){
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                customer.setBirthday(formatter.parse(birthDayEt.getText().toString()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        realm.commitTransaction();

        boolean finalInserted = inserted;

        BaseResponse baseResponse = CustomerDBHelper.createOrUpdateCustomer(customer);

        if(baseResponse.isSuccess()){
            if(finalInserted)
                returnCustomerCallback.OnReturn((Customer) baseResponse.getObject(), ItemProcessEnum.INSERTED);
            else
                returnCustomerCallback.OnReturn((Customer) baseResponse.getObject(), ItemProcessEnum.CHANGED);
        }

        RealmResults<CustomerGroup> customerGroups = CustomerGroupDBHelper.getCustomerGroupsOfCustomer(customer.getId(), user.getId());
        BaseResponse baseResponse1 = CustomerGroupDBHelper.deleteCustomerGroupsByGivenList(customerGroups);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse1);

        if(!baseResponse1.isSuccess())
            return;

        if(selectedGroupList != null && selectedGroupList.size() > 0){
            for(Group group : selectedGroupList){

                CustomerGroup customerGroup = new CustomerGroup();
                customerGroup.setId(UUID.randomUUID().toString());
                customerGroup.setCustomerId(customer.getId());
                customerGroup.setGroupId(group.getId());
                customerGroup.setUserId(user.getId());
                customerGroup.setCreateDate(new Date());

                BaseResponse baseResponse2 = CustomerGroupDBHelper.createOrUpdateCustomerGroup(customerGroup);

                if(baseResponse2.isSuccess())
                    returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.INSERTED);
            }
        }

        if(initialSelectedGroupList.size() > 0){
            for(Group initial : initialSelectedGroupList){
                boolean exist = false;
                for (Group selected : selectedGroupList){

                    if(selected.getId() == initial.getId()){

                        exist = true;
                        break;
                    }
                }
                if(!exist){

                    BaseResponse baseResponse3 = CustomerGroupDBHelper.deleteCustomerGroupByGroupIdAndCustomerId(initial.getId(), customer.getId(), user.getId());
                    DataUtils.showBaseResponseMessage(getContext(), baseResponse3);

                    if(baseResponse3.isSuccess())
                        returnGroupCallback.OnGroupReturn(initial, ItemProcessEnum.DELETED);
                }
            }
        }

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void getContactList() {
        if (!permissionModule.checkReadContactsPermission()) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PermissionModule.PERMISSION_READ_CONTACTS);
        } else {
            contactDialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), contactDialogFragment.getTag());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionModule.PERMISSION_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactDialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), contactDialogFragment.getTag());
            }
        }
    }

    @Override
    public void onCountryClick(Country country) {
        if(country != null && country.getName() != null)
            countryEt.setText(country.getName());
    }

    public void setReturnGroupCallback(ReturnGroupCallback returnGroupCallback) {
        this.returnGroupCallback = returnGroupCallback;
    }
}
