package com.paypad.parator.menu.settings.passcode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PasscodeTimeoutEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.uiUtils.keypad.KeyPadClick;
import com.paypad.parator.uiUtils.keypad.KeyPadPasscodeCreate;
import com.paypad.parator.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.PASSCODE_CREATE;

public class PasscodeEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.editPasscodeTv)
    TextView editPasscodeTv;

    @BindView(R.id.edt1)
    EditText edt1;
    @BindView(R.id.edt2)
    EditText edt2;
    @BindView(R.id.edt3)
    EditText edt3;
    @BindView(R.id.edt4)
    EditText edt4;

    private Realm realm;
    private User user;
    private Passcode passcode;
    private Context mContext;
    private int passcodeActionValue;
    private KeyPadPasscodeCreate keypad;
    private String passcodeVal = null;

    public PasscodeEditFragment(Passcode passcode, int passcodeActionValue) {
        this.passcode = passcode;
        this.passcodeActionValue = passcodeActionValue;
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
        CommonUtils.showNavigationBar((Activity) mContext);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_edit_passcode, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        keypad.setOnNumPadClickListener(new KeyPadClick(new KeyPadSingleNumberListener() {
            @Override
            public void onKeypadClicked(Integer number) {
                fillPasscodeCells(number);
            }
        }));

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.popFragments(1);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passcodeVal = getPasscodeValue();

                if(passcodeVal.trim().length() < 4){
                    CommonUtils.showCustomToast(mContext, mContext.getResources().getString(R.string.please_enter_four_digit_passcode), ToastEnum.TOAST_WARNING);
                    return;
                }
                updatePasscode();
            }
        });
    }

    private String getPasscodeValue() {
        String value = edt1.getText().toString()
                .concat(edt2.getText().toString())
                .concat(edt3.getText().toString())
                .concat(edt4.getText().toString());
        return value;
    }

    private void fillPasscodeCells(Integer number) {
        if(number != -2){
            if(edt1.getText().toString().isEmpty())
                edt1.setText(String.valueOf(number));
            else if(edt2.getText().toString().isEmpty())
                edt2.setText(String.valueOf(number));
            else if(edt3.getText().toString().isEmpty())
                edt3.setText(String.valueOf(number));
            else if(edt4.getText().toString().isEmpty())
                edt4.setText(String.valueOf(number));
        }else {
            if(!edt4.getText().toString().isEmpty())
                edt4.setText("");
            else if(!edt3.getText().toString().isEmpty())
                edt3.setText("");
            else if(!edt2.getText().toString().isEmpty())
                edt2.setText("");
            else if(!edt1.getText().toString().isEmpty())
                edt1.setText("");
        }

        if(!edt1.getText().toString().isEmpty() && !edt2.getText().toString().isEmpty() && !edt3.getText().toString().isEmpty() &&
                !edt4.getText().toString().isEmpty()){
            CommonUtils.setSaveBtnEnability(true, saveBtn, mContext);
        }else {
            CommonUtils.setSaveBtnEnability(false, saveBtn, mContext);
        }
    }


    private void initVariables() {
        CommonUtils.setSaveBtnEnability(false, saveBtn, mContext);
        keypad = mView.findViewById(R.id.keypad);
        editPasscodeTv.setText(mContext.getResources().getString(R.string.enter_passcode));
        realm = Realm.getDefaultInstance();

        if(passcodeActionValue == PASSCODE_CREATE)
            toolbarTitleTv.setText(mContext.getResources().getString(R.string.create_passcode));
        else {
            toolbarTitleTv.setText(mContext.getResources().getString(R.string.edit_passcode));
        }
    }

    private void updatePasscode() {
        realm.beginTransaction();

        if(passcode == null){
            passcode = new Passcode();
            passcode.setId(UUID.randomUUID().toString());
            passcode.setUserId(user.getId());
            passcode.setEnabled(false);
            passcode.setPasscodeVal(passcodeVal);
            passcode.setAfterEachSaleEnabled(false);
            passcode.setBackOutOfSaleEnabled(false);
            passcode.setTimeOutId(PasscodeTimeoutEnum.NEVER.getId());
            passcode.setCreateDate(new Date());
        }else {
            passcode.setPasscodeVal(passcodeVal);
            passcode.setUpdateDate(new Date());
            passcode.setUpdateUserId(user.getId());
        }

        realm.commitTransaction();

        BaseResponse baseResponse = PasscodeDBHelper.createOrUpdatePasscode(passcode);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            mFragmentNavigation.pushFragment(new ReturnSettingsFragment(
                    mContext.getResources().getString(R.string.passcode_updated_successfully), 3
            ));
        }
    }
}