package com.paypad.parator.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.AnnualTurnoverRangeEnum;
import com.paypad.parator.enums.CurrencyEnum;
import com.paypad.parator.enums.NumberOfLocationEnum;
import com.paypad.parator.enums.TypeOfBusinessEnum;
import com.paypad.parator.interfaces.CountrySelectListener;
import com.paypad.parator.login.utils.LoginUtils;
import com.paypad.parator.login.utils.Validation;
import com.paypad.parator.menu.customer.CountrySelectFragment;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.uiUtils.NDSpinner;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.PhoneNumberTextWatcher;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;


public class RegisterActivity extends AppCompatActivity
        implements CountrySelectListener {

    private EditText emailEt;
    private EditText passwordET;
    private Button btnRegister;
    private EditText firstNameEt;
    private EditText lastNameEt;
    private EditText phoneNumberEt;
    private EditText countryEt;

    private String email;
    private String userPassword;
    private ProgressDialog progressDialog;
    private CountrySelectFragment countrySelectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        countryEt = findViewById(R.id.countryEt);

        progressDialog = new ProgressDialog(this);
    }

    private void initVariables(){
        PhoneNumberUtil util = PhoneNumberUtil.createInstance(RegisterActivity.this);
        phoneNumberEt.addTextChangedListener(new PhoneNumberTextWatcher(phoneNumberEt, util));
        initCountrySelectFragment();
    }

    private void initListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegisterClicked();
            }
        });

        countryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countrySelectFragment.show(RegisterActivity.this.getSupportFragmentManager(), countrySelectFragment.getTag());
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

        //email validation
        if (!Validation.getInstance().isValidEmail(this, email)) {
            CommonUtils.showCustomToast(RegisterActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        //password validation
        if (!Validation.getInstance().isValidPassword(this, userPassword)) {
            CommonUtils.showCustomToast(RegisterActivity.this, Validation.getInstance().getErrorMessage());
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
        user.setFirstName(firstNameEt.getText().toString());
        user.setLastName(lastNameEt.getText().toString());
        user.setPhoneNumber(phoneNumberEt.getText().toString());
        user.setCountry(countryEt.getText().toString());

        user.setLoggedIn(true);

        realm.commitTransaction();

        BaseResponse baseResponse = UserDBHelper.createOrUpdateUser(user);

        progressDialog.dismiss();

        if(baseResponse.isSuccess()){
            LoginUtils.applySharedPreferences(RegisterActivity.this,
                    user.getEmail(), user.getPassword(), user.getId());

            Intent intent = new Intent(RegisterActivity.this, RegisterStoreActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Bundle bundle = new Bundle();
            bundle.putString("userId", user.getId());
            intent.putExtras(bundle);
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
