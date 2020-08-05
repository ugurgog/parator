package com.paypad.vuk507.menu.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.PrinterSettingsDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.PrinterSettings;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class PrintOrdersFragment extends BaseFragment {

    private View mView;

    //Toolbar views
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.autoPrintCustomerSwitch)
    Switch autoPrintCustomerSwitch;
    @BindView(R.id.autoPrintMerchantSwitch)
    Switch autoPrintMerchantSwitch;

    private User user;
    private Realm realm;
    private PrinterSettings printerSettings;

    public PrintOrdersFragment() {
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
            mView = inflater.inflate(R.layout.fragment_print_orders, container, false);
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
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        autoPrintCustomerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updatePrinterSettings(b, null);
            }
        });

        autoPrintMerchantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updatePrinterSettings(null, b);
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.print_orders));
        printerSettings = PrinterSettingsDBHelper.getPrinterSetting(user.getUuid());

        if(printerSettings == null){
            autoPrintCustomerSwitch.setChecked(false);
            autoPrintMerchantSwitch.setChecked(false);
            printerSettings = new PrinterSettings();
        } else {
            autoPrintCustomerSwitch.setChecked(printerSettings.isCustomerAutoPrint());
            autoPrintMerchantSwitch.setChecked(printerSettings.isMerchantAutoPrint());
        }
    }

    private void updatePrinterSettings(Boolean isCustomerPrint, Boolean isMerchantPrint){

        realm.beginTransaction();

        if(printerSettings.getUserId() == null || printerSettings.getUserId().isEmpty()){
            printerSettings.setCreateDate(new Date());
            printerSettings.setUpdateDate(new Date());
            printerSettings.setUserId(user.getUuid());
            printerSettings.setCreateUserId(user.getUuid());
            printerSettings.setUpdateUserId(user.getUuid());
        }else {
            printerSettings.setUpdateDate(new Date());
            printerSettings.setUpdateUserId(user.getUuid());
        }

        PrinterSettings tempPrinterSettings = realm.copyToRealm(printerSettings);

        if(isCustomerPrint != null)
            tempPrinterSettings.setCustomerAutoPrint(isCustomerPrint);

        if(isMerchantPrint != null)
            tempPrinterSettings.setMerchantAutoPrint(isMerchantPrint);

        realm.commitTransaction();

        BaseResponse baseResponse = PrinterSettingsDBHelper.updatePrinterSettings(tempPrinterSettings);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);
    }
}