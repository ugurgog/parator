package com.paypad.vuk507.charge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.adapter.ChargeViewPagerAdapter;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.payment.SelectChargePaymentFragment;
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.charge.sale.SaleListFragment;
import com.paypad.vuk507.charge.utils.AnimationUtil;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.ReturnViewCallback;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.menu.customer.CustomerFragment;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.item.ItemListFragment;
import com.paypad.vuk507.menu.transactions.TransactionsFragment;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class ChargeFragment extends BaseFragment implements
        SaleCalculateCallback,
        PaymentStatusCallback,
        ItemListFragment.DiscountUpdateCallback,
        ItemListFragment.ProductUpdateCallback,
        ItemListFragment.CategoryUpdateCallback,
        ReturnViewCallback {

    View mView;

    @BindView(R.id.tablayout)
    TabLayout tablayout;

    @BindView(R.id.htab_viewpager)
    ViewPager htab_viewpager;
    @BindView(R.id.settingsImgv) 
    ImageView settingsImgv;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navViewLayout)
    NavigationView navViewLayout;

    @BindView(R.id.chargell)
    LinearLayout chargell;
    @BindView(R.id.chargeAmountTv)
    TextView chargeAmountTv;
    @BindView(R.id.mainll)
    LinearLayout mainll;
    @BindView(R.id.mainFl)
    FrameLayout mainFl;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saleCountTv)
    TextView saleCountTv;
    @BindView(R.id.toolbarll)
    LinearLayout toolbarll;
    @BindView(R.id.saleCountRL)
    RelativeLayout saleCountRL;

    private static final int TAB_KEYPAD = 0;
    private static final int TAB_LIBRARY = 1;

    static final int CUSTOM_ITEM_ADD_FROM_KEYPAD = 0;
    private static final int CUSTOM_ITEM_ADD_FROM_SALELIST = 1;
    private static final int CUSTOM_ITEM_ADD_FROM_CHARGE = 2;

    private int selectedTabPosition = TAB_KEYPAD;
    private boolean mDrawerState;
    private int currentSaleCount = 0;

    private ChargeViewPagerAdapter chargeViewPagerAdapter;
    private User user;
    private KeypadFragment keypadFragment;
    private LibraryFragment libraryFragment;
    private double totalAmount;
    private String saleNote;
    private boolean isCustomerExist= false;
    private String customerName = "";
    private SelectChargePaymentFragment selectChargePaymentFragment;

    private SaleItem saleItem = null;
    //private OrderItemTax orderItemTax = null;
    private TaxModel mTaxModel = null;
    private IOrderManager orderManager;

    private TextView animationTextView;

    //private List<Discount> discountList;

    private int[] tabIcons = {
            R.drawable.ic_keyboard_black_24dp,
            R.drawable.ic_library_books_black_24dp};

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(keypadFragment != null){
            keypadFragment.setDynamicBoxAdapter();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE );

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_charge, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
            setUpPager();
            setDrawerListeners();
        }
        return mView;
    }

    private void initVariables() {
        orderManager = new OrderManager();
        chargeAmountTv.setHint("0,00".concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        chargell.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 0));
        SaleModelInstance.reset();
    }

    private void initListeners() {
        toolbarll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(saleItem != null ){
                    if(mTaxModel == null){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_select_tax_rate));
                        return ;
                    }else {
                        if(!orderManager.isSaleItemInSale(saleItem)){
                            OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_SALELIST);
                        }
                    }
                }
                startSaleListFragment();
            }
        });

        settingsImgv.setOnClickListener(v -> {
            settingsImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            if (mDrawerState) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        chargell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saleItem != null ){

                    if(mTaxModel == null){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_select_tax_rate));
                        return ;
                    }else {
                        if(!orderManager.isSaleItemInSale(saleItem)){
                            OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_CHARGE);
                        }

                        /*if(!SaleModelInstance.getInstance().getSaleModel().isSaleItemInSale(saleItem)){
                            OnCustomItemAdd();
                        }*/
                    }
                }

                orderManager.setRemainAmountByDiscountedAmount();
                initSelectChargePaymentFragment();
                mFragmentNavigation.pushFragment(selectChargePaymentFragment);
            }
        });
    }

    private void initSelectChargePaymentFragment(){
        selectChargePaymentFragment = new SelectChargePaymentFragment();
        selectChargePaymentFragment.setPaymentStatusCallback(this);
        selectChargePaymentFragment.setSaleCalculateCallback(this);
    }

    private void startSaleListFragment() {
        SaleListFragment saleListFragment = new SaleListFragment();
        saleListFragment.setSaleCalculateCallback(this);
        mFragmentNavigation.pushFragment(saleListFragment);
    }

    private void setUpPager() {
        setupViewPager();
        tablayout.setupWithViewPager(htab_viewpager);
        setupTabIcons();
        setTabListener();
    }

    private void setupTabIcons() {
        Objects.requireNonNull(tablayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tablayout.getTabAt(1)).setIcon(tabIcons[1]);

        Objects.requireNonNull(Objects.requireNonNull(tablayout.getTabAt(1)).getIcon())
                .setColorFilter(Objects.requireNonNull(getContext()).getResources().getColor(R.color.tab_unselected, null), PorterDuff.Mode.SRC_IN);
    }

    private void setupViewPager() {
        keypadFragment = new KeypadFragment();
        keypadFragment.setSaleCalculateCallback(this);
        keypadFragment.setPaymentStatusCallback(this);
        keypadFragment.setReturnViewCallback(this);

        libraryFragment = new LibraryFragment();
        libraryFragment.setSaleCalculateCallback(this);
        libraryFragment.setReturnViewCallback(this);

        chargeViewPagerAdapter = new ChargeViewPagerAdapter(getFragmentManager());
        chargeViewPagerAdapter.addFrag(keypadFragment, getContext().getResources().getString(R.string.keypad));
        chargeViewPagerAdapter.addFrag(libraryFragment, getContext().getResources().getString(R.string.library));
        htab_viewpager.setAdapter(chargeViewPagerAdapter);
    }

    private void setTabListener() {

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon())
                        .setColorFilter(Objects.requireNonNull(getContext()).getResources().getColor(R.color.tab_selected, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon())
                        .setColorFilter(Objects.requireNonNull(getContext()).getResources().getColor(R.color.tab_unselected, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setDrawerListeners() {
        drawerLayout.addDrawerListener(new ActionBarDrawerToggle(getActivity(),
                drawerLayout,
                null,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerState = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerState = true;
            }
        });

        navViewLayout.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.checkout:
                    drawerLayout.closeDrawer(Gravity.LEFT);

                    boolean commit = LoginUtils.deleteSharedPreferences(getContext());

                    if(commit){
                        UserDBHelper.updateUserLoggedInStatus(user.getUsername(), false, new CompleteCallback() {
                            @Override
                            public void onComplete(BaseResponse baseResponse) {
                                if(baseResponse.isSuccess()){
                                    Objects.requireNonNull(getActivity()).finish();
                                    startActivity(new Intent(getActivity(), InitialActivity.class));
                                }else
                                    CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                            }
                        });
                    }else
                        CommonUtils.showToastShort(getContext(), "cache delete error!");
                    break;

                case R.id.transactions:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startTransactionsFragment();
                    break;

                case R.id.reports:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startReportsFragment();
                    break;

                case R.id.customers:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startCustomersFragment();
                    break;

                case R.id.items:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startItemsFragment();
                    break;

                case R.id.settings:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startSettingsFragment();
                    break;

                case R.id.support:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startSupportFragment();
                    break;

                case R.id.notifications:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startNotificationsFragment();
                    break;


                default:
                    break;
            }

            return false;
        });

    }

    private void startNotificationsFragment() {
    }

    private void startSupportFragment() {
    }

    private void startSettingsFragment() {
    }

    private void startItemsFragment() {
        ItemListFragment itemListFragment = new ItemListFragment();
        itemListFragment.setDiscountUpdateCallback(this);
        itemListFragment.setProductUpdateCallback(this);
        itemListFragment.setCategoryUpdateCallback(this);
        mFragmentNavigation.pushFragment(itemListFragment);
    }

    private void startCustomersFragment() {
        mFragmentNavigation.pushFragment(new CustomerFragment(ChargeFragment.class.getName(), new ReturnCustomerCallback() {
            @Override
            public void OnReturn(Customer customer, ItemProcessEnum processEnum) {

            }
        }));
    }

    private void startReportsFragment() {
    }

    private void startTransactionsFragment() {
        mFragmentNavigation.pushFragment(new TransactionsFragment());
    }

    @Override
    public void onProductSelected(Product product, double amount, boolean isDynamicAmount) {
        orderManager.addProductToOrder(product, amount, isDynamicAmount);
        //SaleModelInstance.getInstance().getSaleModel().addProduct(product, amount, isDynamicAmount);
        addUserUUIDToSale();

        setChargeAmountTv();
        totalAmount = 0d;
        onNewSaleCreated();
    }

    @Override
    public void onKeyPadClicked(int number) {

    }

    @Override
    public void onDiscountSelected(Discount discount) {
        if(discount.getRate() > 0){
            orderManager.addDiscountRateToAllSaleItems(discount);
            //SaleModelInstance.getInstance().getSaleModel().addDiscountRateToAllSaleItems(discount);
        }else if(discount.getAmount() > 0){
            orderManager.addDiscountToOrder(discount);
            //SaleModelInstance.getInstance().getSaleModel().addDiscountAmount(discount);
        }
        addUserUUIDToSale();

        setChargeAmountTv();
        //totalAmount = 0d;

        updateLibraryDiscountAdapter();
        updateDynamicBoxAdapter();
        //keypadFragment.clearAmountFields();
    }

    @Override
    public void OnTaxSelected(TaxModel taxModel) {
        if(saleItem != null){
            mTaxModel = taxModel;

            totalAmount = orderManager.getDiscountedAmountByAddingCustomItem(saleItem);
            OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_KEYPAD);

        }else {
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_select_custom_amount_first));
        }
    }

    @Override
    public void onItemsCleared() {
        SaleModelInstance.setInstance(null);
        currentSaleCount = 0;
        isCustomerExist = false;
        setCurrentSaleCount();
        totalAmount = 0;
        saleNote = "";
        setChargeAmountTv();
        updateLibraryDiscountAdapter();
        updateDynamicBoxAdapter();
        mTaxModel = null;
        saleItem = null;
    }

    @Override
    public void onNewSaleCreated() {
        currentSaleCount ++;
        setCurrentSaleCount();
    }

    @Override
    public void OnCustomItemAdd(int addFromValue) {

        if(saleItem == null){
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_type_custom_amount));
            return;
        }

        if(mTaxModel == null){
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_select_tax_rate));
            return;
        }

        if(saleNote != null)
            saleItem.setNote(saleNote);

        orderManager.addCustomItemToOrder(saleItem, mTaxModel);
        //SaleModelInstance.getInstance().getSaleModel().addCustomItem(saleItem, mTaxModel);

        currentSaleCount = orderManager.getOrderItemCount();
        //currentSaleCount = SaleModelInstance.getInstance().getSaleModel().getSaleCount();
        addUserUUIDToSale();

        setCurrentSaleCount();
        setChargeAmountTv();

        totalAmount = 0d;
        saleNote = "";
        saleItem = null;
        mTaxModel = null;
        keypadFragment.clearAmountFields();

        if(addFromValue == CUSTOM_ITEM_ADD_FROM_KEYPAD)
            startAnimation(keypadFragment.getSaleAmountTv());
    }

    @Override
    public void onCustomAmountReturn(double amount) {

        saleItem = new SaleItem();
        saleItem.setName(getResources().getString(R.string.custom_amount));
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setDynamicAmount(true);
        saleItem.setNote(saleNote != null ? saleNote : "");
        saleItem.setSaleUuid(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid());

        totalAmount = orderManager.getDiscountedAmountByAddingCustomItem(saleItem);

        String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());

        if(totalAmount <= 0d)
            chargeAmountTv.setText("");
        else
            chargeAmountTv.setText(amountStr);
    }

    @Override
    public void onRemoveCustomAmount(double amount) {
        totalAmount = totalAmount - amount;
        String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());

        if(totalAmount <= 0d)
            chargeAmountTv.setText("");
        else
            chargeAmountTv.setText(amountStr);

        currentSaleCount --;
        setCurrentSaleCount();
    }

    @Override
    public void onSaleNoteReturn(String note) {
        saleNote = note;
    }

    @Override
    public void onCustomerAdded(Customer customer) {
        isCustomerExist = true;
        customerName = customer.getFullName();
        SaleModelInstance.getInstance().getSaleModel().getSale().setCustomerId(customer.getId());
        toolbarTitleTv.setText(customerName);
    }

    @Override
    public void onCustomerRemoved() {
        isCustomerExist = false;
        SaleModelInstance.getInstance().getSaleModel().getSale().setCustomerId(0);
        toolbarTitleTv.setText(getResources().getString(R.string.current_sale));
    }

    @Override
    public void onSaleItemEditted() {
        //currentSaleCount = SaleModelInstance.getInstance().getSaleModel().getSaleCount();
        currentSaleCount = orderManager.getOrderItemCount();
        setCurrentSaleCount();
        setChargeAmountTv();
    }

    @Override
    public void onSaleItemDeleted() {

        //currentSaleCount = SaleModelInstance.getInstance().getSaleModel().getSaleCount();
        currentSaleCount = orderManager.getOrderItemCount();
        setCurrentSaleCount();
        setChargeAmountTv();
    }

    @Override
    public void OnDiscountRemoved() {
        setChargeAmountTv();
        updateLibraryDiscountAdapter();
        updateDynamicBoxAdapter();
    }

    @Override
    public void OnTransactionCancelled() {

    }

    private void setChargeAmountTv(){
        //SaleModelInstance.getInstance().getSaleModel().setDiscountedAmountOfSale();
        orderManager.setDiscountedAmountOfSale();

        double amountx = SaleModelInstance.getInstance().getSaleModel().getSale().getDiscountedAmount();
        String amountStr = CommonUtils.getDoubleStrValueForView(amountx, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());

        if(amountx <= 0d)
            chargeAmountTv.setText("");
        else
            chargeAmountTv.setText(amountStr);
    }

    private void setCurrentSaleCount(){
        if(currentSaleCount == 0){
            saleCountTv.setVisibility(View.GONE);

            if(!isCustomerExist)
                toolbarTitleTv.setText(getResources().getString(R.string.no_sale));
            else
                toolbarTitleTv.setText(customerName);
        }else {
            saleCountTv.setVisibility(View.VISIBLE);
            saleCountTv.setText(String.valueOf(currentSaleCount));

            if(!isCustomerExist)
                toolbarTitleTv.setText(getResources().getString(R.string.current_sale));
            else
                toolbarTitleTv.setText(customerName);
        }
    }

    private void addUserUUIDToSale(){
        orderManager.setUserIdToOrder(user.getUuid());
    }

    @Override
    public void OnPaymentReturn(int status) {
        onItemsCleared();
    }

    @Override
    public void OnDiscountUpdated() {
        setChargeAmountTv();
        updateLibraryDiscountAdapter();
        updateDynamicBoxAdapter();
    }

    @Override
    public void OnProductUpdated() {
        updateLibraryProductAdapter();
        updateDynamicBoxAdapter();
    }

    @Override
    public void OnCategoryUpdated() {
        updateLibraryCategoryAdapter();
        updateDynamicBoxAdapter();
    }

    private void updateDynamicBoxAdapter() {
        if(keypadFragment != null )
            keypadFragment.setDynamicBoxAdapter();
    }

    private void updateLibraryDiscountAdapter() {
        if(libraryFragment != null && libraryFragment.getDiscountListAdapter() != null)
            libraryFragment.setDiscountAdapter();
    }

    private void updateLibraryProductAdapter() {
        if(libraryFragment != null && libraryFragment.getProductListAdapter() != null)
            libraryFragment.setProductAdapter(0);
    }

    private void updateLibraryCategoryAdapter() {
        if(libraryFragment != null && libraryFragment.getCategorySelectListAdapter() != null)
            libraryFragment.setCategoryAdapter();
    }

    private void startAnimation(View startView){
        AnimationUtil.startAnimation(startView, saleCountTv, mainFl, (Activity) getContext());
    }

    @Override
    public void OnViewCallback(View view) {
        startAnimation(view);
    }


}