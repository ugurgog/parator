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
import com.paypad.parator.db.StoreDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.AnnualTurnoverRangeEnum;
import com.paypad.parator.enums.CurrencyEnum;
import com.paypad.parator.enums.NumberOfLocationEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.enums.TypeOfBusinessEnum;
import com.paypad.parator.interfaces.CountrySelectListener;
import com.paypad.parator.login.utils.LoginUtils;
import com.paypad.parator.login.utils.Validation;
import com.paypad.parator.menu.customer.CountrySelectFragment;
import com.paypad.parator.model.Store;
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
import io.realm.annotations.Index;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;


public class RegisterStoreActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText storeNameEt;

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

    private String userId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            userId = bundle.getString("userId");

        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        initViews();
        initListeners();
        initVariables();
        setShapes();
    }

    public void setShapes(){
        storeNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        btnRegister.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 0));
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btnRegister);
        storeNameEt = findViewById(R.id.storeNameEt);
        typeOfBusinessSpinner = findViewById(R.id.typeOfBusinessSpinner);
        numberOfLocationsSpinner = findViewById(R.id.numberOfLocationsSpinner);
        estimatedAnnTurnoverSpinner = findViewById(R.id.estimatedAnnTurnoverSpinner);
        tradingCurrencySpinner = findViewById(R.id.tradingCurrencySpinner);

        progressDialog = new ProgressDialog(this);
    }

    private void initVariables(){
        currencyEnum = CurrencyEnum.TL;
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
        typeOfBusinessAdapter = new ArrayAdapter<>(RegisterStoreActivity.this, android.R.layout.simple_spinner_item, typeOfBusinessSpinnerList);
        typeOfBusinessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfBusinessSpinner.setAdapter(typeOfBusinessAdapter);
    }

    private void setNumberOfLocationsSpinner(){
        List<String> spinnerList = getNumberOfLocationsSpinnerList();
        numberOfLocationsAdapter = new ArrayAdapter<>(RegisterStoreActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        numberOfLocationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfLocationsSpinner.setAdapter(numberOfLocationsAdapter);
    }

    private void setEstimatedAnnualTurnoverSpinner(){
        List<String> spinnerList = getEstimatedAnnualTurnoverSpinnerList();
        estimatedAnnTurnoverAdapter = new ArrayAdapter<>(RegisterStoreActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        estimatedAnnTurnoverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estimatedAnnTurnoverSpinner.setAdapter(estimatedAnnTurnoverAdapter);
    }

    private void setTradingCurrencySpinner(){
        List<String> spinnerList = getTradingCurrencySpinnerList();
        tradingCurrencyAdapter = new ArrayAdapter<>(RegisterStoreActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        tradingCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tradingCurrencySpinner.setAdapter(tradingCurrencyAdapter);
    }

    private List<String> getTypeOfBusinessSpinnerList() {
        List<String> spinnerList = new ArrayList<>();

        TypeOfBusinessEnum[] values = TypeOfBusinessEnum.values();

        spinnerList.add(RegisterStoreActivity.this.getResources().getString(R.string.choose_a_retail_category));

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

        spinnerList.add(RegisterStoreActivity.this.getResources().getString(R.string.choose_how_many_locs_you_have));

        for(NumberOfLocationEnum item : values)
            spinnerList.add(item.getLabel().concat(" ").concat(getResources().getString(R.string.location)));

        return spinnerList;
    }

    private List<String> getEstimatedAnnualTurnoverSpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        AnnualTurnoverRangeEnum[] values = AnnualTurnoverRangeEnum.values();

        spinnerList.add(RegisterStoreActivity.this.getResources().getString(R.string.choose_annual_turnover_range));

        for(AnnualTurnoverRangeEnum item : values)
            spinnerList.add(item.getLabel().concat(" (").concat(CommonUtils.getCurrency().getSymbol()).concat(")"));

        return spinnerList;
    }

    private List<String> getTradingCurrencySpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        CurrencyEnum[] values = CurrencyEnum.values();

        spinnerList.add(RegisterStoreActivity.this.getResources().getString(R.string.choose_trading_currency));

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

        //validation controls
        if (!checkValidation()) {
            return;
        }

        createStore();
    }

    private boolean checkValidation() {
        progressDialog.dismiss();

        if(storeNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(RegisterStoreActivity.this, getResources().getString(R.string.please_type_store_name), ToastEnum.TOAST_WARNING);
            return false;
        }

        if(typeOfBusinessEnum == null){
            CommonUtils.showCustomToast(RegisterStoreActivity.this, getResources().getString(R.string.please_select_type_of_biusiness), ToastEnum.TOAST_WARNING);
            return false;
        }

        if(annualTurnoverRangeEnum == null){
            CommonUtils.showCustomToast(RegisterStoreActivity.this, getResources().getString(R.string.please_select_estimated_annual_turnover), ToastEnum.TOAST_WARNING);
            return false;
        }

        if(numberOfLocationEnum == null){
            CommonUtils.showCustomToast(RegisterStoreActivity.this, getResources().getString(R.string.please_select_number_of_locations), ToastEnum.TOAST_WARNING);
            return false;
        }

        if(currencyEnum == null){
            CommonUtils.showCustomToast(RegisterStoreActivity.this, getResources().getString(R.string.please_select_trading_currency), ToastEnum.TOAST_WARNING);
            return false;
        }

        return true;
    }

    private void createStore() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Store store = new Store();
        store.setId(UUID.randomUUID().toString());
        store.setUserId(userId);
        store.setStoreName(storeNameEt.getText().toString());
        store.setCreateDate(new Date());

        if(typeOfBusinessEnum != null)
            store.setTypeOfBusiness(typeOfBusinessEnum.getId());

        if(numberOfLocationEnum != null)
            store.setNumberOfLocations(numberOfLocationEnum.getId());

        if(annualTurnoverRangeEnum != null)
            store.setEstimatedAnnTurnover(annualTurnoverRangeEnum.getId());

        if(currencyEnum != null)
            store.setTradingCurrency(currencyEnum.getId());

        realm.commitTransaction();

        BaseResponse baseResponse = StoreDBHelper.createOrUpdateStore(store);

        progressDialog.dismiss();

        if(baseResponse.isSuccess()){
            Intent intent = new Intent(RegisterStoreActivity.this, AppIntroductionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            CommonUtils.showCustomToast(RegisterStoreActivity.this, baseResponse.getMessage(), ToastEnum.TOAST_ERROR);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
