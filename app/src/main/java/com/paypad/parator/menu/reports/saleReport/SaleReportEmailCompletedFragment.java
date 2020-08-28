package com.paypad.parator.menu.reports.saleReport;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.model.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleReportEmailCompletedFragment extends BaseFragment {

    private View mView;

    //Toolbar variables
    @BindView(R.id.resultImgv)
    ImageView resultImgv;
    @BindView(R.id.emailSendSuccessTv)
    TextView emailSendSuccessTv;
    @BindView(R.id.mailTitleTv)
    TextView mailTitleTv;
    @BindView(R.id.failedListTv)
    TextView failedListTv;
    @BindView(R.id.btnDone)
    Button btnDone;

    private User user;
    private String  mailTitle;
    private List<String> emailList;
    private List<String> failedMailList;

    public SaleReportEmailCompletedFragment(String mailTitle, List<String> emailList, List<String> failedMailList) {
        this.mailTitle = mailTitle;
        this.emailList = emailList;
        this.failedMailList = failedMailList;
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
            mView = inflater.inflate(R.layout.fragment_sales_report_email_completed, container, false);
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
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        mailTitleTv.setText(mailTitle);

        if(emailList.size() == failedMailList.size()){
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(R.drawable.ic_warning_white_24dp)
                    .into(resultImgv);
            emailSendSuccessTv.setText(getResources().getString(R.string.email_sent_unsuccessful));
        }

        if(failedMailList.size() > 0){
            failedListTv.setVisibility(View.VISIBLE);

            String failedTvMessage = getResources().getString(R.string.mail_cannot_send_these_users).concat(" : ");

            for(String email : failedMailList){
                failedTvMessage = failedTvMessage.concat(email).concat(", ");
            }
            failedListTv.setText(failedTvMessage);

        }else
            failedListTv.setVisibility(View.GONE);

    }
}