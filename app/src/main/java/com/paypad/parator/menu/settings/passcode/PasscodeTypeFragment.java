package com.paypad.parator.menu.settings.passcode;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.hanks.passcodeview.PasscodeView;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PasscodeTimeoutEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.PasscodeTypeCallback;
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

import static com.paypad.parator.constants.CustomConstants.PASSCODE_CREATE;

public class PasscodeTypeFragment extends BaseFragment {

    private View mView;

    //PassCodeView passCodeView;

    private PasscodeView passCodeView;

    private User user;
    private Context mContext;
    private String passcodeVal;
    private PasscodeTypeCallback passcodeTypeCallback;

    public PasscodeTypeFragment(String passcodeVal, PasscodeTypeCallback passcodeTypeCallback) {
        this.passcodeVal = passcodeVal;
        this.passcodeTypeCallback = passcodeTypeCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
    }

    private void initVariables() {

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_type_passcode, container, false);
            ButterKnife.bind(this, mView);
            passCodeView = (PasscodeView) mView.findViewById(R.id.passcode_view);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {

        /*passCodeView.setKeyTextColor(mContext.getResources().getColor(R.color.Black, null));
        passCodeView.setKeyTextSize(30);
        passCodeView.setDigitLength(4);
        passCodeView.setPassCode(passcodeVal);

        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {

                Log.i("Info", "text:" + text);
            }
        });*/


        passCodeView.setPasscodeLength(4)
                .setLocalPasscode(passcodeVal)
                .setListener(new PasscodeView.PasscodeViewListener() {
                    @Override
                    public void onFail() {
                        CommonUtils.showCustomToast(mContext, mContext.getResources().getString(R.string.wrong_password_entered));
                        passcodeTypeCallback.OnResult(false);
                    }

                    @Override
                    public void onSuccess(String number) {
                        passcodeTypeCallback.OnResult(true);
                        mFragmentNavigation.popFragments(1);
                    }
                });
    }
}