package com.paypad.parator.charge.payment.orderpayment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.payment.MailResultFragment;
import com.paypad.parator.charge.payment.SendReceiptEmailFragment;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.charge.payment.utils.PrintOrderManager;
import com.paypad.parator.charge.payment.utils.SendMail;
import com.paypad.parator.db.GlobalSettingsDBHelper;
import com.paypad.parator.db.SaleDBHelper;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.customer.CustomerFragment;
import com.paypad.parator.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.parator.model.Customer;
import com.paypad.parator.model.GlobalSettings;
import com.paypad.parator.model.Order;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback.STATUS_CONTINUE;
import static com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback.STATUS_NEW_SALE;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class OrderPaymentCompletedFragment extends BaseFragment
        implements SendMail.MailSendCallback, WalkthroughCallback  {

    private View mView;

    //Toolbar views
    @BindView(R.id.continueBtn)
    Button continueBtn;
    @BindView(R.id.addCustomerBtn)
    Button addCustomerBtn;

    @BindView(R.id.changeAmountTv)
    TextView changeAmountTv;
    @BindView(R.id.paymentInfoTv)
    TextView paymentInfoTv;
    @BindView(R.id.btnEmail)
    Button btnEmail;
    @BindView(R.id.btnReceipt)
    Button btnReceipt;
    @BindView(R.id.btnPrintReceipt)
    Button btnPrintReceipt;
    @BindView(R.id.receiptInfoll)
    RelativeLayout receiptInfoll;
    @BindView(R.id.mainll)
    LinearLayout mainll;

    private User user;
    private Transaction mTransaction;
    private Customer mCustomer;
    private ProcessDirectionEnum mProcessDirectionType;
    private int paymentStatus = 0;
    private PaymentStatusCallback paymentStatusCallback;
    private SendReceiptEmailFragment sendReceiptEmailFragment;
    private PrintOrderManager printOrderManager;
    private int receiptType = CUSTOMER_RECEIPT;
    private GlobalSettings globalSettings;
    private Realm realm;
    private Order order;
    private int walkthrough;
    private Context mContext;
    private WalkthroughCallback walkthroughCallback;
    private Tutorial tutorial;

    private static final int CUSTOMER_RECEIPT = 1;
    private static final int MERCHANT_RECEIPT = 2;

    private String printerResult;

    OrderPaymentCompletedFragment(Transaction transaction, ProcessDirectionEnum processDirectionType, int walkthrough) {
        mTransaction = transaction;
        mProcessDirectionType = processDirectionType;
        this.walkthrough = walkthrough;
    }

    public void setPaymentStatusCallback(PaymentStatusCallback paymentStatusCallback) {
        this.paymentStatusCallback = paymentStatusCallback;
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
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
            user = UserDBHelper.getUserFromCache(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        CommonUtils.hideNavigationBar((Activity) mContext);

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_payment_completed, container, false);
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
        btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCounter();
                handleMailResponse(null, "");
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCounter();
                initSendReceiptEmailFragment();
                mFragmentNavigation.pushFragment(sendReceiptEmailFragment);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCounter();
                paymentStatusCallback.OnPaymentReturn(paymentStatus);
            }
        });

        addCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelCounter();

                if(SaleModelInstance.getInstance().getSaleModel().getOrder().getCustomerId() == 0){
                    mFragmentNavigation.pushFragment(new CustomerFragment(OrderPaymentCompletedFragment.class.getName(), new ReturnCustomerCallback() {
                        @Override
                        public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                            addCustomerToSale(customer);
                            startCounter();
                        }
                    }));
                }else {
                    removeCustomerFromSale();
                    startCounter();
                }
            }
        });

        btnPrintReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCounter();
                printReceiptProcess();
            }
        });
    }

    private void initPrinter() {
        printOrderManager = new PrintOrderManager(mContext, SaleModelInstance.getInstance().getSaleModel(),true);
        printOrderManager.setCallback(mCallback);
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();

        order = SaleDBHelper.getSaleById(mTransaction.getOrderId());
        initPrinter();

        if(mProcessDirectionType == ProcessDirectionEnum.PAYMENT_FULLY_COMPLETED){
            paymentStatus = STATUS_NEW_SALE;
            continueBtn.setText(getResources().getString(R.string.new_sale));
            receiptInfoll.setVisibility(View.VISIBLE);
            //startCounter();
            checkAutoPrint();
        }else{
            paymentStatus = STATUS_CONTINUE;
            continueBtn.setText(getResources().getString(R.string.continue_text));
            receiptInfoll.setVisibility(View.GONE);
        }

        btnPrintReceipt.setText(mContext.getResources().getString(R.string.print_receipt));
        setChargeAmountTv();
        setPaymentInfoTv();
        initTutorial();
    }

    private void initTutorial() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);

        if(walkthrough == WALK_THROUGH_CONTINUE) {
            tutorial.setLayoutVisibility(View.VISIBLE);
            tutorial.setTutorialMessage(mContext.getResources().getString(R.string.payment_completed_tutorial_message));
        }
    }

    private void initSendReceiptEmailFragment(){
        sendReceiptEmailFragment = new SendReceiptEmailFragment(mTransaction, mCustomer);
        sendReceiptEmailFragment.setMailSendCallback(this);
    }

    private void checkAutoPrint(){
        globalSettings = GlobalSettingsDBHelper.getPrinterSetting(user.getId());

        if(globalSettings != null){

            if(globalSettings.isCustomerAutoPrint() && receiptType == CUSTOMER_RECEIPT){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        printReceiptProcess();
                    }
                }, 100);
            }else if(globalSettings.isMerchantAutoPrint() && receiptType == MERCHANT_RECEIPT){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        printReceiptProcess();
                    }
                }, 4000);
            }else
                btnPrintReceipt.setEnabled(true);
        }
    }

    private void printReceiptProcess(){
        btnPrintReceipt.setEnabled(false);
        if(receiptType == CUSTOMER_RECEIPT)
            printOrderManager.printCustomerReceipt();
        else
            printOrderManager.printMerchantReceipt();
    }

    private void setChargeAmountTv(){
        if(mTransaction.getChangeAmount() == 0d){
            changeAmountTv.setText(getResources().getString(R.string.no_change));
        }else {
            changeAmountTv.setText(CommonUtils.getDoubleStrValueForView(mTransaction.getChangeAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" ")
                    .concat(mContext.getResources().getString(R.string.change)));
        }
    }

    private void startCounter(){
        myCountDown.start();
    }

    private void cancelCounter(){
        myCountDown.cancel();
    }

    private CountDownTimer myCountDown = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
            Log.i("Info", "::myCountDown millisUntilFinished:" + String.valueOf(millisUntilFinished));
        }

        public void onFinish() {
            paymentStatusCallback.OnPaymentReturn(paymentStatus);
        }
    };

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

    private void addCustomerToSale(Customer customer){
        if(customer != null){
            SaleModelInstance.getInstance().getSaleModel().getOrder().setCustomerId(customer.getId());

            BaseResponse saleBaseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel().getOrder());

            DataUtils.showBaseResponseMessage(mContext,saleBaseResponse);

            if(saleBaseResponse.isSuccess()){
                mCustomer = customer;
                addCustomerBtn.setText(getResources().getString(R.string.remove_customer));

                if(customer.getEmail() != null && !customer.getEmail().isEmpty()){
                    btnEmail.setText(getResources().getString(R.string.email).concat(" (")
                            .concat(customer.getEmail()).concat(")"));
                }
            }
        }
    }

    private void removeCustomerFromSale(){
        SaleModelInstance.getInstance().getSaleModel().getOrder().setCustomerId(0);

        BaseResponse saleBaseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel().getOrder());

        DataUtils.showBaseResponseMessage(mContext,saleBaseResponse);

        if(saleBaseResponse.isSuccess()){
            mCustomer = null;
            addCustomerBtn.setText(getResources().getString(R.string.add_customer));
            btnEmail.setText(getResources().getString(R.string.email));
        }
    }

    @Override
    public void OnMailSendResponse(BaseResponse baseResponse, String email) {
        if(sendReceiptEmailFragment != null)
            Objects.requireNonNull(sendReceiptEmailFragment.getActivity()).onBackPressed();

        handleMailResponse(baseResponse, email);
    }

    @Override
    public void OnBackPressed() {
        startCounter();
    }

    private void handleMailResponse(BaseResponse baseResponse, String email) {

        MailResultFragment mailResultFragment = new MailResultFragment(baseResponse, email);
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.receiptInfoll, mailResultFragment, MailResultFragment.class.getName())
                .addToBackStack(null)
                .commit();

        if(baseResponse != null && baseResponse.isSuccess()){
            mTransaction.setMailSend(true);
            mTransaction.setMailAdress(email);
            mTransaction.setMailSendDate(new Date());
        }else
            mTransaction.setMailSend(false);

        BaseResponse mailSendresponse = TransactionDBHelper.createOrUpdateTransaction(mTransaction);
        DataUtils.showBaseResponseMessage(mContext,mailSendresponse);

        LogUtil.logTransaction("handleMailResponse", mTransaction);

        btnEmail.setVisibility(View.GONE);
        btnReceipt.setVisibility(View.GONE);
        btnPrintReceipt.setVisibility(View.GONE);
        startCounter();
    }


    InnerResultCallbcak mCallback = new InnerResultCallbcak() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {

        }

        @Override
        public void onReturnString(String result) throws RemoteException {
            printerResult = result;
        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {

        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            final int res = code;
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(res == 0){
                        CommonUtils.showToastShort(mContext, "Print successful");

                        if(receiptType == CUSTOMER_RECEIPT){
                            receiptType = MERCHANT_RECEIPT;
                            btnPrintReceipt.setText(mContext.getResources().getString(R.string.print_merchant_receipt));
                            checkAutoPrint();
                        }else
                            startCounter();
                    }else{
                        CommonUtils.showToastShort(mContext, "Print failed");
                        //TODO Follow-up after failed, such as reprint
                    }
                }
            });
        }
    };

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;
        walkthroughCallback.OnWalkthroughResult(result);
        if(result == WALK_THROUGH_END)
            tutorial.setLayoutVisibility(View.GONE);
    }
}