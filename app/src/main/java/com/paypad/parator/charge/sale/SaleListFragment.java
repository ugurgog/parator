package com.paypad.parator.charge.sale;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.parator.charge.interfaces.SaleCalculateCallback;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.charge.sale.adapters.SaleListAdapter;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.customer.CustomerFragment;
import com.paypad.parator.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.parator.model.Customer;
import com.paypad.parator.model.OrderItemDiscount;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class SaleListFragment extends BaseFragment implements SaleDiscountListFragment.RemovedDiscountsCallback {

    private View mView;

    @BindView(R.id.mainll)
    LinearLayout mainll;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.selectionImgv)
    ClickableImageView selectionImgv;
    @BindView(R.id.itemRv)
    RecyclerView itemRv;


    private Realm realm;
    private User user;
    private SaleCalculateCallback saleCalculateCallback;
    private SaleListAdapter saleListAdapter;
    private SaleDiscountListFragment saleDiscountListFragment;
    private IOrderManager orderManager;

    public SaleListFragment() {

    }

    public void setSaleCalculateCallback(SaleCalculateCallback saleCalculateCallback) {
        this.saleCalculateCallback = saleCalculateCallback;
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
            mView = inflater.inflate(R.layout.fragment_sale_list, container, false);
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

        selectionImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), selectionImgv);
                popupMenu.inflate(R.menu.menu_sale_list);

                setPopupMenuItems(popupMenu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeCustomer:
                                saleCalculateCallback.onCustomerRemoved();
                                Objects.requireNonNull(getActivity()).onBackPressed();
                                break;

                            case R.id.addCustomer:

                                mFragmentNavigation.pushFragment(new CustomerFragment(SaleListFragment.class.getName(), new ReturnCustomerCallback() {
                                    @Override
                                    public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                                        Log.i("Info", "Customer will be added here");
                                        saleCalculateCallback.onCustomerAdded(customer);
                                    }
                                }));

                                break;
                            case R.id.clearItems:
                                saleCalculateCallback.onItemsCleared();
                                Objects.requireNonNull(getActivity()).onBackPressed();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void initVariables() {
        orderManager = new OrderManager();
        realm = Realm.getDefaultInstance();
        setToolbarTitle();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        itemRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    private void setPopupMenuItems(PopupMenu popupMenu ){
        if(SaleModelInstance.getInstance().getSaleModel().getOrder().getCustomerId() > 0)
            popupMenu.getMenu().findItem(R.id.addCustomer).setVisible(false);
        else
            popupMenu.getMenu().findItem(R.id.removeCustomer).setVisible(false);
    }

    private void setToolbarTitle() {
        double totalAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount();
        String amountStr;

        if(totalAmount == 0d)
            amountStr = getResources().getString(R.string.total).concat(": ").concat("0.00").concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        else
            amountStr = getResources().getString(R.string.total).concat(": ").concat(CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol());

        toolbarTitleTv.setText(amountStr);
    }

    public void updateAdapterWithCurrentList(){

        orderManager.setDiscountedAmountOfSale();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.addAll(SaleModelInstance.getInstance().getSaleModel().getOrderItems());

        //double totalDiscountAmount = orderManager.getTotalDiscountAmountOfSale();
        double totalDiscountAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getTotalDiscountAmount();

        //Indirim tutar toplamnin toplam tutar dan buyuk olmasi durumu
        if(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount() == 0)
            totalDiscountAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getTotalAmount();

        if(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscounts() != null &&
                SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscounts().size() > 0){
            OrderItem orderItem = new OrderItem();
            orderItem.setName(getResources().getString(R.string.discounts));
            orderItem.setAmount(totalDiscountAmount);
            orderItems.add(orderItem);
        }

        saleListAdapter = new SaleListAdapter(orderItems, new ReturnSaleItemCallback() {
            @Override
            public void onReturn(OrderItem orderItem, ItemProcessEnum processtype) {

                if(orderItem.getId() != null && !orderItem.getId().isEmpty()){

                    mFragmentNavigation.pushFragment(new SaleItemEditFragment(orderItem, new ReturnSaleItemCallback() {
                        @Override
                        public void onReturn(OrderItem orderItem, ItemProcessEnum processType) {

                            if(processType == ItemProcessEnum.CHANGED){
                                for(OrderItem orderItem1 :  SaleModelInstance.getInstance().getSaleModel().getOrderItems() ){
                                    if(orderItem1.getId().equals(orderItem.getId())){
                                        orderItem1 = orderItem;
                                        break;
                                    }
                                }
                                saleCalculateCallback.onSaleItemEditted();
                                updateAdapterWithCurrentList();
                                setToolbarTitle();
                            }else if(processType == ItemProcessEnum.DELETED){
                                saleCalculateCallback.onSaleItemDeleted();
                                updateAdapterWithCurrentList();
                                setToolbarTitle();
                            }
                        }
                    }));

                }else {
                    initSaleDiscountListFragment();
                    mFragmentNavigation.pushFragment(saleDiscountListFragment);
                }

            }
        });
        itemRv.setAdapter(saleListAdapter);
    }

    private void initSaleDiscountListFragment(){
        saleDiscountListFragment = new SaleDiscountListFragment();
        saleDiscountListFragment.setRemovedDiscountsCallback(this);
    }

    @Override
    public void OnRemoved(RealmList<OrderItemDiscount> discounts) {
        for(OrderItemDiscount discount : discounts){

            SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscounts().remove(discount);

            for(OrderItem orderItem : SaleModelInstance.getInstance().getSaleModel().getOrderItems()){

                if(orderItem.getDiscounts() != null){
                    for(Iterator<OrderItemDiscount> it = orderItem.getDiscounts().iterator(); it.hasNext();) {
                        OrderItemDiscount discount1 = it.next();

                        if(discount.getId() == discount1.getId()){
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
        updateAdapterWithCurrentList();
        setToolbarTitle();
        saleCalculateCallback.OnDiscountRemoved();
    }
}