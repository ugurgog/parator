package com.paypad.vuk507.charge.sale;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.charge.sale.adapters.SaleDiscountListAdapter;
import com.paypad.vuk507.charge.sale.adapters.SaleListAdapter;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
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
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SaleDiscountListFragment extends BaseFragment implements ReturnDiscountCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.itemRv)
    RecyclerView itemRv;

    private User user;
    private SaleDiscountListAdapter saleDiscountListAdapter;
    private RealmList<Discount> removedDiscounts;
    private RemovedDiscountsCallback removedDiscountsCallback;

    public interface RemovedDiscountsCallback{
        void OnRemoved(RealmList<Discount> discounts);
    }

    public void setRemovedDiscountsCallback(RemovedDiscountsCallback removedDiscountsCallback) {
        this.removedDiscountsCallback = removedDiscountsCallback;
    }

    public SaleDiscountListFragment() {

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
            mView = inflater.inflate(R.layout.fragment_sale_discount_list, container, false);
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removedDiscountsCallback.OnRemoved(removedDiscounts);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.discounts));
        removedDiscounts = new RealmList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        itemRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        List<Discount> discountList = SaleModelInstance.getInstance().getSaleModel().getSale().getDiscounts();

        saleDiscountListAdapter = new SaleDiscountListAdapter(getContext(), discountList);
        saleDiscountListAdapter.setDiscountCallback(this);
        itemRv.setAdapter(saleDiscountListAdapter);
    }

    @Override
    public void OnReturn(Discount discount, ItemProcessEnum processType) {
        removedDiscounts.add(discount);
    }
}