package com.paypad.parator.charge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.adapter.CustomViewPagerAdapter;
import com.paypad.parator.charge.interfaces.SaleCalculateCallback;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.charge.payment.orderpayment.OrderChargePaymentFragment;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.charge.sale.SaleListFragment;
import com.paypad.parator.charge.utils.AnimationUtil;
import com.paypad.parator.db.DynamicBoxModelDBHelper;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.SaleDBHelper;
import com.paypad.parator.db.StoreDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.DynamicStructEnum;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.enums.TutorialTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.LocationGrantedCallback;
import com.paypad.parator.interfaces.ReturnViewCallback;
import com.paypad.parator.interfaces.TutorialSelectedCallback;
import com.paypad.parator.menu.customer.CustomerFragment;
import com.paypad.parator.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.parator.menu.item.ItemListFragment;
import com.paypad.parator.menu.reports.ReportsFragment;
import com.paypad.parator.menu.settings.SettingsFragment;
import com.paypad.parator.menu.settings.passcode.PasscodeTypeActivity;
import com.paypad.parator.menu.settings.profile.EditStoreFragment;
import com.paypad.parator.menu.support.SupportFragment;
import com.paypad.parator.menu.transactions.TransactionsFragment;
import com.paypad.parator.model.Customer;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.DynamicBoxModel;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.Store;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.ConversionHelper;
import com.paypad.parator.utils.CustomDialogBoxVert;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.PermissionModule;
import com.paypad.parator.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class ChargeFragment extends BaseFragment implements
        SaleCalculateCallback,
        PaymentStatusCallback,
        ItemListFragment.DiscountUpdateCallback,
        ItemListFragment.ProductUpdateCallback,
        ItemListFragment.CategoryUpdateCallback,
        ReturnViewCallback,
        LocationGrantedCallback,
        TutorialSelectedCallback,
        WalkthroughCallback{

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

    //Tutorial Views
    /*@BindView(R.id.tutorialRl)
    RelativeLayout tutorialRl;
    @BindView(R.id.closeTutorialImgv)
    ImageView closeTutorialImgv;
    @BindView(R.id.tutorialMsgTv)
    TextView tutorialMsgTv;*/

    private static final int TAB_KEYPAD = 0;
    private static final int TAB_LIBRARY = 1;

    static final int CUSTOM_ITEM_ADD_FROM_KEYPAD = 0;
    private static final int CUSTOM_ITEM_ADD_FROM_SALELIST = 1;
    private static final int CUSTOM_ITEM_ADD_FROM_CHARGE = 2;

    private int selectedTabPosition = TAB_KEYPAD;
    private boolean mDrawerState;
    private int currentSaleCount = 0;

    private CustomViewPagerAdapter customViewPagerAdapter;
    private User user;
    private KeypadFragment keypadFragment;
    private LibraryFragment libraryFragment;
    private double totalAmount;
    private String saleNote;
    private boolean isCustomerExist= false;
    private String customerName = "";
    private OrderChargePaymentFragment orderChargePaymentFragment;
    private LocationRequestFragment locationRequestFragment;
    private SupportFragment supportFragment;
    private EditStoreFragment editStoreFragment;

    private OrderItem orderItem = null;
    private TaxModel mTaxModel = null;
    private IOrderManager orderManager;
    private PermissionModule permissionModule;
    private Passcode passcode;
    private Context mContext;
    private Tutorial tutorial;
    private Integer walkthrough = null;
    private TutorialTypeEnum tutorialType = null;

    private TextView animationTextView;

    //private List<Discount> discountList;

    private int[] tabIcons = {
            R.drawable.ic_keyboard_black_24dp,
            R.drawable.ic_library_books_black_24dp};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);

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
        }
        return mView;
    }

    private void initVariables() {
        permissionModule = new PermissionModule(mContext);
        initTutorial();
        orderManager = new OrderManager();
        chargeAmountTv.setHint("0,00".concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        chargell.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 0));
        SaleModelInstance.reset();

        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);

        DataUtils.checkPrinterSettings(user.getId());
        DataUtils.checkAutoIncrement(user.getId());
        setUpPager();
        setDrawerListeners();
    }

    private void initTutorial() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);
        walkthrough = WALK_THROUGH_END;
    }

    private void initListeners() {
        toolbarll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(orderItem != null ){
                    if(mTaxModel == null){
                        CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_tax_rate), ToastEnum.TOAST_WARNING);
                        return ;
                    }else {
                        if(!OrderManager.isSaleItemInSale(
                                SaleModelInstance.getInstance().getSaleModel().getOrderItems(), orderItem)){
                            OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_SALELIST);
                        }
                    }
                }
                startSaleListFragment();
            }
        });

        settingsImgv.setOnClickListener(v -> {
            settingsImgv.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
            if (mDrawerState) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        chargell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderItem != null ){

                    if(mTaxModel == null){
                        CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_tax_rate), ToastEnum.TOAST_WARNING);
                        return ;
                    }else {
                        if(!OrderManager.isSaleItemInSale(
                                SaleModelInstance.getInstance().getSaleModel().getOrderItems(), orderItem))
                            OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_CHARGE);
                    }
                }

                //Check transaction amount
                if(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount() <= 0d){
                    CommonUtils.showCustomToast(mContext, mContext.getResources().getString(R.string.sale_amount_zero), ToastEnum.TOAST_WARNING);
                    return;
                }

                //Check store Information
                checkStoreInformation();
            }
        });
    }

    private void checkStoreInformation(){
        Store store = StoreDBHelper.getStoreByUserId(user.getId());
        if(store == null){
            new CustomDialogBoxVert.Builder((Activity) mContext)
                    .setTitle(mContext.getResources().getString(R.string.store_information_missing))
                    .setMessage(mContext.getResources().getString(R.string.store_information_missing_message))
                    .setNegativeBtnVisibility(View.VISIBLE)
                    .setPositiveBtnVisibility(View.VISIBLE)
                    .setPositiveBtnText(mContext.getResources().getString(R.string.edit_store))
                    .setNegativeBtnText(mContext.getResources().getString(R.string.cancel))
                    .setPositiveBtnBackground(mContext.getResources().getColor(R.color.DodgerBlue, null))
                    .setNegativeBtnBackground(mContext.getResources().getColor(R.color.custom_btn_bg_color, null))
                    .setDurationTime(0)
                    .isCancellable(false)
                    .setEdittextVisibility(View.GONE)
                    .setpBtnTextColor(mContext.getResources().getColor(R.color.White, null))
                    .setnBtnTextColor(mContext.getResources().getColor(R.color.DodgerBlue, null))
                    .OnPositiveClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {
                            initEditStoreFragment();
                            mFragmentNavigation.pushFragment(editStoreFragment);
                        }
                    }).OnNegativeClicked(new CustomDialogListener() {
                @Override
                public void OnClick() {

                }
            }).build();
        }else
            goPayment();
    }

    private void initEditStoreFragment(){
        editStoreFragment = new EditStoreFragment();
        editStoreFragment.setCompleteCallback(new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                //goPayment();
            }
        });
    }

    private void goPayment(){
        if(!ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION))
            startSelectChargePaymentFragment();
        else if (!permissionModule.checkAccessFineLocationPermission()){
            initLocationRequestFragment();
            mFragmentNavigation.pushFragment(locationRequestFragment);
        }else
            startSelectChargePaymentFragment();
    }

    private void initLocationRequestFragment(){
        locationRequestFragment = new LocationRequestFragment();
        locationRequestFragment.setLocationGrantedCallback(this);
    }

    private void initSelectChargePaymentFragment(){
        orderChargePaymentFragment = new OrderChargePaymentFragment(tutorialType == TutorialTypeEnum.TUTORIAL_PAYMENT ? walkthrough : WALK_THROUGH_END);
        orderChargePaymentFragment.setPaymentStatusCallback(this);
        orderChargePaymentFragment.setSaleCalculateCallback(this);
        orderChargePaymentFragment.setWalkthroughCallback(this);
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
                .setColorFilter(mContext.getResources().getColor(R.color.tab_unselected, null), PorterDuff.Mode.SRC_IN);
    }

    private void setupViewPager() {
        keypadFragment = new KeypadFragment();
        keypadFragment.setSaleCalculateCallback(this);
        keypadFragment.setPaymentStatusCallback(this);
        keypadFragment.setReturnViewCallback(this);

        libraryFragment = new LibraryFragment();
        libraryFragment.setSaleCalculateCallback(this);
        libraryFragment.setReturnViewCallback(this);

        customViewPagerAdapter = new CustomViewPagerAdapter(getChildFragmentManager());
        customViewPagerAdapter.addFrag(keypadFragment, mContext.getResources().getString(R.string.keypad));
        customViewPagerAdapter.addFrag(libraryFragment, mContext.getResources().getString(R.string.library));
        htab_viewpager.setAdapter(customViewPagerAdapter);
    }

    private void setTabListener() {

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon())
                        .setColorFilter(mContext.getResources().getColor(R.color.tab_selected, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon())
                        .setColorFilter(mContext.getResources().getColor(R.color.tab_unselected, null), PorterDuff.Mode.SRC_IN);
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
                    SaleDBHelper.deleteAllOrders();

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
        initSupportFragment();
        mFragmentNavigation.pushFragment(supportFragment);
    }

    private void initSupportFragment(){
        supportFragment = new SupportFragment();
        supportFragment.setTutorialSelectedCallback(this);
    }

    private void startSettingsFragment() {
        mFragmentNavigation.pushFragment(new SettingsFragment());
    }

    private void startItemsFragment() {
        ItemListFragment itemListFragment = new ItemListFragment(walkthrough, tutorialType);
        itemListFragment.setDiscountUpdateCallback(this);
        itemListFragment.setProductUpdateCallback(this);
        itemListFragment.setCategoryUpdateCallback(this);
        itemListFragment.setWalkthroughCallback(this);
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
        mFragmentNavigation.pushFragment(new ReportsFragment());
    }

    private void startTransactionsFragment() {
        mFragmentNavigation.pushFragment(new TransactionsFragment());
    }

    @Override
    public void onProductSelected(Product product, double amount, boolean isDynamicAmount) {
        orderManager.addProductToOrder(mContext, product, amount, isDynamicAmount);
        addDefaultValuesToOrder();

        setChargeAmountTv();
        totalAmount = 0d;
        onNewSaleCreated();
    }

    @Override
    public void onKeyPadClicked(int number) {

    }

    @Override
    public void onDiscountSelected(Discount discount) {
        OrderManager.addDiscount(SaleModelInstance.getInstance().getSaleModel().getOrder(), discount);

        addDefaultValuesToOrder();
        setChargeAmountTv();

        updateLibraryDiscountAdapter();
        updateDynamicBoxAdapter();
    }

    @Override
    public void OnTaxSelected(TaxModel taxModel) {
        if(orderItem != null){
            mTaxModel = taxModel;

            totalAmount = OrderManager.getDiscountedAmountByAddingCustomItem(
                    SaleModelInstance.getInstance().getSaleModel().getOrder(), orderItem);
            OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_KEYPAD);

        }else {
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_custom_amount_first), ToastEnum.TOAST_WARNING);
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
        orderItem = null;
    }

    @Override
    public void onNewSaleCreated() {
        currentSaleCount ++;
        setCurrentSaleCount();
    }

    @Override
    public void OnCustomItemAdd(int addFromValue) {

        if(orderItem == null){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_type_custom_amount), ToastEnum.TOAST_WARNING);
            return;
        }

        if(mTaxModel == null){
            CommonUtils.showCustomToast(mContext, getResources().getString(R.string.please_select_tax_rate), ToastEnum.TOAST_WARNING);
            return;
        }

        if(saleNote != null)
            orderItem.setNote(saleNote);

        orderManager.addCustomItemToOrder(orderItem, ConversionHelper.convertTaxModelToOrderItemTax(mTaxModel));
        //SaleModelInstance.getInstance().getSaleModel().addCustomItem(orderItem, mTaxModel);

        currentSaleCount = orderManager.getOrderItemCount();
        //currentSaleCount = SaleModelInstance.getInstance().getSaleModel().getSaleCount();
        addDefaultValuesToOrder();

        setCurrentSaleCount();
        setChargeAmountTv();

        totalAmount = 0d;
        saleNote = "";
        orderItem = null;
        mTaxModel = null;
        keypadFragment.clearAmountFields();

        if(addFromValue == CUSTOM_ITEM_ADD_FROM_KEYPAD)
            startAnimation(keypadFragment.getSaleAmountTv());

        if(walkthrough == WALK_THROUGH_CONTINUE && tutorialType == TutorialTypeEnum.TUTORIAL_PAYMENT)
            tutorial.setTutorialMessage(mContext.getResources().getString(R.string.tap_to_charge_button));
    }

    @Override
    public void onCustomAmountReturn(double amount) {

        orderItem = new OrderItem();
        orderItem.setName(getResources().getString(R.string.custom_amount));
        orderItem.setId(UUID.randomUUID().toString());
        orderItem.setAmount(amount);
        orderItem.setQuantity(1);
        orderItem.setDynamicAmount(true);
        orderItem.setNote(saleNote != null ? saleNote : "");
        orderItem.setCategoryName(DataUtils.getCategoryName(mContext, 0));
        orderItem.setOrderId(SaleModelInstance.getInstance().getSaleModel().getOrder().getId());
        orderItem.setTransferred(false);

        totalAmount = OrderManager.getDiscountedAmountByAddingCustomItem(
                SaleModelInstance.getInstance().getSaleModel().getOrder(), orderItem);

        String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());

        if(totalAmount <= 0d)
            chargeAmountTv.setText("");
        else
            chargeAmountTv.setText(amountStr);

        if(walkthrough == WALK_THROUGH_CONTINUE  && tutorialType == TutorialTypeEnum.TUTORIAL_PAYMENT){
            RealmResults<DynamicBoxModel> dynamicBoxModels = DynamicBoxModelDBHelper.getAllDynamicBoxes(user.getId());

            boolean isExistTax = false;
            if(dynamicBoxModels != null && dynamicBoxModels.size() > 0){
                for(DynamicBoxModel dynamicBoxModel : dynamicBoxModels){
                    if(dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()){
                        isExistTax = true;
                        break;
                    }
                }
            }

            if(!isExistTax)
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.add_tax_to_dynamic_box));
            else
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.select_tax_from_dynamic_box));
        }
    }

    @Override
    public void onRemoveCustomAmount(double amount) {
        orderItem = null;
        OrderManager.calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
        totalAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount();
        //totalAmount = totalAmount - amount;


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
        SaleModelInstance.getInstance().getSaleModel().getOrder().setCustomerId(customer.getId());
        toolbarTitleTv.setText(customerName);
    }

    @Override
    public void onCustomerRemoved() {
        isCustomerExist = false;
        SaleModelInstance.getInstance().getSaleModel().getOrder().setCustomerId(0);
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
        OrderManager.calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
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
        orderManager.setDiscountedAmountOfSale();

        double amountx = SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount();
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

    private void addDefaultValuesToOrder(){
        OrderManager.setUserIdToOrder(SaleModelInstance.getInstance().getSaleModel().getOrder(), user.getId());
        OrderManager.setDeviceIdToOrder(mContext, SaleModelInstance.getInstance().getSaleModel().getOrder());
    }

    @Override
    public void OnPaymentReturn(int status) {
        onItemsCleared();
        Log.i("Info", "walkthrough:" + walkthrough);
        checkPasscodeAfterSale();
        finalizeTutorialAfterSale();
    }

    private void finalizeTutorialAfterSale() {
        if(walkthrough == WALK_THROUGH_CONTINUE && tutorialType == TutorialTypeEnum.TUTORIAL_PAYMENT){

            new CustomDialogBoxVert.Builder((Activity) mContext)
                    .setTitle(mContext.getResources().getString(R.string.congratulations))
                    .setMessage(mContext.getResources().getString(R.string.sale_tutorial_finalize_message))
                    .setNegativeBtnVisibility(View.VISIBLE)
                    .setPositiveBtnVisibility(View.VISIBLE)
                    .setPositiveBtnText(mContext.getResources().getString(R.string.see_transactions))
                    .setNegativeBtnText(mContext.getResources().getString(R.string.done))
                    .setPositiveBtnBackground(mContext.getResources().getColor(R.color.DodgerBlue, null))
                    .setNegativeBtnBackground(mContext.getResources().getColor(R.color.custom_btn_bg_color, null))
                    .setDurationTime(0)
                    .isCancellable(false)
                    .setEdittextVisibility(View.GONE)
                    .setpBtnTextColor(mContext.getResources().getColor(R.color.White, null))
                    .setnBtnTextColor(mContext.getResources().getColor(R.color.DodgerBlue, null))
                    .OnPositiveClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {
                            mFragmentNavigation.pushFragment(new TransactionsFragment());
                        }
                    }).OnNegativeClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }).build();

            walkthrough = WALK_THROUGH_END;
            OnWalkthroughResult(walkthrough);
        }
    }

    private void checkPasscodeAfterSale(){
        passcode = PasscodeDBHelper.getPasscodeByUserId(user.getId());
        if(passcode != null && passcode.isEnabled() && passcode.isAfterEachSaleEnabled() &&
                passcode.getPasscodeVal() != null && !passcode.getPasscodeVal().isEmpty()){
            Intent intent = new Intent(mContext, PasscodeTypeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("toolbarVisible", false);
            bundle.putString("passcodeVal", passcode.getPasscodeVal());
            intent.putExtras(bundle);
            startActivity(intent);
        }
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
        AnimationUtil.startAnimation(startView, saleCountTv, mainFl, (Activity) mContext);
    }

    @Override
    public void OnViewCallback(View view) {
        startAnimation(view);
    }


    @Override
    public void OnLocationGranted(boolean granted) {
        if(locationRequestFragment != null){
            try {
                locationRequestFragment.getActivity().onBackPressed();
            }catch (Exception e){

            }
        }

        startSelectChargePaymentFragment();
    }

    private void startSelectChargePaymentFragment(){
        OrderManager.setRemainAmountByDiscountedAmount();
        initSelectChargePaymentFragment();
        mFragmentNavigation.pushFragment(orderChargePaymentFragment);
    }

    @Override
    public void OnSelectedTutorial(TutorialTypeEnum tutorialType) {
        this.tutorialType = tutorialType;

        if (tutorialType == TutorialTypeEnum.TUTORIAL_CREATE_ITEM || tutorialType == TutorialTypeEnum.TUTORIAL_CREATE_UNIT ||
                tutorialType == TutorialTypeEnum.TUTORIAL_CREATE_TAX || tutorialType == TutorialTypeEnum.TUTORIAL_CREATE_CATEGORY ||
                tutorialType == TutorialTypeEnum.TUTORIAL_CREATE_DISCOUNT) {

            walkthrough = WALK_THROUGH_CONTINUE;
            tutorial.setLayoutVisibility(View.VISIBLE);
            tutorial.setTutorialMessage(mContext.getResources().getString(R.string.select_items_tutorial_message));
        }else if(tutorialType == TutorialTypeEnum.TUTORIAL_PAYMENT){
            walkthrough = WALK_THROUGH_CONTINUE;
            tutorial.setLayoutVisibility(View.VISIBLE);
            tutorial.setTutorialMessage(mContext.getResources().getString(R.string.enter_price_to_get_started));
        }
    }

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;

        if(walkthrough == WALK_THROUGH_END)
            tutorial.setLayoutVisibility(View.GONE);

    }
}