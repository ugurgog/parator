package com.paypad.vuk507.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.ReceiptDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.enums.ReceiptTypeEnum;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.transactions.adapters.NewReceiptAdapter;
import com.paypad.vuk507.menu.transactions.interfaces.ReturnTransactionCallback;
import com.paypad.vuk507.model.Receipt;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import pl.droidsonroids.gif.GifImageView;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class RefundOrCancellationFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.nfcInfoll)
    LinearLayout nfcInfoll;
    @BindView(R.id.referenceNumberEt)
    EditText referenceNumberEt;
    @BindView(R.id.checkImgv)
    ImageView checkImgv;
    @BindView(R.id.mainrl)
    RelativeLayout mainrl;

    private User user;
    private Transaction transaction;
    private double totalAmount;
    private Realm realm;
    private int refundCancelStatus;
    private SendNewReceiptFragment sendNewReceiptFragment;

    public RefundOrCancellationFragment(Transaction transaction, int refundCancelStatus) {
        this.transaction = transaction;
        this.refundCancelStatus = refundCancelStatus;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_refund_payment, container, false);
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

        referenceNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().trim().isEmpty()) {
                    if(transaction.getRetrefNum().equals(editable.toString())){
                        checkImgv.setVisibility(View.VISIBLE);
                        CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
                    }else {
                        checkImgv.setVisibility(View.GONE);
                        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
                    }
                } else {
                    checkImgv.setVisibility(View.GONE);
                    CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRefund();
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        saveBtn.setText(getResources().getString(R.string.continue_text));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        setToolbarTitle(transaction.getTotalAmount());
        nfcInfoll.setVisibility(View.GONE);
        checkImgv.setVisibility(View.GONE);

        //Card gosterildi ve nfc basarili kabul edelim
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nfcInfoll.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }

    private void setToolbarTitle(double amount){
        totalAmount = amount;
        toolbarTitleTv.setText(CommonUtils.getAmountTextWithCurrency(amount).concat(" ")
                .concat(refundCancelStatus == TYPE_REFUND ? getContext().getResources().getString(R.string.refund)
                        : getContext().getResources().getString(R.string.cancel)));
    }

    private void processRefund(){
        //iade islemi baslatilir

        realm.beginTransaction();

        Transaction tempTransaction = realm.copyToRealm(transaction);

        tempTransaction.setTransactionType(refundCancelStatus == TYPE_REFUND ? TransactionTypeEnum.REFUND.getId()
                : TransactionTypeEnum.CANCEL.getId());
        tempTransaction.setRefundOrCancelDate(new Date());

        realm.commitTransaction();

        BaseResponse baseResponse = TransactionDBHelper.createOrUpdateTransaction(tempTransaction);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        LogUtil.logTransaction("processRefund", tempTransaction);

        if(baseResponse.isSuccess()){
            refundCompletedProcess();

        }
    }

    private void refundCompletedProcess(){
        View child = getLayoutInflater().inflate(R.layout.layout_payment_fully_completed, null);

        GifImageView gifImageView = child.findViewById(R.id.gifImageView);

        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                CommonUtils.getScreenWidth(getContext()),
                CommonUtils.getScreenHeight(getContext()) + CommonUtils.getNavigationBarHeight(getContext())
        ));

        mainrl.addView(child);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initSendNewReceiptFragment();
                mFragmentNavigation.pushFragment(sendNewReceiptFragment);

            }
        }, 4000);
    }

    private void initSendNewReceiptFragment(){
        sendNewReceiptFragment = new SendNewReceiptFragment(transaction, refundCancelStatus, totalAmount);
    }
}