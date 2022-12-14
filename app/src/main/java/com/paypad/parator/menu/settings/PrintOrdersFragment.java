package com.paypad.parator.menu.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

//import com.bitvale.switcher.SwitcherX;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.GlobalSettingsDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.model.GlobalSettings;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.DataUtils;

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
    LabeledSwitch autoPrintCustomerSwitch;
    @BindView(R.id.autoPrintMerchantSwitch)
    LabeledSwitch autoPrintMerchantSwitch;

    private User user;
    private Realm realm;
    private GlobalSettings globalSettings;

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

        autoPrintCustomerSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                updatePrinterSettings(isOn, null);
            }
        });

        autoPrintMerchantSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                updatePrinterSettings(null, isOn);
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.print_orders));
        globalSettings = GlobalSettingsDBHelper.getPrinterSetting(user.getId());

        if(globalSettings == null){
            autoPrintCustomerSwitch.setOn(false);
            autoPrintMerchantSwitch.setOn(false);
            globalSettings = new GlobalSettings();
        } else {
            autoPrintCustomerSwitch.setOn(globalSettings.isCustomerAutoPrint());
            autoPrintMerchantSwitch.setOn(globalSettings.isMerchantAutoPrint());
        }
    }

    private void updatePrinterSettings(Boolean isCustomerPrint, Boolean isMerchantPrint){

        realm.beginTransaction();

        if(globalSettings.getUserId() == null || globalSettings.getUserId().isEmpty()){
            globalSettings.setCreateDate(new Date());
            globalSettings.setUpdateDate(new Date());
            globalSettings.setUserId(user.getId());
            globalSettings.setCreateUserId(user.getId());
            globalSettings.setUpdateUserId(user.getId());
        }else {
            globalSettings.setUpdateDate(new Date());
            globalSettings.setUpdateUserId(user.getId());
        }

        GlobalSettings tempGlobalSettings = realm.copyToRealm(globalSettings);

        if(isCustomerPrint != null)
            tempGlobalSettings.setCustomerAutoPrint(isCustomerPrint);

        if(isMerchantPrint != null)
            tempGlobalSettings.setMerchantAutoPrint(isMerchantPrint);

        realm.commitTransaction();

        BaseResponse baseResponse = GlobalSettingsDBHelper.updatePrinterSettings(tempGlobalSettings);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);
    }
}