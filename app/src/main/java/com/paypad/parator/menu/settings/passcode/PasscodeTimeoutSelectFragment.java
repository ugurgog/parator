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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PasscodeTimeoutEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.PasscodeTimeoutCallback;
import com.paypad.parator.menu.category.adapters.CategoryListAdapter;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.menu.settings.passcode.adapters.PasscodeTimeoutSelectAdapter;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.PASSCODE_CREATE;
import static com.paypad.parator.constants.CustomConstants.PASSCODE_EDIT;

public class PasscodeTimeoutSelectFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Realm realm;
    private User user;
    private Context mContext;
    private Passcode passcode;
    private PasscodeTimeoutSelectAdapter passcodeTimeoutSelectAdapter;
    private PasscodeTimeoutCallback passcodeTimeoutCallback;
    private PasscodeTimeoutEnum selectedPasscodeTimeoutType;

    public PasscodeTimeoutSelectFragment(Passcode passcode) {
        this.passcode = passcode;
    }

    public void setPasscodeTimeoutCallback(PasscodeTimeoutCallback passcodeTimeoutCallback) {
        this.passcodeTimeoutCallback = passcodeTimeoutCallback;
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
            mView = inflater.inflate(R.layout.fragment_passcode_timeout_select, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePasscodeTimeout();
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.popFragments(1);
            }
        });
    }
    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.timeout));
        selectedPasscodeTimeoutType = PasscodeTimeoutEnum.getById(passcode.getTimeOutId());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        passcodeTimeoutSelectAdapter = new PasscodeTimeoutSelectAdapter(mContext, selectedPasscodeTimeoutType, new PasscodeTimeoutCallback() {
            @Override
            public void OnTimeoutReturn(PasscodeTimeoutEnum passcodeTimeoutType) {
                selectedPasscodeTimeoutType = passcodeTimeoutType;
            }
        });
        recyclerView.setAdapter(passcodeTimeoutSelectAdapter);
    }

    private void updatePasscodeTimeout(){
        realm.beginTransaction();
        passcode.setTimeOutId(selectedPasscodeTimeoutType.getId());
        passcode.setUpdateDate(new Date());
        passcode.setUpdateUserId(user.getId());
        realm.commitTransaction();

        BaseResponse baseResponse = PasscodeDBHelper.createOrUpdatePasscode(passcode);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            passcodeTimeoutCallback.OnTimeoutReturn(selectedPasscodeTimeoutType);
            mFragmentNavigation.popFragments(1);
        }
    }
}