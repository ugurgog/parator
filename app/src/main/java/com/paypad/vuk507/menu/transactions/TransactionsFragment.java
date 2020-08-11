package com.paypad.vuk507.menu.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.paypad.vuk507.db.RefundDBHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.transactions.adapters.SaleModelListAdapter;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsFragment extends BaseFragment implements SaleModelListAdapter.ReturnSaleModelCallback {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.addItemImgv)
    ImageView addItemImgv;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;


    @BindView(R.id.transactionsRv)
    RecyclerView transactionsRv;

    private User user;
    private SaleModelListAdapter saleModelListAdapter;

    public TransactionsFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
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
            mView = inflater.inflate(R.layout.fragment_transactions, container, false);
            ButterKnife.bind(this, mView);
            //initVariables();
            //initListeners();
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

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    updateAdapter(s.toString());
                    searchCancelImgv.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    searchCancelImgv.setVisibility(View.GONE);
                }
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
                CommonUtils.showKeyboard(getContext(),false, searchEdittext);
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.transactions));
        addItemImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        transactionsRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    public static class DateComparator implements Comparator<TransactionItem> {
        @Override
        public int compare(TransactionItem o1, TransactionItem o2) {
            return o2.getTrxDate().compareTo(o1.getTrxDate());
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void updateAdapterWithCurrentList(){

        List<SaleModel> saleModels = SaleDBHelper.getSaleModelsForTransactionList(user.getUuid());

        List<TransactionItem> transactionItems = new ArrayList<>();

        for(SaleModel saleModel : saleModels){

            List<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(saleModel.getSale().getSaleUuid(), true);

            //Iade modelleri eklenir
            for(Refund refund : refunds){
                TransactionItem transactionItem = new TransactionItem();
                transactionItem.setTrxDate(refund.getCreateDate());
                transactionItem.setTransaction(null);
                transactionItem.setSaleModel(saleModel);
                transactionItem.setRefund(refund);
                transactionItems.add(transactionItem);
            }

            for(Transaction transaction : saleModel.getTransactions()){

                LogUtil.logTransaction("updateAdapterWithCurrentList", transaction);

                //Cancel edilen islemler varsa eklenir
                if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                    TransactionItem transactionItem = new TransactionItem();
                    transactionItem.setTrxDate(transaction.getCancellationDate());
                    transactionItem.setTransaction(transaction);
                    transactionItem.setSaleModel(saleModel);
                    transactionItem.setRefund(null);
                    transactionItems.add(transactionItem);
                }
            }

            //Sale modeller eklenir
            TransactionItem transactionItem1 = new TransactionItem();
            transactionItem1.setTrxDate(saleModel.getSale().getCreateDate());
            transactionItem1.setTransaction(null);
            transactionItem1.setSaleModel(saleModel);
            transactionItem1.setRefund(null);
            transactionItems.add(transactionItem1);
        }

        Collections.sort(transactionItems, new DateComparator());

        List<TransactionItem> adapterModel = new ArrayList<>();

        Date previousDate = null;

        for(TransactionItem transactionItem : transactionItems){

            //Her gune ozel basliklar eklenir
            if(DataUtils.getDifferenceDays(previousDate, transactionItem.getTrxDate()) != 0){
                TransactionItem transactionItem1 = new TransactionItem();
                transactionItem1.setTrxDate(transactionItem.getTrxDate());
                transactionItem1.setTransaction(null);
                transactionItem1.setSaleModel(null);
                transactionItem1.setNoItem(true);
                transactionItem1.setRefund(null);
                adapterModel.add(transactionItem1);
            }

            adapterModel.add(transactionItem);
            previousDate = transactionItem.getTrxDate();
        }
        saleModelListAdapter = new SaleModelListAdapter(getContext(), adapterModel);
        saleModelListAdapter.setReturnSaleModelCallback(this);
        transactionsRv.setAdapter(saleModelListAdapter);
    }

    public void updateAdapter(String searchText) {

    }

    @Override
    public void OnReturnSaleModel(SaleModel saleModel) {
        mFragmentNavigation.pushFragment(new TransactionDetailFragment(saleModel));
    }

    public class TransactionItem{
        private SaleModel saleModel;
        private Transaction transaction;
        private Refund refund;
        private Date trxDate;
        private boolean noItem;

        public SaleModel getSaleModel() {
            return saleModel;
        }

        public void setSaleModel(SaleModel saleModel) {
            this.saleModel = saleModel;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        public Date getTrxDate() {
            return trxDate;
        }

        public void setTrxDate(Date trxDate) {
            this.trxDate = trxDate;
        }

        public boolean isNoItem() {
            return noItem;
        }

        public void setNoItem(boolean noItem) {
            this.noItem = noItem;
        }

        public Refund getRefund() {
            return refund;
        }

        public void setRefund(Refund refund) {
            this.refund = refund;
        }
    }
}