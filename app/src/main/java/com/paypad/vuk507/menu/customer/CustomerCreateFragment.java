package com.paypad.vuk507.menu.customer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CustomerCreateFragment extends BaseFragment implements CountrySelectFragment.CountrySelectListener {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

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



    private Realm realm;
    private User user;
    private ReturnCustomerCallback returnCustomerCallback;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private CountrySelectFragment countrySelectFragment;

    public CustomerCreateFragment(ReturnCustomerCallback returnCustomerCallback) {
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
    }

    private void initVariables() {
        countrySelectFragment = new CountrySelectFragment();
        countrySelectFragment.setCountryListener(this);
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

    @Override
    public void onCountryClick(String country) {
        if(country != null && !country.isEmpty())
            countryEt.setText(country);
    }
}
