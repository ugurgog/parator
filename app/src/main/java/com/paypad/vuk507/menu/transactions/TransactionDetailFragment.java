package com.paypad.vuk507.menu.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.contact.ContactHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.transactions.adapters.ItemsServicesAdapter;
import com.paypad.vuk507.menu.transactions.adapters.PaymentDetailAdapter;
import com.paypad.vuk507.menu.transactions.adapters.PaymentTotalAdapter;
import com.paypad.vuk507.menu.transactions.adapters.TransactionsListAdapter;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.Contact;
import com.paypad.vuk507.model.pojo.PaymentDetailModel;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class TransactionDetailFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;


    @BindView(R.id.newReceiptBtn)
    Button newReceiptBtn;
    @BindView(R.id.issueRefundBtn)
    Button issueRefundBtn;
    @BindView(R.id.paymentsRv)
    RecyclerView paymentsRv;
    @BindView(R.id.saleItemsRv)
    RecyclerView saleItemsRv;
    @BindView(R.id.totalRv)
    RecyclerView totalRv;


    private User user;

    private PaymentDetailAdapter paymentDetailAdapter;
    private ItemsServicesAdapter itemsServicesAdapter;
    private PaymentTotalAdapter paymentTotalAdapter;

    private SaleModel saleModel;
    private OrderManager orderManager;

    public TransactionDetailFragment(SaleModel saleModel) {
        this.saleModel = saleModel;
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
            mView = inflater.inflate(R.layout.fragment_transaction_detail, container, false);
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
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        newReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new NewReceiptFragment(saleModel.getTransactions()));
            }
        });
    }

    private void initVariables() {
        orderManager = new OrderManager();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.transactions));

        setPaymentDetailAdapter();
        setItemsServicesAdapter();
        setPaymentTotalAdapter();
    }

    public static class TrxSeqNumComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return (int) (o1.getSeqNumber() - o2.getSeqNumber());
        }
    }

    public void setPaymentDetailAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        List<Transaction> trxlist = realm.copyFromRealm(saleModel.getTransactions());

        Collections.sort(trxlist, new TrxSeqNumComparator());

        paymentDetailAdapter = new PaymentDetailAdapter(getContext(), trxlist);
        paymentsRv.setAdapter(paymentDetailAdapter);
    }

    private void setItemsServicesAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        saleItemsRv.setLayoutManager(linearLayoutManager);

        itemsServicesAdapter = new ItemsServicesAdapter(getContext(), saleModel.getSaleItems());
        saleItemsRv.setAdapter(itemsServicesAdapter);
    }

    private void setPaymentTotalAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        totalRv.setLayoutManager(linearLayoutManager);

        PaymentTotalManager paymentTotalManager = new PaymentTotalManager(getContext(), saleModel);
        List<PaymentDetailModel> paymentDetailModels = paymentTotalManager.getPaymentDetails();

        paymentTotalAdapter = new PaymentTotalAdapter(getContext(), paymentDetailModels);
        totalRv.setAdapter(paymentTotalAdapter);
    }

    public void updateAdapter(String searchText) {
       /* if (searchText != null && taxSelectListAdapter != null) {
            taxSelectListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (taxModelList != null && taxModelList.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }*/
    }
}