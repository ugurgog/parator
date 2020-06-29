package com.paypad.vuk507.charge.sale;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.sale.adapters.SaleListAdapter;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.customer.CustomerFragment;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.group.GroupFragment;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.adapters.TaxListAdapter;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModel;
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

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                                SaleModelInstance.setInstance(null);
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
        realm = Realm.getDefaultInstance();
        setToolbarTitle();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        itemRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    private void setPopupMenuItems(PopupMenu popupMenu ){
        if(SaleModelInstance.getInstance().getSaleModel().getSale().getCustomerId() > 0)
            popupMenu.getMenu().findItem(R.id.addCustomer).setVisible(false);
        else
            popupMenu.getMenu().findItem(R.id.removeCustomer).setVisible(false);
    }

    private void setToolbarTitle() {
        double totalAmount = SaleModelInstance.getInstance().getSaleModel().getSale().getDiscountedAmount();
        String amountStr;

        if(totalAmount == 0d)
            amountStr = getResources().getString(R.string.total).concat(": ").concat("0.00").concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        else
            amountStr = getResources().getString(R.string.total).concat(": ").concat(CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol());

        toolbarTitleTv.setText(amountStr);
    }

    public void updateAdapterWithCurrentList(){

        SaleModelInstance.getInstance().getSaleModel().setDiscountedAmountOfSale();
        List<SaleItem> saleItems = new ArrayList<>();
        saleItems.addAll(SaleModelInstance.getInstance().getSaleModel().getSaleItems());

        double totalDiscountAmount = SaleModelInstance.getInstance().getSaleModel().getTotalDiscountAmountOfSale();

        //Indirim tutar toplamnin toplam tutar dan buyuk olmasi durumu
        if(SaleModelInstance.getInstance().getSaleModel().getSale().getDiscountedAmount() == 0)
            totalDiscountAmount = SaleModelInstance.getInstance().getSaleModel().getSale().getTotalAmount();

        if(totalDiscountAmount > 0){
            SaleItem saleItem = new SaleItem();
            saleItem.setName(getResources().getString(R.string.discounts));
            saleItem.setAmount(totalDiscountAmount);
            saleItems.add(saleItem);
        }

        saleListAdapter = new SaleListAdapter(saleItems, new ReturnSaleItemCallback() {
            @Override
            public void onReturn(SaleItem saleItem, ItemProcessEnum processtype) {

                if(saleItem.getUuid() != null && !saleItem.getUuid().isEmpty()){

                    mFragmentNavigation.pushFragment(new SaleItemEditFragment(saleItem, new ReturnSaleItemCallback() {
                        @Override
                        public void onReturn(SaleItem saleItem, ItemProcessEnum processType) {

                            if(processType == ItemProcessEnum.CHANGED){
                                for(SaleItem saleItem1 :  SaleModelInstance.getInstance().getSaleModel().getSaleItems() ){
                                    if(saleItem1.getUuid().equals(saleItem.getUuid())){
                                        saleItem1 = saleItem;
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
    public void OnRemoved(RealmList<Discount> discounts) {
        for(Discount discount : discounts){

            SaleModelInstance.getInstance().getSaleModel().getSale().getDiscounts().remove(discount);

            for(SaleItem saleItem: SaleModelInstance.getInstance().getSaleModel().getSaleItems()){
                for(Discount discount1 : saleItem.getDiscounts()){
                    if(discount.getId() == discount1.getId()){
                        saleItem.getDiscounts().remove(discount);
                        break;
                    }
                }
            }
        }
        updateAdapterWithCurrentList();
        setToolbarTitle();
        saleCalculateCallback.OnDiscountRemoved();
    }
}