package com.paypad.vuk507.charge.payment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.charge.sale.DynamicAmountFragment;
import com.paypad.vuk507.charge.sale.SaleListFragment;
import com.paypad.vuk507.charge.sale.adapters.SaleItemDiscountListAdapter;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.customer.CustomerFragment;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class PaymentCompletedFragment extends BaseFragment {

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

    private User user;
    private Transaction mTransaction;
    private Customer mCustomer;

    PaymentCompletedFragment(Transaction transaction) {
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

            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SaleModelInstance.getInstance().getSaleModel().getSale().getCustomerId() == 0){
                    mFragmentNavigation.pushFragment(new CustomerFragment(PaymentCompletedFragment.class.getName(), new ReturnCustomerCallback() {
                        @Override
                        public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                            addCustomerToSale(customer);
                        }
                    }));
                }else {
                    removeCustomerFfromSale();
                }
            }
        });
    }

    private void initVariables() {

    }

    private void addCustomerToSale(Customer customer){
        if(customer != null){
            SaleModelInstance.getInstance().getSaleModel().getSale().setCustomerId(customer.getId());

            SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel(), new CompleteCallback() {
                @Override
                public void onComplete(BaseResponse baseResponse) {
                    if(baseResponse.isSuccess()){
                        addCustomerBtn.setText(getResources().getString(R.string.remove_customer));

                        if(customer.getEmail() != null && !customer.getEmail().isEmpty()){
                            btnEmail.setText(getResources().getString(R.string.email).concat(" (")
                                    .concat(customer.getEmail()).concat(")"));
                        }
                    }
                }
            });
        }
    }

    private void removeCustomerFfromSale(){
        SaleModelInstance.getInstance().getSaleModel().getSale().setCustomerId(0);

        SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel(), new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                if(baseResponse.isSuccess()){
                    addCustomerBtn.setText(getResources().getString(R.string.add_customer));
                    btnEmail.setText(getResources().getString(R.string.email));
                }
            }
        });
    }
}