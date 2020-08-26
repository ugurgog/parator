package com.paypad.vuk507.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.AnnualTurnoverRangeEnum;
import com.paypad.vuk507.enums.CurrencyEnum;
import com.paypad.vuk507.enums.ItemSpinnerEnum;
import com.paypad.vuk507.enums.NumberOfLocationEnum;
import com.paypad.vuk507.enums.TypeOfBusinessEnum;
import com.paypad.vuk507.interfaces.CountrySelectListener;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.menu.customer.CountrySelectFragment;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.uiUtils.NDSpinner;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.PhoneNumberTextWatcher;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;


public class RegisterActivity extends AppCompatActivity
        implements CountrySelectListener {

    private EditText emailEt;
    private EditText passwordET;
    private Button btnRegister;
    private EditText storeNameEt;
    private EditText firstNameEt;
    private EditText lastNameEt;
    private EditText phoneNumberEt;
    private EditText countryEt;
    private ImageView backImgv;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private NDSpinner typeOfBusinessSpinner;
    private NDSpinner numberOfLocationsSpinner;
    private NDSpinner estimatedAnnTurnoverSpinner;
    private NDSpinner tradingCurrencySpinner;

    private TypeOfBusinessEnum typeOfBusinessEnum = null;
    private NumberOfLocationEnum numberOfLocationEnum = null;
    private AnnualTurnoverRangeEnum annualTurnoverRangeEnum = null;
    private CurrencyEnum currencyEnum = null;

    private ArrayAdapter<String> typeOfBusinessAdapter;
    private ArrayAdapter<String> numberOfLocationsAdapter;
    private ArrayAdapter<String> estimatedAnnTurnoverAdapter;
    private ArrayAdapter<String> tradingCurrencyAdapter;

    private String email;
    private String userPassword;
    private ProgressDialog progressDialog;
    private CountrySelectFragment countrySelectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_x);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        initViews();
        initVariables();
        initListeners();
        setShapes();
    }

    public void setShapes(){
        emailEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        passwordET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        storeNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        firstNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        lastNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        phoneNumberEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        countryEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        btnRegister.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 0));
    }

    private void initViews() {
        emailEt = findViewById(R.id.emailEt);
        passwordET = findViewById(R.id.input_password);
        btnRegister = findViewById(R.id.btnRegister);
        storeNameEt = findViewById(R.id.storeNameEt);
        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        countryEt = findViewById(R.id.countryEt);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        backImgv = findViewById(R.id.backImgv);

        typeOfBusinessSpinner = findViewById(R.id.typeOfBusinessSpinner);
        numberOfLocationsSpinner = findViewById(R.id.numberOfLocationsSpinner);
        estimatedAnnTurnoverSpinner = findViewById(R.id.estimatedAnnTurnoverSpinner);
        tradingCurrencySpinner = findViewById(R.id.tradingCurrencySpinner);

        progressDialog = new ProgressDialog(this);
    }

    private void initVariables(){
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.register));
        PhoneNumberUtil util = PhoneNumberUtil.createInstance(RegisterActivity.this);
        phoneNumberEt.addTextChangedListener(new PhoneNumberTextWatcher(phoneNumberEt, util));
        initCountrySelectFragment();
        setSpinnerAdapters();
    }

    private void setSpinnerAdapters() {
        setTypeOfBusinessSpinner();
        setNumberOfLocationsSpinner();
        setEstimatedAnnualTurnoverSpinner();
        setTradingCurrencySpinner();
    }

    private void setTypeOfBusinessSpinner(){
        List<String> typeOfBusinessSpinnerList = getTypeOfBusinessSpinnerList();
        typeOfBusinessAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, typeOfBusinessSpinnerList);
        typeOfBusinessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfBusinessSpinner.setAdapter(typeOfBusinessAdapter);
    }

    private void setNumberOfLocationsSpinner(){
        List<String> spinnerList = getNumberOfLocationsSpinnerList();
        numberOfLocationsAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        numberOfLocationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfLocationsSpinner.setAdapter(numberOfLocationsAdapter);
    }

    private void setEstimatedAnnualTurnoverSpinner(){
        List<String> spinnerList = getEstimatedAnnualTurnoverSpinnerList();
        estimatedAnnTurnoverAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        estimatedAnnTurnoverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estimatedAnnTurnoverSpinner.setAdapter(estimatedAnnTurnoverAdapter);
    }

    private void setTradingCurrencySpinner(){
        List<String> spinnerList = getTradingCurrencySpinnerList();
        tradingCurrencyAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        tradingCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tradingCurrencySpinner.setAdapter(tradingCurrencyAdapter);
    }

    private List<String> getTypeOfBusinessSpinnerList() {
        List<String> spinnerList = new ArrayList<>();

        TypeOfBusinessEnum[] values = TypeOfBusinessEnum.values();

        spinnerList.add(RegisterActivity.this.getResources().getString(R.string.choose_a_retail_category));

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            for(TypeOfBusinessEnum item : values)
                spinnerList.add(item.getLabelTr());
        }else{
            for(TypeOfBusinessEnum item : values)
                spinnerList.add(item.getLabelEn());
        }
        return spinnerList;
    }

    private List<String> getNumberOfLocationsSpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        NumberOfLocationEnum[] values = NumberOfLocationEnum.values();

        spinnerList.add(RegisterActivity.this.getResources().getString(R.string.choose_how_many_locs_you_have));

        for(NumberOfLocationEnum item : values)
            spinnerList.add(item.getLabel().concat(" ").concat(getResources().getString(R.string.location)));

        return spinnerList;
    }

    private List<String> getEstimatedAnnualTurnoverSpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        AnnualTurnoverRangeEnum[] values = AnnualTurnoverRangeEnum.values();

        spinnerList.add(RegisterActivity.this.getResources().getString(R.string.choose_annual_turnover_range));

        for(AnnualTurnoverRangeEnum item : values)
            spinnerList.add(item.getLabel().concat(" (").concat(CommonUtils.getCurrency().getSymbol()).concat(")"));

        return spinnerList;
    }

    private List<String> getTradingCurrencySpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        CurrencyEnum[] values = CurrencyEnum.values();

        spinnerList.add(RegisterActivity.this.getResources().getString(R.string.choose_trading_currency));

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            for(CurrencyEnum item : values)
                spinnerList.add(item.getLabelTr());
        }else{
            for(CurrencyEnum item : values)
                spinnerList.add(item.getLabelEn());
        }
        return spinnerList;
    }

    private void initListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegisterClicked();
            }
        });

        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.onBackPressed();
            }
        });

        countryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countrySelectFragment.show(RegisterActivity.this.getSupportFragmentManager(), countrySelectFragment.getTag());
            }
        });

        typeOfBusinessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0)
                    typeOfBusinessEnum = TypeOfBusinessEnum.getById(position);
                else
                    typeOfBusinessEnum = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        estimatedAnnTurnoverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0)
                    annualTurnoverRangeEnum = AnnualTurnoverRangeEnum.getById(position);
                else
                    annualTurnoverRangeEnum = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        numberOfLocationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0)
                    numberOfLocationEnum = NumberOfLocationEnum.getById(position);
                else
                    numberOfLocationEnum = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tradingCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0)
                    currencyEnum = CurrencyEnum.getById(position);
                else
                    currencyEnum = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initCountrySelectFragment(){
        countrySelectFragment = new CountrySelectFragment();
        countrySelectFragment.setCountryListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void btnRegisterClicked() {

        progressDialog.setMessage(this.getString(R.string.registering_user));
        progressDialog.show();

        email = emailEt.getText().toString();
        userPassword = passwordET.getText().toString();

        //validation controls
        if (!checkValidation()) {
            return;
        }

        createUser();
    }

    private boolean checkValidation() {

        User user = UserDBHelper.getUserByEmail(email);

        progressDialog.dismiss();

        if(user != null){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.email_is_defined_before));
            return false;
        }

        //username validation
        if (!Validation.getInstance().isValidEmail(this, email)) {
            CommonUtils.showCustomToast(RegisterActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        //password validation
        if (!Validation.getInstance().isValidPassword(this, userPassword)) {
            CommonUtils.showCustomToast(RegisterActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        if(storeNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_type_store_name));
            return false;
        }

        if(firstNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_type_first_name));
            return false;
        }

        if(lastNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_type_last_name));
            return false;
        }

        if(phoneNumberEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_type_phone_number));
            return false;
        }

        if(countryEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_select_country));
            return false;
        }

        if(typeOfBusinessEnum == null){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_select_type_of_biusiness));
            return false;
        }

        if(annualTurnoverRangeEnum == null){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_select_estimated_annual_turnover));
            return false;
        }

        if(numberOfLocationEnum == null){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_select_number_of_locations));
            return false;
        }

        if(currencyEnum == null){
            CommonUtils.showCustomToast(RegisterActivity.this, getResources().getString(R.string.please_select_trading_currency));
            return false;
        }

        return true;
    }

    private void createUser() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setPassword(userPassword);
        user.setCreateDate(new Date());
        user.setStoreName(storeNameEt.getText().toString());
        user.setFirstName(firstNameEt.getText().toString());
        user.setLastName(lastNameEt.getText().toString());
        user.setPhoneNumber(phoneNumberEt.getText().toString());
        user.setCountry(countryEt.getText().toString());

        if(typeOfBusinessEnum != null)
            user.setTypeOfBusinessId(typeOfBusinessEnum.getId());

        if(numberOfLocationEnum != null)
            user.setNumberOfLocationsId(numberOfLocationEnum.getId());

        if(annualTurnoverRangeEnum != null)
            user.setEstimatedAnnTurnoverId(annualTurnoverRangeEnum.getId());

        if(currencyEnum != null)
            user.setTradingCurrencyId(currencyEnum.getId());

        user.setLoggedIn(true);

        realm.commitTransaction();

        BaseResponse baseResponse = UserDBHelper.createOrUpdateUser(user);

        progressDialog.dismiss();

        if(baseResponse.isSuccess()){
            LoginUtils.applySharedPreferences(RegisterActivity.this,
                    user.getEmail(), user.getPassword(), user.getId());

            Intent intent = new Intent(RegisterActivity.this, InitialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            CommonUtils.showToastShort(RegisterActivity.this, baseResponse.getMessage());
        }
    }

    @Override
    public void onCountryClick(String country) {
        if(country != null && !country.isEmpty())
            countryEt.setText(country);
    }
}
