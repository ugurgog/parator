package com.paypad.vuk507.menu.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.contact.ContactDialogFragment;
import com.paypad.vuk507.contact.ContactHelper;
import com.paypad.vuk507.contact.adapters.ContactListAdapter;
import com.paypad.vuk507.contact.interfaces.ReturnContactListener;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.CustomerDBHelper;
import com.paypad.vuk507.db.GroupDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.CountryDataEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.httpprocess.CountryProcess;
import com.paypad.vuk507.httpprocess.interfaces.OnEventListener;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.ReturnObjectCallback;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.SelectMultiGroupFragment;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.Contact;
import com.paypad.vuk507.model.pojo.CountryPhoneCode;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.PermissionModule;
import com.paypad.vuk507.utils.PhoneNumberTextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import io.realm.Realm;
import io.realm.RealmList;

import static android.content.Context.MODE_PRIVATE;

public class CustomerCreateFragment extends BaseFragment
        implements CountrySelectFragment.CountrySelectListener{

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
    private Realm realm;

    CustomerCreateFragment(ReturnCustomerCallback returnCustomerCallback) {
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
            mView = inflater.inflate(R.layout.fragment_customer_create, container, false);
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
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.create_customer));
        PhoneNumberUtil util = PhoneNumberUtil.createInstance(Objects.requireNonNull(getContext()));
        phoneNumberEt.addTextChangedListener(new PhoneNumberTextWatcher(phoneNumberEt, util));
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
        Customer customer = new Customer();
        customer.setId(CustomerDBHelper.getCustomerCurrentPrimaryKeyId());
        customer.setUserUuid(user.getUuid());
        customer.setCreateDate(new Date());
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
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            customer.setBirthday(formatter.parse(birthDayEt.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        CustomerDBHelper.createOrUpdateCustomer(customer, baseResponse -> {
            CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
            if(baseResponse.isSuccess()){
                returnCustomerCallback.OnReturn((Customer) baseResponse.getObject(), ItemProcessEnum.INSERTED);
            }
        });

        if(selectedGroupList != null && selectedGroupList.size() > 0){
            for(Group group : selectedGroupList){
                updateGroup(group, customer);
            }
        }

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    public void updateGroup(Group group, Customer customer){
        realm.beginTransaction();

        Group tempGroup = realm.copyToRealm(group);
        tempGroup.getCustomers().add(customer);

        realm.commitTransaction();

        GroupDBHelper.createOrUpdateGroup(tempGroup, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {

            }
        });
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
    public void onCountryClick(String country) {
        if(country != null && !country.isEmpty())
            countryEt.setText(country);
    }
}
