package com.paypad.vuk507.charge.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.MainActivity;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.login.LoginActivity;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;
import com.paypad.vuk507.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class PaymentFragment extends BaseFragment {

    private View mView;

    private User user;
    private Transaction mTransaction;
    private ProgressDialog progressDialog;

    public PaymentFragment(Transaction transaction) {
        mTransaction = transaction;
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
    public void accountHolderUserReceived(UserBus userBus) {
        user = userBus.getUser();
        if (user == null)
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
            mView = inflater.inflate(R.layout.fragment_payment, container, false);
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

    }

    private void initVariables() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait until payment completed...");
        progressDialog.show();

        mTransaction.setPaymentTypeId(PaymentTypeEnum.CASH.getId());
        progressDialog.dismiss();

        thread.start();

        completePayment();
    }

    private void completePayment() {
        final boolean[] saleSaved = {false};

        SaleModelInstance.getInstance().getSaleModel().getSale().setCreateDate(new Date());

        LogUtil.logSale(SaleModelInstance.getInstance().getSaleModel().getSale());

        SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel(), new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {

                if(baseResponse.getMessage() != null && !baseResponse.getMessage().isEmpty())
                    CommonUtils.showToastShort(getContext(), baseResponse.getMessage());

                if(baseResponse.isSuccess()){
                    saleSaved[0] = true;

                } else {
                    progressDialog.dismiss();
                }
            }
        });

        if(saleSaved[0])
            saveTransaction();
    }

    public void saveTransaction(){
        mTransaction.setPaymentCompleted(true);
        mTransaction.setCreateDate(new Date());
        mTransaction.setUserUuid(user.getUuid());
        mTransaction.setTotalAmount(mTransaction.getTransactionAmount() + mTransaction.getTipAmount());

        LogUtil.logTransaction(mTransaction);

        TransactionDBHelper.createOrUpdateTransaction(mTransaction, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {

                if(baseResponse.getMessage() != null && !baseResponse.getMessage().isEmpty())
                    CommonUtils.showToastShort(getContext(), baseResponse.getMessage());

                if(baseResponse.isSuccess()){

                    mFragmentNavigation.pushFragment(new PaymentCompletedFragment(mTransaction));
                    progressDialog.dismiss();
                    //Objects.requireNonNull(getActivity()).onBackPressed();
                }else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
