package com.paypad.vuk507.charge.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.model.pojo.BaseResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MailResultFragment extends BaseFragment{

    private View mView;

    @BindView(R.id.resultImgv)
    ImageView resultImgv;
    @BindView(R.id.mailSendResultTv)
    TextView mailSendResultTv;
    @BindView(R.id.sendEmailTv)
    TextView sendEmailTv;

    private BaseResponse baseResponse;
    private String email;

    public MailResultFragment(BaseResponse baseResponse, String email) {
        this.baseResponse = baseResponse;
        this.email = email;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.layout_mail_send_result, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {

        if(baseResponse != null){
            if(baseResponse.isSuccess()){
                sendEmailTv.setText(email);
            }
        }else{
            mailSendResultTv.setText(getResources().getString(R.string.thanks));
            sendEmailTv.setText("");
        }


    }

}