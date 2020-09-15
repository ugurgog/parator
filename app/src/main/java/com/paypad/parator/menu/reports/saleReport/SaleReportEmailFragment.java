package com.paypad.parator.menu.reports.saleReport;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.payment.utils.SendMail;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.login.utils.Validation;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.ReportModel;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleReportEmailFragment extends BaseFragment implements SendMail.MailSendCallback {

    private View mView;

    //Toolbar variables
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.emailEt)
    EditText emailEt;
    @BindView(R.id.includeItemsSwitch)
    LabeledSwitch includeItemsSwitch;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.saleReportEmailRl)
    RelativeLayout saleReportEmailRl;

    private User user;
    private List<String> emailList;
    private List<String> emailFailedList;
    private boolean isIncludeItems = true;

    private String mailTitle;
    private ReportModel reportModel;

    public SaleReportEmailFragment(String mailTitle, ReportModel reportModel) {
        this.mailTitle = mailTitle;
        this.reportModel = reportModel;
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
            mView = inflater.inflate(R.layout.fragment_sales_report_email, container, false);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailList();
            }
        });

        includeItemsSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                isIncludeItems = isOn;
            }
        });
    }

    private void initVariables() {
        includeItemsSwitch.setOn(true);
        emailList = new ArrayList<>();
        emailFailedList = new ArrayList<>();
        saveBtn.setVisibility(View.GONE);
        toolbarTitleTv.setText(getResources().getString(R.string.email_report));
    }

    private void checkEmailList(){
        String emails = emailEt.getText().toString();

        if(emails.isEmpty()){
            CommonUtils.showCustomToast(getContext(), getResources().getString(R.string.email_empty_error), ToastEnum.TOAST_WARNING);
            return;
        }

        String[] emailArrayList = emails.split(",");

        for(String email : emailArrayList){

            boolean isValid = Validation.getInstance().isValidEmail(getContext(), email.trim());

            if(!isValid){
                CommonUtils.showCustomToast(getContext(), Validation.getInstance().getErrorMessage() + " : " + email, ToastEnum.TOAST_WARNING);
                return;
            }
            emailList.add(email);
        }

        for(String email : emailList){
            String messageTitle = getResources().getString(R.string.app_name)
                    .concat(" ")
                    .concat(getResources().getString(R.string.sales_report))
                    .concat(" : ")
                    .concat(mailTitle);

            SendMail sm = new SendMail(getContext(), email, messageTitle,
                    "Bu mesajda satis detaylari yer alacak!!");
            sm.setMailSendCallback(this);
            sm.execute();
        }
    }

    @Override
    public void OnMailSendResponse(BaseResponse baseResponse, String email) {

        if(!baseResponse.isSuccess())
            emailFailedList.add(email);

        if(email.equals(emailList.get(emailList.size() - 1))){
            SaleReportEmailCompletedFragment saleReportEmailCompletedFragment = new SaleReportEmailCompletedFragment(mailTitle, emailList, emailFailedList);

            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.saleReportEmailRl, saleReportEmailCompletedFragment, SaleReportEmailCompletedFragment.class.getName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void OnBackPressed() {

    }
}