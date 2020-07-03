package com.paypad.vuk507.menu.discount;


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
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.discount.adapters.DiscountListAdapter;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class DiscountFragment extends BaseFragment {

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
    @BindView(R.id.createDiscountBtn)
    Button createDiscountBtn;

    @BindView(R.id.discountRv)
    RecyclerView discountRv;

    private User user;
    private Realm realm;
    private RealmResults<Discount> discounts;
    private List<Discount> discountList;
    private DiscountListAdapter discountListAdapter;
    private ReturnDiscountCallback discountCallback;

    public DiscountFragment() {

    }

    public void setDiscountCallback(ReturnDiscountCallback discountCallback) {
        this.discountCallback = discountCallback;
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
            mView = inflater.inflate(R.layout.fragment_discount, container, false);
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

        createDiscountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscountEditFragment(null);
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
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.discounts));
        addItemImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        discountRv.setLayoutManager(linearLayoutManager);
        discountRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        discounts = DiscountDBHelper.getAllDiscounts(user.getUsername());
        discountList = new ArrayList(discounts);
        discountListAdapter = new DiscountListAdapter(getContext(), discountList, mFragmentNavigation, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                //updateAdapterWithCurrentList();
                startDiscountEditFragment(discount);
            }
        }, null);
        discountRv.setAdapter(discountListAdapter);
    }

    private void startDiscountEditFragment(Discount discount){
        mFragmentNavigation.pushFragment(new DiscountEditFragment(discount, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                discountCallback.OnReturn(discount, processType);
                updateAdapterWithCurrentList();
            }
        }));
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && discountListAdapter != null) {
            discountListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && discountList.size() > 0)
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}