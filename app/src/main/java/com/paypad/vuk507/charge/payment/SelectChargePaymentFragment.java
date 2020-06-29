package com.paypad.vuk507.charge.payment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.charge.sale.DynamicAmountFragment;
import com.paypad.vuk507.charge.sale.adapters.SaleItemDiscountListAdapter;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Split;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class SelectChargePaymentFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.chargeAmountTv)
    TextView chargeAmountTv;

    @BindView(R.id.paymentsRv)
    RecyclerView paymentsRv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.splitBtn)
    Button splitBtn;
    @BindView(R.id.splitInfoTv)
    TextView splitInfoTv;

    private User user;
    private DynamicPaymentSelectAdapter dynamicPaymentSelectAdapter;
    //private double splitAmount = 0d;
    private Transaction mTransaction;

    public SelectChargePaymentFragment() {

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
            mView = inflater.inflate(R.layout.fragment_select_charge_payment, container, false);
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

        splitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentNavigation.pushFragment(new SplitAmountFragment(mTransaction.getTransactionAmount(), new CompleteCallback() {
                    @Override
                    public void onComplete(BaseResponse baseResponse) {

                        for(Transaction transaction : SaleModelInstance.getInstance().getSaleModel().getTransactions()){

                            if(!transaction.isPaymentCompleted()){
                                mTransaction = transaction;
                                //splitAmount = transaction.getTransactionAmount();
                                setChargeAmount();
                                setSplitInfoTv(transaction.getSeqNumber());

                                break;
                            }
                        }
                    }
                }));
            }
        });
    }

    private void initVariables() {
        createInitialTransaction();
        //splitAmount = SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount();
        setChargeAmount();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);
        setPaymentAdapter();
    }

    private void createInitialTransaction() {
        mTransaction = new Transaction();
        mTransaction.setSaleUuid(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid());
        mTransaction.setTransactionUuid(UUID.randomUUID().toString());
        mTransaction.setSeqNumber(SaleModelInstance.getInstance().getSaleModel().getMaxSplitId() + 1);
        mTransaction.setTransactionAmount(SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount());
        SaleModelInstance.getInstance().getSaleModel().getTransactions().add(mTransaction);
    }

    private void setPaymentAdapter(){
        PaymentTypeEnum[] paymentTypeEnums = PaymentTypeEnum.values();
        List<PaymentTypeEnum> paymentTypes = new ArrayList<>(Arrays.asList(paymentTypeEnums));

        dynamicPaymentSelectAdapter = new DynamicPaymentSelectAdapter(getContext(), ProcessDirectionEnum.DIRECTION_PAYMENT_SELECT, paymentTypes, new ReturnPaymentCallback() {
            @Override
            public void onReturn(PaymentTypeEnum paymentType) {
                if(paymentType.getId() == PaymentTypeEnum.CASH.getId()){
                    mFragmentNavigation.pushFragment(new CashSelectFragment(mTransaction));
                }
            }
        });
        paymentsRv.setAdapter(dynamicPaymentSelectAdapter);
    }

    private void setChargeAmount(){
        String amountStr = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        chargeAmountTv.setText(amountStr);
    }

    private void setSplitInfoTv(long id){
        splitInfoTv.setVisibility(View.VISIBLE);

        String infoText = "";
        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            infoText = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" Toplam, Ödeme ")
                    .concat(String.valueOf(id))
                    .concat("/")
                    .concat(String.valueOf(SaleModelInstance.getInstance().getSaleModel().getTransactions().size()));
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            infoText = "Out of "
                    .concat(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(" Total, Payment ")
                    .concat(String.valueOf(id))
                    .concat(" of ")
                    .concat(String.valueOf(SaleModelInstance.getInstance().getSaleModel().getTransactions().size()));
        }
        splitInfoTv.setText(infoText);
    }

}