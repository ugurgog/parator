package com.paypad.parator.charge.payment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.payment.utils.SendMail;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.httpprocess.interfaces.OnEventListener;
import com.paypad.parator.login.utils.Validation;
import com.paypad.parator.model.Customer;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.ReceiptHtmlContentHelper;
import com.paypad.parator.utils.ReceiptHtmlContentHelper2;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class SendReceiptEmailFragment extends BaseFragment implements SendMail.MailSendCallback {

    private View mView;

    //Toolbar views
    @BindView(R.id.backImgBtn)
    ImageButton backImgBtn;

    @BindView(R.id.changeAmountTv)
    TextView changeAmountTv;
    @BindView(R.id.paymentInfoTv)
    TextView paymentInfoTv;
    @BindView(R.id.emailEt)
    EditText emailEt;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.textView)
    TextView textView;

    private User user;
    private Transaction mTransaction;
    private Customer mCustomer;
    private ProcessDirectionEnum mProcessDirectionType;
    private int paymentStatus = 0;
    private String email;
    private SendMail.MailSendCallback mailSendCallback;
    private SpannableStringBuilder spannableStringBuilder;

    public SendReceiptEmailFragment(Transaction transaction, Customer customer) {
        mTransaction = transaction;
        mCustomer = customer;
    }

    public void setMailSendCallback(SendMail.MailSendCallback mailSendCallback) {
        this.mailSendCallback = mailSendCallback;
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

        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(
                //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE );

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_send_receipt_email, container, false);
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
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkValidEmail())
                    sendMail();
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mailSendCallback.OnBackPressed();
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void sendMail(){
        String htmlText = ReceiptHtmlContentHelper.getReceiptHtmlContent();

       // Html.fromHtml(new StringBuilder().append(htmlText).toString();


        MailContentSpannableProcess mailContentSpannableProcess = new MailContentSpannableProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) object;
                textView.setText(spannableStringBuilder);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                sendEmailProcess();
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onTaskContinue() {

            }
        }, htmlText);

        mailContentSpannableProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendEmailProcess(){
        SendMail sm = new SendMail(getContext(),
                email,
                getResources().getString(R.string.receipt_from_my_business),
                textView.getText().toString());
        sm.setMailSendCallback(this);
        sm.execute();
    }

    private void initVariables() {
        setChargeAmountTv();
        setPaymentInfoTv();
        fillEmailFromCustomer();
    }

    public class MailContentSpannableProcess extends AsyncTask<Void, Void, SpannableStringBuilder> {

        private OnEventListener<SpannableStringBuilder> mCallBack;
        private String htmlText;
        public Exception mException;

        public MailContentSpannableProcess(OnEventListener callback, String htmlText) {
            this.htmlText = htmlText;
            mCallBack = callback;
        }

        @Override
        protected SpannableStringBuilder doInBackground(Void... voids) {
            Spannable spannable = (new HtmlSpanner()).fromHtml(htmlText);

            //Spanned spannable = Html.fromHtml(new StringBuilder().append(htmlText).toString());

            SpannableStringBuilder spannableStringBuilder = CommonUtils.setTextViewHTML(spannable, getContext());
            return spannableStringBuilder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mCallBack != null) {
                mCallBack.onTaskContinue();
            }
        }

        @Override
        protected void onPostExecute(SpannableStringBuilder spannableStringBuilder) {
            super.onPostExecute(spannableStringBuilder);
            if (mCallBack != null) {
                if (mException == null) {
                    mCallBack.onSuccess(spannableStringBuilder);
                } else {
                    mCallBack.onFailure(mException);
                }
            }
        }
    }

    private boolean checkValidEmail() {

        boolean isValid;

        if(emailEt.getText().toString().isEmpty()) {
            if(fillEmailFromCustomer()){
                isValid = true;
            }else {
                CommonUtils.showToastShort(getContext(), getResources().getString(R.string.email_empty_error));
                isValid = false;
            }
        }else{
            isValid = Validation.getInstance().isValidEmail(getContext(), emailEt.getText().toString());
            if(!isValid){
                CommonUtils.showToastShort(getContext(), Validation.getInstance().getErrorMessage());
            }else
                email = emailEt.getText().toString();
        }

        return isValid;
    }

    private boolean fillEmailFromCustomer(){
        if(mCustomer != null && mCustomer.getEmail() != null && !mCustomer.getEmail().isEmpty()){
            emailEt.setHint(mCustomer.getEmail());
            email = mCustomer.getEmail();
            return true;
        }
        return false;
    }

    private void setChargeAmountTv(){
        if(mTransaction.getChangeAmount() == 0d){
            changeAmountTv.setText(getResources().getString(R.string.no_change));
        }else {
            changeAmountTv.setText(CommonUtils.getDoubleStrValueForView(mTransaction.getChangeAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" ")
                    .concat(getResources().getString(R.string.change)));
        }
    }

    private void setPaymentInfoTv() {

        String infoText = "";
        if(SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount() == 0d){

            if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
                infoText = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(" ödendi.");
            }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
                infoText = "Out of ".concat(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(". ");
            }
            paymentInfoTv.setText(infoText);
        }else {
            if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
                infoText = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(" ödendi. Kalan Tutar ")
                        .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(".");
            }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
                infoText = "Out of ".concat(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(". ")
                        .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(" payment due.");
            }
            paymentInfoTv.setText(infoText);
        }
    }

    @Override
    public void OnMailSendResponse(BaseResponse baseResponse, String email) {
        mailSendCallback.OnMailSendResponse(baseResponse, email);
    }

    @Override
    public void OnBackPressed() {

    }
}