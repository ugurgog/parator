package com.paypad.parator.menu.settings.profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.StoreDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.AnnualTurnoverRangeEnum;
import com.paypad.parator.enums.CurrencyEnum;
import com.paypad.parator.enums.NumberOfLocationEnum;
import com.paypad.parator.enums.TypeOfBusinessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CountrySelectListener;
import com.paypad.parator.login.RegisterStoreActivity;
import com.paypad.parator.menu.customer.CountrySelectFragment;
import com.paypad.parator.menu.settings.passcode.ReturnSettingsFragment;
import com.paypad.parator.model.Store;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.uiUtils.NDSpinner;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.PhoneNumberTextWatcher;
import com.paypad.parator.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class EditStoreFragment extends BaseFragment {

    private View mView;

    //Toolbar views
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.storeNameEt)
    EditText storeNameEt;
    @BindView(R.id.typeOfBusinessSpinner)
    NDSpinner typeOfBusinessSpinner;
    @BindView(R.id.numberOfLocationsSpinner)
    NDSpinner numberOfLocationsSpinner;
    @BindView(R.id.estimatedAnnTurnoverSpinner)
    NDSpinner estimatedAnnTurnoverSpinner;
    @BindView(R.id.tradingCurrencySpinner)
    NDSpinner tradingCurrencySpinner;

    private TypeOfBusinessEnum typeOfBusinessEnum = null;
    private NumberOfLocationEnum numberOfLocationEnum = null;
    private AnnualTurnoverRangeEnum annualTurnoverRangeEnum = null;
    private CurrencyEnum currencyEnum = null;

    private ArrayAdapter<String> typeOfBusinessAdapter;
    private ArrayAdapter<String> numberOfLocationsAdapter;
    private ArrayAdapter<String> estimatedAnnTurnoverAdapter;
    private ArrayAdapter<String> tradingCurrencyAdapter;

    private User user;
    private Realm realm;
    private Context mContext;
    private Store store;

    public EditStoreFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CommonUtils.showNavigationBar((Activity) mContext);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_edit_store, container, false);
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
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidation())
                    updateStore();
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
                currencyEnum = CurrencyEnum.getById(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean checkValidation() {
        if(storeNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_type_store_name));
            return false;
        }

        if(typeOfBusinessEnum == null){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_type_of_biusiness));
            return false;
        }

        if(annualTurnoverRangeEnum == null){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_estimated_annual_turnover));
            return false;
        }

        if(numberOfLocationEnum == null){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_number_of_locations));
            return false;
        }

        if(currencyEnum == null){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_trading_currency));
            return false;
        }
        return true;
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_store));
        store = StoreDBHelper.getStoreByUserId(user.getId());
        setSpinnerAdapters();
        fillStoreFields();
    }

    private void setSpinnerAdapters() {
        setTypeOfBusinessSpinner();
        setNumberOfLocationsSpinner();
        setEstimatedAnnualTurnoverSpinner();
        setTradingCurrencySpinner();
    }

    private void setTypeOfBusinessSpinner(){
        List<String> typeOfBusinessSpinnerList = getTypeOfBusinessSpinnerList();
        typeOfBusinessAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, typeOfBusinessSpinnerList);
        typeOfBusinessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfBusinessSpinner.setAdapter(typeOfBusinessAdapter);
    }

    private void setNumberOfLocationsSpinner(){
        List<String> spinnerList = getNumberOfLocationsSpinnerList();
        numberOfLocationsAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerList);
        numberOfLocationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfLocationsSpinner.setAdapter(numberOfLocationsAdapter);
    }

    private void setEstimatedAnnualTurnoverSpinner(){
        List<String> spinnerList = getEstimatedAnnualTurnoverSpinnerList();
        estimatedAnnTurnoverAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerList);
        estimatedAnnTurnoverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estimatedAnnTurnoverSpinner.setAdapter(estimatedAnnTurnoverAdapter);
    }

    private void setTradingCurrencySpinner(){
        List<String> spinnerList = getTradingCurrencySpinnerList();
        tradingCurrencyAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerList);
        tradingCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tradingCurrencySpinner.setAdapter(tradingCurrencyAdapter);
    }

    private List<String> getTypeOfBusinessSpinnerList() {
        List<String> spinnerList = new ArrayList<>();

        TypeOfBusinessEnum[] values = TypeOfBusinessEnum.values();

        spinnerList.add(mContext.getResources().getString(R.string.choose_a_retail_category));

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

        spinnerList.add(mContext.getResources().getString(R.string.choose_how_many_locs_you_have));

        for(NumberOfLocationEnum item : values)
            spinnerList.add(item.getLabel().concat(" ").concat(getResources().getString(R.string.location)));

        return spinnerList;
    }

    private List<String> getEstimatedAnnualTurnoverSpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        AnnualTurnoverRangeEnum[] values = AnnualTurnoverRangeEnum.values();

        spinnerList.add(mContext.getResources().getString(R.string.choose_annual_turnover_range));

        for(AnnualTurnoverRangeEnum item : values)
            spinnerList.add(item.getLabel().concat(" (").concat(CommonUtils.getCurrency().getSymbol()).concat(")"));

        return spinnerList;
    }

    private List<String> getTradingCurrencySpinnerList(){
        List<String> spinnerList = new ArrayList<>();

        CurrencyEnum[] values = CurrencyEnum.values();

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            for(CurrencyEnum item : values)
                spinnerList.add(item.getLabelTr());
        }else{
            for(CurrencyEnum item : values)
                spinnerList.add(item.getLabelEn());
        }
        return spinnerList;
    }

    private void fillStoreFields() {
        if(store.getStoreName() != null)
            storeNameEt.setText(store.getStoreName());

        typeOfBusinessSpinner.setSelection(store.getTypeOfBusiness());
        numberOfLocationsSpinner.setSelection(store.getNumberOfLocations());
        estimatedAnnTurnoverSpinner.setSelection(store.getEstimatedAnnTurnover());
        tradingCurrencySpinner.setSelection(store.getTradingCurrency());
    }

    private void updateStore(){
        realm.beginTransaction();

        store.setStoreName(storeNameEt.getText().toString());
        store.setUpdateDate(new Date());
        store.setUpdateUserId(user.getId());

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
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            mFragmentNavigation.pushFragment(
                    new ReturnSettingsFragment(mContext.getResources().getString(R.string.update_store_success), 2));
        }
    }
}