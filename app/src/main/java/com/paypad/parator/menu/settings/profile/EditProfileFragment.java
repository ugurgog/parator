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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.GlobalSettingsDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CountrySelectListener;
import com.paypad.parator.login.RegisterActivity;
import com.paypad.parator.menu.customer.CountrySelectFragment;
import com.paypad.parator.menu.settings.passcode.ReturnSettingsFragment;
import com.paypad.parator.model.GlobalSettings;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.Country;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.PhoneNumberTextWatcher;
import com.paypad.parator.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;

public class EditProfileFragment extends BaseFragment implements CountrySelectListener {

    private View mView;

    //Toolbar views
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.firstNameEt)
    EditText firstNameEt;
    @BindView(R.id.lastNameEt)
    EditText lastNameEt;
    @BindView(R.id.phoneNumberEt)
    EditText phoneNumberEt;
    @BindView(R.id.countryEt)
    EditText countryEt;

    @BindView(R.id.firstNameTv)
    AppCompatTextView firstNameTv;
    @BindView(R.id.lastNameTv)
    AppCompatTextView lastNameTv;
    @BindView(R.id.emailTv)
    AppCompatTextView emailTv;

    private User user;
    private UserBus mUserBus;
    private Realm realm;
    private Context mContext;
    private CountrySelectFragment countrySelectFragment;

    public EditProfileFragment() {
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
        mUserBus = userBus;
        if(user == null){
            User tempUser = UserDBHelper.getUserFromCache(getContext());
            user = UserDBHelper.getUserById(tempUser.getId());
        }
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
            mView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
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

        firstNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().isEmpty()) {
                    firstNameTv.setText(editable.toString());
                }else
                    firstNameTv.setText("");
            }
        });

        lastNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().isEmpty()) {
                    lastNameTv.setText(editable.toString());
                }else {
                    lastNameTv.setText("");
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidation())
                    updateUser();
            }
        });

        countryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCountrySelectFragment();
                countrySelectFragment.show(getActivity().getSupportFragmentManager(), countrySelectFragment.getTag());
            }
        });
    }

    private void initCountrySelectFragment(){
        countrySelectFragment = new CountrySelectFragment();
        countrySelectFragment.setCountryListener(this);
    }

    private boolean checkValidation() {
        if(firstNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_type_first_name));
            return false;
        }

        if(lastNameEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_type_last_name));
            return false;
        }

        if(phoneNumberEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_type_phone_number));
            return false;
        }

        if(countryEt.getText().toString().isEmpty()){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_country));
            return false;
        }
        return true;
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_profile));
        fillUserFields();
        setShapes();
    }

    public void setShapes(){
        firstNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        lastNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        phoneNumberEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        countryEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
    }

    private void fillUserFields() {
        if(user.getEmail() != null)
            emailTv.setText(user.getEmail());

        if(user.getFirstName() != null){
            firstNameTv.setText(user.getFirstName());
            firstNameEt.setText(user.getFirstName());
        }

        if(user.getLastName() != null){
            lastNameTv.setText(user.getLastName());
            lastNameEt.setText(user.getLastName());
        }

        PhoneNumberUtil util = PhoneNumberUtil.createInstance(Objects.requireNonNull(getContext()));
        phoneNumberEt.addTextChangedListener(new PhoneNumberTextWatcher(phoneNumberEt, util));

        if(user.getPhoneNumber() != null)
            phoneNumberEt.setText(user.getPhoneNumber());

        if(user.getCountry() != null)
            countryEt.setText(user.getCountry());
    }

    private void updateUser(){
        realm.beginTransaction();

        user.setFirstName(firstNameEt.getText().toString());
        user.setLastName(lastNameEt.getText().toString());
        user.setPhoneNumber(phoneNumberEt.getText().toString());
        user.setCountry(countryEt.getText().toString());
        user.setUpdateDate(new Date());

        realm.commitTransaction();

        BaseResponse baseResponse = UserDBHelper.createOrUpdateUser(user);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            mUserBus.setUser(user);
            EventBus.getDefault().postSticky(mUserBus);
            mFragmentNavigation.pushFragment(
                    new ReturnSettingsFragment(mContext.getResources().getString(R.string.update_profile_success), 2));
        }
    }

    @Override
    public void onCountryClick(Country country) {
        if(country != null && country.getName() != null)
            countryEt.setText(country.getName());
    }
}