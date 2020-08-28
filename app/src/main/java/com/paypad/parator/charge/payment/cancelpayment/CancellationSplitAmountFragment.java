package com.paypad.parator.charge.payment.cancelpayment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.order.CancellationManager;
import com.paypad.parator.charge.payment.CustomSplitFragment;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.CancelPaymentModelInstance;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;
import com.paypad.parator.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;


public class CancellationSplitAmountFragment extends BaseFragment implements NumberFormatWatcher.ReturnEtTextCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.remainInfoTv)
    TextView remainInfoTv;
    @BindView(R.id.amountEt)
    EditText amountEt;
    @BindView(R.id.continueBtn)
    Button continueBtn;
    @BindView(R.id.splitInfoTv)
    TextView splitInfoTv;

    @BindView(R.id.splitTwoBtn)
    Button splitTwoBtn;
    @BindView(R.id.splitThreeBtn)
    Button splitThreeBtn;
    @BindView(R.id.splitFourBtn)
    Button splitFourBtn;
    @BindView(R.id.splitCustomBtn)
    Button splitCustomBtn;

    private User user;
    //private double amount;
    private Transaction mTransaction;
    private CompleteCallback completeCallback;
    private CustomSplitFragment customSplitFragment;
    private NumberFormatWatcher numberFormatWatcher;
    private double totalAmount;

    public CancellationSplitAmountFragment(Transaction transaction,  double totalAmount, CompleteCallback completeCallback) {
        this.mTransaction = transaction;
        this.completeCallback = completeCallback;
        this.totalAmount = totalAmount;
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
            mView = inflater.inflate(R.layout.fragment_split_amount, container, false);
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

        splitTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancellationManager.removeNotPayedSplits(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());
                decideSplitCount(2);
            }
        });

        splitThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancellationManager.removeNotPayedSplits(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());
                decideSplitCount(3);
            }
        });

        splitFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancellationManager.removeNotPayedSplits(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());
                decideSplitCount(4);
            }
        });

        splitCustomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCustomSplitFragment();
                mFragmentNavigation.pushFragment(customSplitFragment);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double firstAmount = DataUtils.getDoubleValueFromFormattedString(amountEt.getText().toString());

                if(firstAmount == mTransaction.getTransactionAmount()){
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }else {
                    CancellationManager.removeNotPayedSplits(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());
                    CancellationManager.addTransactionToOrder(firstAmount,
                            CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions(),
                            CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getOrder().getId());
                    //decideSplitCountByAmount(firstAmount);

                    double secondAmount = mTransaction.getTransactionAmount() - firstAmount;

                    if(secondAmount > 0)
                        CancellationManager.addTransactionToOrder(secondAmount,
                                CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions(),
                                CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getOrder().getId());

                    LogUtil.logTransactions(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());

                    completeCallback.onComplete(null);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
    }

    private void initVariables() {
        initNumberFormatWatcher();
        amountEt.addTextChangedListener(numberFormatWatcher);
        amountEt.setHint("0.00 ".concat(CommonUtils.getCurrency().getSymbol()));
        CommonUtils.setAmountToView(mTransaction.getTransactionAmount(), amountEt, TYPE_PRICE);
        saveBtn.setVisibility(View.GONE);
        String amountStr = CommonUtils.getDoubleStrValueForView(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getRemainAmount(), TYPE_PRICE)
                .concat(" ")
                .concat(CommonUtils.getCurrency().getSymbol());
        toolbarTitleTv.setText(amountStr);
        setSplitInfoTv(mTransaction.getTransactionAmount());
    }

    private void initNumberFormatWatcher(){
        numberFormatWatcher = new NumberFormatWatcher(amountEt, TYPE_PRICE, CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getRemainAmount());
        numberFormatWatcher.setReturnEtTextCallback(this);
    }

    private void setSplitInfoTv(double amount) {
        String infoText = "";

        double remainAmount = CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getRemainAmount() - amount;

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            infoText = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" toplam tutardan, ödeme sonrası kalacak tutar ")
                    .concat(CommonUtils.getDoubleStrValueForView(remainAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(".");
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            infoText = CommonUtils.getDoubleStrValueForView(remainAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" of ")
                    .concat(CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(" will remain after this transaction.");
        }
        splitInfoTv.setText(infoText);
    }

    private void initCustomSplitFragment(){
        customSplitFragment = new CustomSplitFragment(mTransaction.getTransactionAmount(), new CustomSplitFragment.ReturnSplitCountCallback() {
            @Override
            public void OnReturnSplitCount(int splitCount) {
                CancellationManager.removeNotPayedSplits(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());

                if(customSplitFragment != null)
                    Objects.requireNonNull(customSplitFragment.getActivity()).onBackPressed();

                decideSplitCount(splitCount);
            }
        });
    }

    private void decideSplitCount(int splitCount){
        double splitAmount = CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getRemainAmount() / (double) splitCount;

        for(int count = 0; count < splitCount; count ++){
            CancellationManager.addTransactionToOrder(splitAmount,
                    CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions(),
                    CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getOrder().getId());
        }

        LogUtil.logTransactions(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());

        completeCallback.onComplete(null);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void OnReturnEtValue(String text) {
        if(text == null || text.isEmpty()){
            CommonUtils.setSaveBtnEnability(false, continueBtn, getContext());
            setSplitInfoTv(0d);
        } else{
            CommonUtils.setSaveBtnEnability(true, continueBtn, getContext());
            double amount = DataUtils.getDoubleValueFromFormattedString(text);
            setSplitInfoTv(amount);
        }

    }
}