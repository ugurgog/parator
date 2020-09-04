package com.paypad.parator.menu.settings.passcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.PasscodeTimeoutEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.PasscodeTimeoutCallback;
import com.paypad.parator.interfaces.PasscodeTypeCallback;
import com.paypad.parator.login.InitialActivity;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.PASSCODE_CREATE;
import static com.paypad.parator.constants.CustomConstants.PASSCODE_EDIT;

public class PasscodeManagementFragment extends BaseFragment implements PasscodeTimeoutCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.passcodeEnableSwitch)
    Switch passcodeEnableSwitch;
    @BindView(R.id.backingToSaleCb)
    CheckBox backingToSaleCb;
    @BindView(R.id.afterEachSalecb)
    CheckBox afterEachSalecb;
    @BindView(R.id.passcodeEnabledll)
    LinearLayout passcodeEnabledll;
    @BindView(R.id.timeoutValueTv)
    TextView timeoutValueTv;
    @BindView(R.id.editPasscodeTv)
    TextView editPasscodeTv;

    private Realm realm;
    private User user;
    private Passcode passcode;
    private Context mContext;
    private int passcodeActionValue = PASSCODE_CREATE;
    private boolean isManuallyChanged = false;
    private PasscodeTimeoutSelectFragment passcodeTimeoutSelectFragment;

    public PasscodeManagementFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
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
            user = UserDBHelper.getUserFromCache(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_passcode_management, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        editPasscodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passcode == null)
                    mFragmentNavigation.pushFragment(new PasscodeEditFragment(passcode, passcodeActionValue));
                else {
                    //startActivity(new Intent(getActivity(), PasscodeTypeActivity.class));



                    if(passcode.getPasscodeVal() != null && !passcode.getPasscodeVal().isEmpty()){

                        LogUtil.logPasscode(passcode);

                        Intent intent = new Intent(mContext, PasscodeTypeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("toolbarVisible", true);
                        bundle.putString("passcodeVal", passcode.getPasscodeVal());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                    }

                    /*mFragmentNavigation.pushFragment(new PasscodeTypeFragment(passcode.getPasscodeVal(), new PasscodeTypeCallback() {
                        @Override
                        public void OnResult(boolean result) {
                            if(result)
                                mFragmentNavigation.pushFragment(new PasscodeEditFragment(passcode, passcodeActionValue));
                        }
                    }));*/
                }
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.popFragments(1);
            }
        });

        passcodeEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(passcode == null){
                    if(b){
                        CommonUtils.showCustomToast(mContext, mContext.getResources().getString(R.string.please_first_create_passcode));
                        passcodeEnableSwitch.setChecked(false);
                    }
                } else if (passcode.getPasscodeVal() != null && !passcode.getPasscodeVal().isEmpty()) {
                    if (b) {
                        Intent intent = new Intent(mContext, PasscodeTypeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("toolbarVisible", true);
                        bundle.putString("passcodeVal", passcode.getPasscodeVal());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 2);
                    } else {
                        updatePasscodeEnabled(false);
                    }
                }
            }
        });

        afterEachSalecb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(afterEachSalecb.isChecked())
                    updateAfterEachSale(true);
                else
                    updateAfterEachSale(false);
            }
        });

        backingToSaleCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(backingToSaleCb.isChecked())
                    updateBackingToSale(true);
                else
                    updateBackingToSale(false);
            }
        });

        timeoutValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPasscodeTimeoutSelectFragment();
                mFragmentNavigation.pushFragment(passcodeTimeoutSelectFragment);
            }
        });
    }

    private void initPasscodeTimeoutSelectFragment(){
        passcodeTimeoutSelectFragment = new PasscodeTimeoutSelectFragment(passcode);
        passcodeTimeoutSelectFragment.setPasscodeTimeoutCallback(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            boolean passcodeTypeSucceed = data.getBooleanExtra("isSucceed", true);

            if(passcodeTypeSucceed)
                mFragmentNavigation.pushFragment(new PasscodeEditFragment(passcode, passcodeActionValue));
        }else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                boolean passcodeTypeSucceed = data.getBooleanExtra("isSucceed", true);

                if(passcodeTypeSucceed){
                    updatePasscodeEnabled(true);
                }else
                    passcodeEnableSwitch.setChecked(false);
            }else if(resultCode == Activity.RESULT_CANCELED){
                passcodeEnableSwitch.setChecked(false);
            }

        }
    }

    private void updatePasscodeEnabled(boolean isEnabled) {
        realm.beginTransaction();
        passcode.setEnabled(isEnabled);
        passcode.setUpdateDate(new Date());
        passcode.setUpdateUserId(user.getId());
        realm.commitTransaction();

        BaseResponse baseResponse = PasscodeDBHelper.createOrUpdatePasscode(passcode);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            if(isEnabled)
                passcodeEnabledll.setVisibility(View.VISIBLE);
            else
                passcodeEnabledll.setVisibility(View.GONE);
        }
    }

    private void updateAfterEachSale(boolean isAfterEachSale) {
        realm.beginTransaction();
        passcode.setAfterEachSaleEnabled(isAfterEachSale);
        passcode.setUpdateDate(new Date());
        passcode.setUpdateUserId(user.getId());
        realm.commitTransaction();

        BaseResponse baseResponse = PasscodeDBHelper.createOrUpdatePasscode(passcode);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            afterEachSalecb.setChecked(!isAfterEachSale);
    }

    private void updateBackingToSale(boolean isBackingToSale) {
        realm.beginTransaction();
        passcode.setBackOutOfSaleEnabled(isBackingToSale);
        passcode.setUpdateDate(new Date());
        passcode.setUpdateUserId(user.getId());
        realm.commitTransaction();

        BaseResponse baseResponse = PasscodeDBHelper.createOrUpdatePasscode(passcode);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            backingToSaleCb.setChecked(!isBackingToSale);
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        saveBtn.setVisibility(View.GONE);
        passcode = PasscodeDBHelper.getPasscodeByUserId(user.getId());
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.passcode_management));

        if(passcode == null){
            passcodeActionValue = PASSCODE_CREATE;
            editPasscodeTv.setText(mContext.getResources().getString(R.string.create_passcode));
            passcodeEnabledll.setVisibility(View.GONE);
        }else {
            passcodeActionValue = PASSCODE_EDIT;
            editPasscodeTv.setText(mContext.getResources().getString(R.string.edit_passcode));

            if(passcode.isEnabled()){
                passcodeEnabledll.setVisibility(View.VISIBLE);
                passcodeEnableSwitch.setChecked(true);
            } else{
                passcodeEnabledll.setVisibility(View.GONE);
                passcodeEnableSwitch.setChecked(false);
            }

            afterEachSalecb.setChecked(passcode.isAfterEachSaleEnabled());
            backingToSaleCb.setChecked(passcode.isBackOutOfSaleEnabled());

            setTimeoutValue(PasscodeTimeoutEnum.getById(passcode.getTimeOutId()));
        }
    }

    @Override
    public void OnTimeoutReturn(PasscodeTimeoutEnum passcodeTimeoutType) {
        realm.beginTransaction();
        passcode.setTimeOutId(passcodeTimeoutType.getId());
        realm.commitTransaction();
        setTimeoutValue(passcodeTimeoutType);

        ((MainActivity) getActivity()).setCounterForPasscode();
    }

    private void setTimeoutValue(PasscodeTimeoutEnum passcodeTimeoutType){
        if(passcodeTimeoutType.getId() == PasscodeTimeoutEnum.NEVER.getId())
            timeoutValueTv.setText(mContext.getResources().getString(R.string.never));
        else if(passcodeTimeoutType.getId() == PasscodeTimeoutEnum.SECOND_30.getId())
            timeoutValueTv.setText(mContext.getResources().getString(R.string.thirty_seconds));
        else if(passcodeTimeoutType.getId() == PasscodeTimeoutEnum.MINUTE_1.getId())
            timeoutValueTv.setText(mContext.getResources().getString(R.string.one_minute));
        else if(passcodeTimeoutType.getId() == PasscodeTimeoutEnum.MINUTE_5.getId())
            timeoutValueTv.setText(mContext.getResources().getString(R.string.five_minute));
    }
}