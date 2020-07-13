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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.contact.ContactHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.adapters.TaxSelectListAdapter;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.menu.transactions.adapters.TransactionsListAdapter;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.Contact;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
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

public class TransactionsFragment extends BaseFragment implements TransactionsListAdapter.ReturnSaleModelCallback {

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
    private TransactionsListAdapter transactionsListAdapter;

    public TransactionsFragment() {

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
            mView = inflater.inflate(R.layout.fragment_transactions, container, false);
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

    public static class DateComparator implements Comparator<SaleModel> {
        @Override
        public int compare(SaleModel o1, SaleModel o2) {
            return o1.getSale().getCreateDate().compareTo(o2.getSale().getCreateDate());
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void updateAdapterWithCurrentList(){

        List<SaleModel> saleModels = SaleDBHelper.getSaleModelsByUserId(user.getUuid());
        Collections.sort(saleModels, new DateComparator());

        List<SaleModel> adapterModel = new ArrayList<>();

        Date previousDate = null;

        for(SaleModel saleModel : saleModels){
            if(DataUtils.getDifferenceDays(previousDate, saleModel.getSale().getCreateDate()) != 0){
                SaleModel saleModel1 = new SaleModel();
                saleModel1.setSale(new Sale());
                saleModel1.getSale().setCreateDate(saleModel.getSale().getCreateDate());
                adapterModel.add(saleModel1);
            }

            adapterModel.add(saleModel);
            previousDate = saleModel.getSale().getCreateDate();
        }
        transactionsListAdapter = new TransactionsListAdapter(getContext(), adapterModel);
        transactionsListAdapter.setReturnSaleModelCallback(this);
        transactionsRv.setAdapter(transactionsListAdapter);
    }

    public void updateAdapter(String searchText) {

    }

    @Override
    public void OnReturnSaleModel(SaleModel saleModel) {
        mFragmentNavigation.pushFragment(new TransactionDetailFragment(saleModel));
    }
}