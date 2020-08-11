package com.paypad.vuk507.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.adapter.CustomViewPagerAdapter;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnAmountCallback;
import com.paypad.vuk507.interfaces.ReturnOrderItemCallback;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.Transaction;
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

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

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
    @BindView(R.id.refundDescTv)
    TextView refundDescTv;

    private User user;
    private Transaction transaction;
    private double totalAmount;
    private Realm realm;
    private int refundCancelStatus;
    private CustomViewPagerAdapter customViewPagerAdapter;
    private double returnAmount;
    private double returnAmountFromFrag;

    private RefundByItemsFragment refundByItemsFragment;
    private RefundByAmountFragment refundByAmountFragment;

    private int selectedTab = SELECTED_BY_ITEMS;

    private static final int SELECTED_BY_ITEMS = 0;
    private static final int SELECTED_BY_AMOUNT = 1;

    private List<OrderRefundItem> orderRefundItems;

    public RefundFragment(Transaction transaction, int refundCancelStatus, double returnAmount) {
        this.transaction = transaction;
        this.refundCancelStatus = refundCancelStatus;
        this.returnAmount = returnAmount;
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

                    if(refundAmount > returnAmount){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.refund_amount_cannot_be_bigger_trx_amount));
                        return;
                    }

                    mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(transaction, refundAmount,
                            TYPE_REFUND, false, orderRefundItems, "XXXXX"));

                }else if(selectedTab == SELECTED_BY_AMOUNT){

                    if(returnAmountFromFrag > returnAmount){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.refund_amount_cannot_be_bigger_trx_amount));
                        return;
                    }

                    mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(transaction, returnAmountFromFrag,
                            TYPE_REFUND, true, null, "XXXXX"));
                }
            }
        });
    }

    private void initVariables() {
        orderRefundItems = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        saveBtn.setText(getResources().getString(R.string.continue_text));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        setToolbarTitle(transaction.getTotalAmount());
        setRefundDescTv();

        setupViewPager();
        tablayout.setupWithViewPager(viewPager);
        setTabListener();
    }

    private void setRefundDescTv() {
        String refundDesc;
        if (CommonUtils.getLanguage().equals(LANGUAGE_TR))
            refundDesc = "Yapılabilecek maximum iade tutarı ".concat(CommonUtils.getAmountTextWithCurrency(returnAmount));
        else
            refundDesc = "Max ".concat(CommonUtils.getAmountTextWithCurrency(returnAmount))
                    .concat(" available for refund");
        refundDescTv.setText(refundDesc);
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
        refundByItemsFragment = new RefundByItemsFragment(transaction, returnAmount);
        refundByItemsFragment.setReturnOrderItemCallback(this);
    }

    private void initRefundByAmountFragment(){
        refundByAmountFragment = new RefundByAmountFragment(transaction, false, returnAmount);
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
        toolbarTitleTv.setText(CommonUtils.getAmountTextWithCurrency(amount).concat(" ").concat(getContext().getResources().getString(R.string.sale)));
    }

    @Override
    public void OnReturnAmount(double amount) {
        returnAmountFromFrag = amount;
        if (returnAmountFromFrag > 0d && returnAmountFromFrag <= returnAmount)
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
            if (returnAmountFromFrag > 0d && returnAmountFromFrag <= returnAmount)
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