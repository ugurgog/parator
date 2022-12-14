package com.paypad.parator.charge.sale;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.sale.adapters.SaleDiscountListAdapter;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ReturnOrderItemDiscountCallback;
import com.paypad.parator.model.OrderItemDiscount;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class SaleDiscountListFragment extends BaseFragment implements ReturnOrderItemDiscountCallback {

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
    private RealmList<OrderItemDiscount> removedDiscounts;
    private RemovedDiscountsCallback removedDiscountsCallback;

    public interface RemovedDiscountsCallback{
        void OnRemoved(RealmList<OrderItemDiscount> discounts);
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
        List<OrderItemDiscount> discountList = SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscounts();

        saleDiscountListAdapter = new SaleDiscountListAdapter(getContext(), discountList);
        saleDiscountListAdapter.setReturnOrderItemDiscountCallback(this);
        itemRv.setAdapter(saleDiscountListAdapter);
    }

    @Override
    public void OnReturn(OrderItemDiscount discount, ItemProcessEnum processType) {
        removedDiscounts.add(discount);
    }
}