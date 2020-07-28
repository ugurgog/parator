package com.paypad.vuk507.charge.payment;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.charge.payment.utils.PrintReceiptManager;
import com.paypad.vuk507.charge.payment.utils.SendMail;
import com.paypad.vuk507.charge.sale.DynamicAmountFragment;
import com.paypad.vuk507.charge.sale.SaleListFragment;
import com.paypad.vuk507.db.PrinterSettingsDBHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.customer.CustomerFragment;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.PrinterSettings;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

import static com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback.STATUS_CONTINUE;
import static com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback.STATUS_NEW_SALE;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class PaymentCompletedFragment extends BaseFragment implements SendMail.MailSendCallback  {

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
    private PrintReceiptManager printReceiptManager;
    private int receiptType = CUSTOMER_RECEIPT;
    private PrinterSettings printerSettings;

    private static final int CUSTOMER_RECEIPT = 1;
    private static final int MERCHANT_RECEIPT = 2;

    PaymentCompletedFragment(Transaction transaction, ProcessDirectionEnum processDirectionType) {
        mTransaction = transaction;
        mProcessDirectionType = processDirectionType;
    }

    public void setPaymentStatusCallback(PaymentStatusCallback paymentStatusCallback) {
        this.paymentStatusCallback = paymentStatusCallback;
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

        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );

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
                //Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        addCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelCounter();

                if(SaleModelInstance.getInstance().getSaleModel().getSale().getCustomerId() == 0){
                    mFragmentNavigation.pushFragment(new CustomerFragment(PaymentCompletedFragment.class.getName(), new ReturnCustomerCallback() {
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
        printReceiptManager = new PrintReceiptManager(getContext(), SaleModelInstance.getInstance().getSaleModel(),true);
        printReceiptManager.setCallback(mCallback);
    }

    private void initVariables() {

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

        btnPrintReceipt.setText(getContext().getResources().getString(R.string.print_receipt));
        setChargeAmountTv();
        setPaymentInfoTv();
    }

    private void initSendReceiptEmailFragment(){
        sendReceiptEmailFragment = new SendReceiptEmailFragment(mTransaction, mCustomer);
        sendReceiptEmailFragment.setMailSendCallback(this);
    }

    private void checkAutoPrint(){
        initPrinter();
        printerSettings = PrinterSettingsDBHelper.getPrinterSetting(user.getUuid());

        if(printerSettings != null && printerSettings.isOrdersAutoPrint()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    printReceiptProcess();

                }
            }, 3000);
        }
    }

    private void printReceiptProcess(){
        if(receiptType == CUSTOMER_RECEIPT)
            printReceiptManager.printCustomerReceipt();
        else
            printReceiptManager.printMerchantReceipt();
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
        if(SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount() == 0d){

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
                        .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(".");
            }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
                infoText = "Out of ".concat(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(". ")
                        .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                        .concat(" payment due.");
            }
            paymentInfoTv.setText(infoText);
        }
    }

    private void addCustomerToSale(Customer customer){
        if(customer != null){
            SaleModelInstance.getInstance().getSaleModel().getSale().setCustomerId(customer.getId());

            BaseResponse saleBaseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel());

            DataUtils.showBaseResponseMessage(getContext(),saleBaseResponse);

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
        SaleModelInstance.getInstance().getSaleModel().getSale().setCustomerId(0);

        BaseResponse saleBaseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel());

        DataUtils.showBaseResponseMessage(getContext(),saleBaseResponse);

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
        DataUtils.showBaseResponseMessage(getContext(),mailSendresponse);

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
                        //TODO Follow-up after successful

                        if(receiptType == CUSTOMER_RECEIPT){
                            receiptType = MERCHANT_RECEIPT;
                            btnPrintReceipt.setText(getContext().getResources().getString(R.string.print_merchant_receipt));
                        }else
                            startCounter();
                    }else{
                        CommonUtils.showToastShort(getContext(), "Print failed");
                        //TODO Follow-up after failed, such as reprint
                    }
                }
            });
        }
    };
}