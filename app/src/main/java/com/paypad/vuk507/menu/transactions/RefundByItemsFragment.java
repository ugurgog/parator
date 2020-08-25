package com.paypad.vuk507.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.OrderRefundItemDBHelper;
import com.paypad.vuk507.db.SaleItemDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnOrderItemCallback;
import com.paypad.vuk507.menu.transactions.adapters.NotRefundedItemsAdapter;
import com.paypad.vuk507.menu.transactions.adapters.RefundedItemsAdapter;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ConversionHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class RefundByItemsFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.refundItemsRv)
    RecyclerView refundItemsRv;

    @BindView(R.id.refundedLL)
    LinearLayout refundedLL;
    @BindView(R.id.refundedItemsRv)
    RecyclerView refundedItemsRv;

    private User user;
    private Realm realm;
    private SaleModel saleModel;

    private NotRefundedItemsAdapter notRefundedItemsAdapter;
    private RefundedItemsAdapter refundedItemsAdapter;
    private ReturnOrderItemCallback returnOrderItemCallback;

    private List<OrderRefundItem> notRefundedItems;
    private List<OrderRefundItem> refundedItems;

    public RefundByItemsFragment(SaleModel saleModel) {
        this.saleModel = saleModel;
    }

    public void setReturnOrderItemCallback(ReturnOrderItemCallback returnOrderItemCallback) {
        this.returnOrderItemCallback = returnOrderItemCallback;
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
            mView = inflater.inflate(R.layout.fragment_refund_by_items, container, false);
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
        realm = Realm.getDefaultInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        refundItemsRv.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        refundedItemsRv.setLayoutManager(linearLayoutManager1);

        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        setRefundedItems();

        notRefundedItemsAdapter = new NotRefundedItemsAdapter(getContext(), notRefundedItems, new ReturnOrderItemCallback() {
            @Override
            public void onReturn(OrderRefundItem orderRefundItem, ItemProcessEnum processType) {
                returnOrderItemCallback.onReturn(orderRefundItem, processType);
            }
        });

        refundItemsRv.setAdapter(notRefundedItemsAdapter);

        if(refundedItems == null || refundedItems.size() == 0){
            refundedLL.setVisibility(View.GONE);
            return;
        }

        refundedItemsAdapter = new RefundedItemsAdapter(getContext(), refundedItems);
        refundedItemsRv.setAdapter(refundedItemsAdapter);
    }

    private void setRefundedItems() {
        RealmResults<OrderItem> orderItems = SaleItemDBHelper.getSaleItemsBySaleId(saleModel.getOrder().getId());
        List<OrderItem> orderItemList = new ArrayList(orderItems);

        notRefundedItems = new ArrayList<>();
        refundedItems = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {

            double discountedByRateAmount = orderItem.getTotalDiscountAmount();

            realm.beginTransaction();
            OrderItem tempOrderItem = realm.copyFromRealm(orderItem);

            tempOrderItem.setAmount(CommonUtils.round((orderItem.getAmount() * orderItem.getQuantity()) - (discountedByRateAmount), 2));

            realm.commitTransaction();

            for (int i = 1; i <= orderItem.getQuantity(); i++) {
                OrderRefundItem orderRefundItem = ConversionHelper.convertSaleItemToOrderRefundItem(orderItem);
                orderRefundItem.setAmount(CommonUtils.round(tempOrderItem.getAmount() / orderItem.getQuantity(), 2));
                notRefundedItems.add(ConversionHelper.convertSaleItemToOrderRefundItem(tempOrderItem));
            }
        }

        RealmResults<OrderRefundItem> refundedOrderItems = OrderRefundItemDBHelper.getRefundItemsByOrderId(saleModel.getOrder().getId());
        refundedItems = new ArrayList(refundedOrderItems);

        for(Iterator<OrderRefundItem> it = refundedOrderItems.iterator(); it.hasNext();) {
            OrderRefundItem orderRefundItem = it.next();

            for(Iterator<OrderRefundItem> its = notRefundedItems.iterator(); its.hasNext();) {
                OrderRefundItem orderRefundItem1 = its.next();

                if(orderRefundItem.getOrderItemId().equals(orderRefundItem1.getOrderItemId())){
                    its.remove();
                    break;
                }
            }
        }
    }

    private double getDiscountAmountByAmount(double orderItemsTotalAmount, double orderItemAmount){
        double discountedByAmount = 0d;
        for(OrderItemDiscount discount : saleModel.getOrder().getDiscounts()){
            if(discount.getAmount() > 0d){
                discountedByAmount = CommonUtils.round(discountedByAmount + (
                        (discount.getAmount() / orderItemsTotalAmount) * orderItemAmount), 2);
            }
        }
        return discountedByAmount;
    }
}
