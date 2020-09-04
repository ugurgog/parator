package com.paypad.parator.menu.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.login.InitialActivity;
import com.paypad.parator.login.utils.LoginUtils;
import com.paypad.parator.menu.settings.passcode.PasscodeManagementFragment;
import com.paypad.parator.menu.settings.profile.EditProfileFragment;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    @BindView(R.id.viewProfileRl)
    RelativeLayout viewProfileRl;
    @BindView(R.id.passcodeRl)
    RelativeLayout passcodeRl;

    private User user;
    private Context mContext;

    public SettingsFragment() {
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

        CommonUtils.showNavigationBar((Activity) mContext);

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

        viewProfileRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new EditProfileFragment());
            }
        });

        passcodeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new PasscodeManagementFragment());
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
            BaseResponse baseResponse = UserDBHelper.updateUserLoggedInStatus(user.getId(), false);
            DataUtils.showBaseResponseMessage(getContext(), baseResponse);

            if(baseResponse.isSuccess()){
                Objects.requireNonNull(getActivity()).finish();
                startActivity(new Intent(getActivity(), InitialActivity.class));
            }
        }else
            CommonUtils.showToastShort(getContext(), "cache delete error!");
    }

}