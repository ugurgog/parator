package com.paypad.vuk507.menu.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.transactions.adapters.SaleModelListAdapter;
import com.paypad.vuk507.menu.transactions.interfaces.TransactionItemCallback;
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
import io.realm.Realm;
import io.realm.RealmResults;

public class TransactionsFragment extends BaseFragment implements TransactionItemCallback {

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
    private List<TransactionItem> adapterModel;
    private Realm realm;

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

        if (searchEdittext.getText() != null && !searchEdittext.getText().toString().isEmpty()) {
            updateAdapter(searchEdittext.getText().toString());
            searchCancelImgv.setVisibility(View.VISIBLE);
        } else {
            updateAdapter("");
            searchCancelImgv.setVisibility(View.GONE);
        }
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
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.transactions));
        addItemImgv.setVisibility(View.GONE);
        searchEdittext.setHint(getContext().getResources().getString(R.string.search_by_order_zno_fno));
        adapterModel = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        transactionsRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    @Override
    public void onReturnTransactionItem(TransactionItem transactionItem) {
        mFragmentNavigation.pushFragment(new TransactionDetailFragment(transactionItem));
    }


    public static class DateComparator implements Comparator<TransactionItem> {
        @Override
        public int compare(TransactionItem o1, TransactionItem o2) {
            return o2.getTrxDate().compareTo(o1.getTrxDate());
        }
    }

    public static class RefundGroupIdComparator implements Comparator<Refund> {
        @Override
        public int compare(Refund o1, Refund o2) {
            return o2.getRefundGroupId().compareTo(o1.getRefundGroupId());
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void updateAdapterWithCurrentList(){

        List<SaleModel> saleModels = SaleDBHelper.getSaleModelsForTransactionList(user.getId());

        List<TransactionItem> transactionItems = new ArrayList<>();

        for(SaleModel saleModel : saleModels){

            RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(saleModel.getOrder().getId(), true);
            List<Refund> refundList = new ArrayList(refunds);

            Collections.sort(refundList, new RefundGroupIdComparator());

            String previousRefGroupId = "";

            for(Refund refund : refundList){

                LogUtil.logRefund(refund);

                if(!refund.getRefundGroupId().equals(previousRefGroupId)){
                    RealmResults<Refund> refundsOfGroup = RefundDBHelper.getAllRefundsByRefundGroupId(refund.getRefundGroupId());

                    if(refundsOfGroup != null && refundsOfGroup.size() > 0){
                        TransactionItem transactionItem = new TransactionItem();
                        transactionItem.setTrxDate(refundsOfGroup.get(0).getCreateDate());
                        transactionItem.setTransaction(null);
                        transactionItem.setSaleModel(saleModel);

                        List<Refund> refunds1 = new ArrayList<>();
                        for(Refund refund1 : refundsOfGroup)
                            refunds1.add(refund1);
                        transactionItem.setRefunds(refunds1);

                        transactionItems.add(transactionItem);
                    }
                }

                previousRefGroupId = refund.getRefundGroupId();
            }


            //Iade modelleri eklenir
            /*for(Refund refund : refunds){
                TransactionItem transactionItem = new TransactionItem();
                transactionItem.setTrxDate(refund.getCreateDate());
                transactionItem.setTransaction(null);
                transactionItem.setSaleModel(saleModel);
                transactionItem.setRefund(refund);
                transactionItems.add(transactionItem);

                previousRefGroupId = refund.getRefundGroupId();
            }*/

            for(Transaction transaction : saleModel.getTransactions()){

                LogUtil.logTransaction("updateAdapterWithCurrentList", transaction);

                //Cancel edilen islemler varsa eklenir
                if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                    TransactionItem transactionItem = new TransactionItem();
                    transactionItem.setTrxDate(transaction.getCancellationDate());
                    transactionItem.setTransaction(transaction);
                    transactionItem.setSaleModel(saleModel);
                    transactionItem.setRefunds(null);
                    transactionItems.add(transactionItem);
                }
            }

            //Order modeller eklenir
            TransactionItem transactionItem1 = new TransactionItem();
            transactionItem1.setTrxDate(saleModel.getOrder().getCreateDate());
            transactionItem1.setTransaction(null);
            transactionItem1.setSaleModel(saleModel);
            transactionItem1.setRefunds(null);
            transactionItems.add(transactionItem1);
        }

        Collections.sort(transactionItems, new DateComparator());

        Date previousDate = null;

        for(TransactionItem transactionItem : transactionItems){

            //Her gune ozel basliklar eklenir
            if(DataUtils.getDifferenceDays(previousDate, transactionItem.getTrxDate()) != 0){
                TransactionItem transactionItem1 = new TransactionItem();
                transactionItem1.setTrxDate(transactionItem.getTrxDate());
                transactionItem1.setTransaction(null);
                transactionItem1.setSaleModel(null);
                transactionItem1.setNoItem(true);
                transactionItem1.setRefunds(null);
                adapterModel.add(transactionItem1);
            }

            adapterModel.add(transactionItem);
            previousDate = transactionItem.getTrxDate();
        }
        saleModelListAdapter = new SaleModelListAdapter(getContext(), adapterModel);
        saleModelListAdapter.setTransactionItemCallback(this);
        transactionsRv.setAdapter(saleModelListAdapter);
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && saleModelListAdapter != null) {

            Log.i("Info", "searchText:" + searchText);

            saleModelListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (adapterModel != null && adapterModel.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }

    public class TransactionItem{
        private SaleModel saleModel;
        private Transaction transaction;
        private List<Refund> refunds;
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

        public List<Refund> getRefunds() {
            return refunds;
        }

        public void setRefunds(List<Refund> refunds) {
            this.refunds = refunds;
        }
    }
}