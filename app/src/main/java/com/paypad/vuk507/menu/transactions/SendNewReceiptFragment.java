package com.paypad.vuk507.menu.transactions;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
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

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class SendNewReceiptFragment extends BaseFragment implements SendMail.MailSendCallback {

    private View mView;

    //Toolbar views
    @BindView(R.id.backImgBtn)
    ImageButton backImgBtn;

    @BindView(R.id.emailEt)
    EditText emailEt;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.mainRl)
    RelativeLayout mainRl;
    @BindView(R.id.printReceiptImgv)
    ClickableImageView printReceiptImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;

    private User user;
    private Transaction mTransaction;
    private String email;
    private Integer refundCancelStatus = null;
    private double refundAmount;

    private PrintReceiptManager printReceiptManager;

    SendNewReceiptFragment(Transaction transaction, Integer refundCancelStatus, double refundAmount) {
        mTransaction = transaction;
        this.refundCancelStatus = refundCancelStatus;
        this.refundAmount = refundAmount;
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
            mView = inflater.inflate(R.layout.fragment_send_new_receipt, container, false);
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
                CommonUtils.showKeyboard(getContext(), false, emailEt);
                checkValidEmail();
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refundCancelStatus != null)
                    mFragmentNavigation.popFragments(3);
                else
                    Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        printReceiptImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refundCancelStatus != null && (refundCancelStatus == TYPE_REFUND || refundCancelStatus == TYPE_CANCEL))
                    printReceiptManager.printRefundOrCancelReceipt();
                else
                    printReceiptManager.printCustomerReceipt();
            }
        });
    }

    private void initPrinter() {
        SaleModel saleModel = SaleDBHelper.getSaleModelBySaleId(mTransaction.getSaleUuid());

        if(refundCancelStatus != null)
            printReceiptManager = new PrintReceiptManager(getContext(), mTransaction,false,  refundAmount, refundCancelStatus);
        else
            printReceiptManager = new PrintReceiptManager(getContext(), saleModel,false);

        printReceiptManager.setCallback(mCallback);
    }

    private void sendMail(){
        SendMail sm = new SendMail(getContext(),
                email,
                getResources().getString(R.string.receipt_from_my_business),
                "transaction detaylari yer alacak bu mesajda...");
        sm.setMailSendCallback(this);
        sm.execute();
    }

    private void initVariables() {
        if(refundCancelStatus != null)
            printReceiptImgv.setVisibility(View.VISIBLE);

        setToolbarTitle();
        initPrinter();
    }

    private void setToolbarTitle(){
        if(refundCancelStatus == null)
            toolbarTitleTv.setText(getResources().getString(R.string.new_receipt));
        else {
            if(refundCancelStatus == TYPE_REFUND)
                toolbarTitleTv.setText(getResources().getString(R.string.refund_receipt));
            else if(refundCancelStatus == TYPE_CANCEL)
                toolbarTitleTv.setText(getResources().getString(R.string.cancel_receipt));
        }
    }

    private void checkValidEmail() {

        boolean isValid = Validation.getInstance().isValidEmail(getContext(), emailEt.getText().toString());

        if(!isValid){
            CommonUtils.showToastShort(getContext(), Validation.getInstance().getErrorMessage());
            return;
        }
        email = emailEt.getText().toString();
        sendMail();
    }

    @Override
    public void OnMailSendResponse(BaseResponse baseResponse, String email) {
        handleMailResponse(baseResponse, email);
    }

    private void handleMailResponse(BaseResponse baseResponse, String email) {

        MailResultFragment mailResultFragment = new MailResultFragment(baseResponse, email);
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainRl, mailResultFragment, MailResultFragment.class.getName())
                .addToBackStack(null)
                .commit();

        if(baseResponse != null && baseResponse.isSuccess()){
            mTransaction.setMailSend(true);
            mTransaction.setMailAdress(email);
            mTransaction.setMailSendDate(new Date());
        }else
            mTransaction.setMailSend(false);

        BaseResponse mailSendResponse = TransactionDBHelper.createOrUpdateTransaction(mTransaction);
        DataUtils.showBaseResponseMessage(getContext(), mailSendResponse);

        LogUtil.logTransaction("handleMailResponse", mTransaction);
    }

    @Override
    public void OnBackPressed() {

    }

    InnerResultCallbcak mCallback = new InnerResultCallbcak() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {

        }

        @Override
        public void onReturnString(String result) throws RemoteException {

        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {

        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            final int res = code;
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(res == 0){
                        CommonUtils.showToastShort(getContext(), "Print successful");
                    }else{
                        CommonUtils.showToastShort(getContext(), "Print failed");
                    }
                }
            });
        }
    };
}