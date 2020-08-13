package com.paypad.vuk507.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager1;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.SaleItemDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnOrderItemCallback;
import com.paypad.vuk507.menu.transactions.adapters.RefundItemsAdapter;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
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

    private User user;
    private Transaction mTransaction;
    private double returnAmount;
    private Sale sale;
    private Realm realm;

    private RefundItemsAdapter refundItemsAdapter;
    private ReturnOrderItemCallback returnOrderItemCallback;

    public RefundByItemsFragment(Transaction transaction, double returnAmount) {
        mTransaction = transaction;
        this.returnAmount = returnAmount;
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
        sale = SaleDBHelper.getSaleById(mTransaction.getSaleUuid());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        refundItemsRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        List<SaleItem> saleItems = getSaleItems();
        List<OrderRefundItem> orderRefundItems = new ArrayList<>();

        for(SaleItem saleItem : saleItems)
            orderRefundItems.add(ConversionHelper.convertSaleItemToOrderRefundItem(saleItem));

        refundItemsAdapter = new RefundItemsAdapter(getContext(), orderRefundItems, new ReturnOrderItemCallback() {
            @Override
            public void onReturn(OrderRefundItem orderRefundItem, ItemProcessEnum processType) {
                returnOrderItemCallback.onReturn(orderRefundItem, processType);
            }
        });


        refundItemsRv.setAdapter(refundItemsAdapter);
    }

    private List<SaleItem> getSaleItems() {
        RealmResults<SaleItem> saleItems = SaleItemDBHelper.getSaleItemsBySaleId(mTransaction.getSaleUuid());
        List<SaleItem> saleItemList = new ArrayList(saleItems);
        List<SaleItem> adapterOrderItemList = new ArrayList<>();

        double orderItemsTotalAmount = 0d;

        for(Iterator<SaleItem> it = saleItemList.iterator(); it.hasNext();) {
           SaleItem saleItem = it.next();

           orderItemsTotalAmount = CommonUtils.round(orderItemsTotalAmount + saleItem.getAmount(), 2);

           if(OrderManager1.isSaleItemRefunded(saleItem, mTransaction.getSaleUuid()))
               it.remove();
        }

        for(SaleItem saleItem : saleItemList){

            //double discountedByRateAmount = OrderManager1.getTotalDiscountAmountOfSaleItem(saleItem);
            double discountedByRateAmount = saleItem.getTotalDiscountAmount();

            //double discountedByAmountAmount = getDiscountAmountByAmount(orderItemsTotalAmount, saleItem.getAmount() * saleItem.getQuantity());

            realm.beginTransaction();
            SaleItem tempSaleItem = realm.copyFromRealm(saleItem);

            //tempSaleItem.setAmount(CommonUtils.round((saleItem.getAmount() * saleItem.getQuantity()) - (discountedByRateAmount + discountedByAmountAmount), 2));
            tempSaleItem.setAmount(CommonUtils.round((saleItem.getAmount() * saleItem.getQuantity()) - (discountedByRateAmount), 2));

            realm.commitTransaction();

            adapterOrderItemList.add(tempSaleItem);
        }

        return adapterOrderItemList;
    }

    private double getDiscountAmountByAmount(double orderItemsTotalAmount, double orderItemAmount){
        double discountedByAmount = 0d;
        for(OrderItemDiscount discount : sale.getDiscounts()){
            if(discount.getAmount() > 0d){
                discountedByAmount = CommonUtils.round(discountedByAmount + (
                        (discount.getAmount() / orderItemsTotalAmount) * orderItemAmount), 2);
            }
        }
        return discountedByAmount;
    }
}
