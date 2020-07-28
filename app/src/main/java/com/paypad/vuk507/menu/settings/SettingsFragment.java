package com.paypad.vuk507.menu.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.payment.MailResultFragment;
import com.paypad.vuk507.charge.payment.utils.PrintReceiptManager;
import com.paypad.vuk507.charge.payment.utils.SendMail;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingsFragment extends BaseFragment{

    private View mView;

    //Toolbar views
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.btnSignOut)
    Button btnSignOut;

    @BindView(R.id.printOrdersRl)
    RelativeLayout printOrdersRl;

    private User user;

    public SettingsFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_settings, container, false);
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
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signOutProcess();
            }
        });

        printOrdersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new PrintOrdersFragment());
            }
        });
    }

    private void initVariables() {
        saveBtn.setVisibility(View.GONE);
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.settings));
    }

    private void signOutProcess(){
        boolean commit = LoginUtils.deleteSharedPreferences(getContext());

        if(commit){
            BaseResponse baseResponse = UserDBHelper.updateUserLoggedInStatus(user.getUsername(), false);
            DataUtils.showBaseResponseMessage(getContext(), baseResponse);

            if(baseResponse.isSuccess()){
                Objects.requireNonNull(getActivity()).finish();
                startActivity(new Intent(getActivity(), InitialActivity.class));
            }
        }else
            CommonUtils.showToastShort(getContext(), "cache delete error!");
    }

}