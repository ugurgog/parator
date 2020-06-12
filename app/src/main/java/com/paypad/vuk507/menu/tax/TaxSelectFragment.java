package com.paypad.vuk507.menu.tax;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.tax.adapters.TaxSelectListAdapter;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class TaxSelectFragment extends BaseFragment {

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
    @BindView(R.id.createTaxBtn)
    Button createTaxBtn;
    @BindView(R.id.taxRv)
    RecyclerView taxRv;

    private TaxSelectListAdapter taxSelectListAdapter;

    private Realm realm;

    private RealmResults<TaxModel> taxModels;
    private List<TaxModel> taxModelList;
    private User user;
    private ReturnTaxCallback returnTaxCallback;

    public TaxSelectFragment(ReturnTaxCallback returnTaxCallback) {
        this.returnTaxCallback = returnTaxCallback;
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
            mView = inflater.inflate(R.layout.fragment_tax, container, false);
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

        createTaxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new TaxEditFragment(null, new ReturnTaxCallback() {
                    @Override
                    public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                        updateAdapterWithCurrentList();
                    }
                }));
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
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.taxes));
        addItemImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        taxRv.setLayoutManager(linearLayoutManager);
        taxRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){

        taxModelList = new ArrayList<>();
        fillItems();

        taxModels = TaxDBHelper.getAllTaxes(user.getUsername());
        taxModelList.addAll( new ArrayList(taxModels));

        taxSelectListAdapter = new TaxSelectListAdapter(getContext(), taxModelList, mFragmentNavigation, new ReturnTaxCallback() {
            @Override
            public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                returnTaxCallback.OnReturn(taxModel, ItemProcessEnum.SELECTED);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        taxRv.setAdapter(taxSelectListAdapter);
    }

    private void fillItems() {
        TaxRateEnum[] values = TaxRateEnum.values();
        for(TaxRateEnum item : values){
            TaxModel taxModel = new TaxModel();
            taxModel.setName(item.getLabel());
            taxModel.setTaxRate(item.getRateValue());
            taxModel.setId(item.getId());
            taxModelList.add(taxModel);
        }
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && taxSelectListAdapter != null) {
            taxSelectListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && taxModelList.size() > 0)
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}