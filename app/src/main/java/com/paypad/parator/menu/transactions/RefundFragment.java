package com.paypad.parator.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.adapter.CustomViewPagerAdapter;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ReturnAmountCallback;
import com.paypad.parator.interfaces.ReturnOrderItemCallback;
import com.paypad.parator.model.OrderRefundItem;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.SaleModel;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class RefundFragment extends BaseFragment implements ReturnOrderItemCallback, ReturnAmountCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    //@BindView(R.id.refundDescTv)
    //TextView refundDescTv;

    private User user;
    private double totalAmount;
    private Realm realm;
    private int refundCancelStatus;
    private CustomViewPagerAdapter customViewPagerAdapter;
    private double returnAmount;
    private double returnAmountFromFrag;
    private String refundReasonText;
    private SaleModel saleModel;
    private double availableRefundAmount = 0d;

    private RefundByItemsFragment refundByItemsFragment;
    private RefundByAmountFragment refundByAmountFragment;

    private int selectedTab = SELECTED_BY_ITEMS;

    private static final int SELECTED_BY_ITEMS = 0;
    private static final int SELECTED_BY_AMOUNT = 1;

    private List<OrderRefundItem> orderRefundItems;

    public RefundFragment(SaleModel saleModel, int refundCancelStatus) {
        this.refundCancelStatus = refundCancelStatus;
        this.saleModel = saleModel;
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
            mView = inflater.inflate(R.layout.fragment_refund_payment, container, false);
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
                if(selectedTab == SELECTED_BY_ITEMS){

                    double refundAmount = 0d;
                    for(OrderRefundItem orderRefundItem : orderRefundItems)
                        refundAmount = CommonUtils.round(refundAmount + orderRefundItem.getAmount(), 2);

                    /*if(refundAmount > returnAmount){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.refund_amount_cannot_be_bigger_trx_amount));
                        return;
                    }*/

                    mFragmentNavigation.pushFragment(new SelectPaymentForRefundFragment(refundCancelStatus, saleModel, false, refundAmount, orderRefundItems));

                    //mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(null, refundAmount,
                    //        TYPE_REFUND, false, orderRefundItems, refundReasonText));

                }else if(selectedTab == SELECTED_BY_AMOUNT){

                    /*if(returnAmountFromFrag > returnAmount){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.refund_amount_cannot_be_bigger_trx_amount));
                        return;
                    }*/

                    mFragmentNavigation.pushFragment(new SelectPaymentForRefundFragment(refundCancelStatus, saleModel, true, returnAmountFromFrag, null));

                    //mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(null, returnAmountFromFrag,
                    //        TYPE_REFUND, true, null, refundReasonText));
                }
            }
        });
    }

    private void initVariables() {
        orderRefundItems = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        saveBtn.setText(getResources().getString(R.string.continue_text));
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.refund));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        availableRefundAmount = OrderManager.getAvailableRefundAmount(saleModel);

        setupViewPager();
        tablayout.setupWithViewPager(viewPager);
        setTabListener();
    }

    private void setupViewPager() {
        customViewPagerAdapter = new CustomViewPagerAdapter(getChildFragmentManager());

        initRefundByItemsFragment();
        initRefundByAmountFragment();

        customViewPagerAdapter.addFrag(refundByItemsFragment, getContext().getResources().getString(R.string.items));
        customViewPagerAdapter.addFrag(refundByAmountFragment, getContext().getResources().getString(R.string.amount));

        viewPager.setAdapter(customViewPagerAdapter);
    }

    private void initRefundByItemsFragment(){
        refundByItemsFragment = new RefundByItemsFragment(saleModel);
        refundByItemsFragment.setReturnOrderItemCallback(this);
    }

    private void initRefundByAmountFragment(){
        refundByAmountFragment = new RefundByAmountFragment(saleModel, false, availableRefundAmount);
        refundByAmountFragment.setReturnAmountCallback(this);
    }

    private void setTabListener() {

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                checkButtonEnability();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setToolbarTitle(double amount){
        totalAmount = amount;
        toolbarTitleTv.setText(CommonUtils.getAmountTextWithCurrency(amount).concat(" ").concat(getContext().getResources().getString(R.string.order)));
    }

    @Override
    public void OnReturnAmount(double amount) {
        returnAmountFromFrag = amount;
        if (returnAmountFromFrag > 0d && returnAmountFromFrag <= availableRefundAmount)
            CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
        else
            CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
    }

    private void checkButtonEnability(){
        if(selectedTab == SELECTED_BY_ITEMS){
            if(orderRefundItems.size() > 0)
                CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
            else
                CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        }else if(selectedTab == SELECTED_BY_AMOUNT){
            if (returnAmountFromFrag > 0d && returnAmountFromFrag <= availableRefundAmount)
                CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
            else
                CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        }
    }


    @Override
    public void onReturn(OrderRefundItem orderRefundItem, ItemProcessEnum processType) {
        if(processType == ItemProcessEnum.SELECTED)
            orderRefundItems.add(orderRefundItem);
        else if(processType == ItemProcessEnum.UNSELECTED)
            orderRefundItems.remove(orderRefundItem);

        if(orderRefundItems.size() > 0)
            CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
        else
            CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
    }
}