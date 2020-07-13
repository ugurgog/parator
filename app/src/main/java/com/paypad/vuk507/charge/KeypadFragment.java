package com.paypad.vuk507.charge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.DynamicItemSelectFragmant;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicStructListAdapter;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.payment.CashSelectFragment;
import com.paypad.vuk507.charge.payment.CreditCardSelectFragment;
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.charge.sale.AddNoteToSaleFragment;
import com.paypad.vuk507.charge.sale.DynamicAmountFragment;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProductUnitTypeEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.interfaces.ReturnViewCallback;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.uiUtils.keypad.KeyPad;
import com.paypad.vuk507.uiUtils.keypad.KeyPadClick;
import com.paypad.vuk507.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.vuk507.uiUtils.keypad.KeyPadWithoutAdd;
import com.paypad.vuk507.uiUtils.keypad.keyPadClickListener;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.vuk507.charge.ChargeFragment.CUSTOM_ITEM_ADD_FROM_KEYPAD;
import static com.paypad.vuk507.constants.CustomConstants.DYNAMIC_BOX_COUNT;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class KeypadFragment extends BaseFragment implements
        StructSelectFragment.StructSelectListener,
        DynamicItemSelectFragmant.DynamicItemSelectListener ,
        PaymentStatusCallback,
        ReturnViewCallback {

    View mView;

    @BindView(R.id.taxTypeRv)
    RecyclerView taxTypeRv;
    @BindView(R.id.noteMainll)
    LinearLayout noteMainll;
    @BindView(R.id.keypadMainLl)
    LinearLayout keypadMainLl;

    //Sale note variables
    @BindView(R.id.notePicImgv)
    ImageView notePicImgv;
    @BindView(R.id.saleNoteTv)
    TextView saleNoteTv;
    @BindView(R.id.saleAmountTv)
    TextView saleAmountTv;

    private KeyPadWithoutAdd keypad;
    private User user;
    private DynamicStructListAdapter dynamicStructListAdapter;

    private StructSelectFragment structSelectFragment;
    private DynamicItemSelectFragmant dynamicItemSelectFragmant;

    private PaymentStatusCallback paymentStatusCallback;

    private Realm realm;
    private long categoryId;

    private CashSelectFragment cashSelectFragment;
    private CreditCardSelectFragment creditCardSelectFragment;

    private ReturnViewCallback returnViewCallback;


    static final int PRODUCT_SELECT_FROM_DYNAMIC_BOX = 0;
    static final int PRODUCT_SELECT_FROM_CATEGORY_LIST = 1;

    private double amount = 0d;

    private SaleCalculateCallback saleCalculateCallback;

    private Transaction mTransaction;
    private IOrderManager orderManager;

    public KeypadFragment() {

    }

    public void setSaleCalculateCallback(SaleCalculateCallback saleCalculateCallback) {
        this.saleCalculateCallback = saleCalculateCallback;
    }

    public void setPaymentStatusCallback(PaymentStatusCallback paymentStatusCallback) {
        this.paymentStatusCallback = paymentStatusCallback;
    }

    public void setReturnViewCallback(ReturnViewCallback returnViewCallback) {
        this.returnViewCallback = returnViewCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        initListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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

    private void initVariables() {
        orderManager = new OrderManager();
        realm = Realm.getDefaultInstance();
        keypad = mView.findViewById(R.id.keypad);
        saleAmountTv.setHint("0,00".concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        saleNoteTv.setHint(getResources().getString(R.string.add_note));

        initStructSelectFragment();
        initRecyclerView();

    }

    private void initStructSelectFragment(){
        structSelectFragment = new StructSelectFragment();
        structSelectFragment.setStructListener(this);
    }

    private void initListeners() {
        keypad.setOnNumPadClickListener(new KeyPadClick(new KeyPadSingleNumberListener() {
            @Override
            public void onKeypadClicked(Integer number) {

                if(number == -1){
                    if(amount > 0d){
                        saleCalculateCallback.onRemoveCustomAmount(amount);
                        clearAmountFields();

                        if(orderManager.getOrderItemCount() == 0){
                            saleCalculateCallback.onItemsCleared();
                        }


                    }else {
                        if(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleCount() > 0){
                            clearItems();
                        }else {
                            clearAmountFields();
                        }
                    }
                }else if(number == -2){

                    /*if(amount > 0){
                        saleCalculateCallback.OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_KEYPAD);
                    }*/

                }else {
                    if(amount == 0){
                        amount = (amount  + number) / 100;
                        saleCalculateCallback.onNewSaleCreated();
                    }else {
                        amount = (amount * 10) + (number / 100.00d);
                    }

                    if(amount > MAX_PRICE_VALUE){
                        amount = MAX_PRICE_VALUE;
                    }

                    String amountStr = CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                    saleAmountTv.setText(amountStr);

                    saleCalculateCallback.onCustomAmountReturn(amount);
                }
            }
        }));

        saleNoteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new AddNoteToSaleFragment(saleNoteTv.getText().toString(), new AddNoteToSaleFragment.NoteCallback() {
                    @Override
                    public void onNoteReturn(String note) {
                        saleNoteTv.setText(note);
                        saleCalculateCallback.onSaleNoteReturn(note);

                        if(note != null && !note.isEmpty())
                            notePicImgv.setVisibility(View.VISIBLE);
                        else
                            notePicImgv.setVisibility(View.GONE);
                    }
                }));
            }
        });
    }

    public double getAmount() {
        return amount;
    }

    public void clearAmountFields(){
        saleAmountTv.setText("");
        amount = 0;
        notePicImgv.setVisibility(View.GONE);
        saleNoteTv.setText("");
    }

    public void clearItems(){
        new CustomDialogBox.Builder((Activity) getContext())
                .setTitle(getContext().getResources().getString(R.string.clear_sale))
                .setMessage(getContext().getResources().getString(R.string.question_are_you_sure))
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(getContext().getResources().getString(R.string.cancel))
                .setNegativeBtnBackground(getContext().getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.yes))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEditTextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        clearAmountFields();
                        saleCalculateCallback.onItemsCleared();
                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                }).build();
    }

    private void initRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        taxTypeRv.setLayoutManager(gridLayoutManager);
        setDynamicBoxAdapter();
    }

    public void setDynamicBoxAdapter(){
        if(user == null || user.getUuid() == null)
            return;

        RealmResults<DynamicBoxModel> dynamicBoxModels = DynamicBoxModelDBHelper.getAllDynamicBoxes(user.getUuid());
        List<DynamicBoxModel> dynamicBoxModelList = new ArrayList(dynamicBoxModels);

        if(dynamicBoxModelList.size() < DYNAMIC_BOX_COUNT){
            for(int i=dynamicBoxModelList.size(); i < DYNAMIC_BOX_COUNT; i++){
                DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
                dynamicBoxModelList.add(dynamicBoxModel);
            }
        }else {
            DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
            dynamicBoxModelList.add(dynamicBoxModel);
        }

        LogUtil.logDynamicBoxList(dynamicBoxModelList);

        dynamicStructListAdapter = new DynamicStructListAdapter(getContext(), dynamicBoxModelList, mFragmentNavigation, new ReturnDynamicBoxListener() {
            @Override
            public void onReturn(DynamicBoxModel dynamicBoxModel, ItemProcessEnum processEnum) {

                if(processEnum == ItemProcessEnum.SELECTED){

                    if(dynamicBoxModel.getStructId() == 0){
                        initStructSelectFragment();
                        structSelectFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), structSelectFragment.getTag());
                    }else {
                        handleDynamicBoxSelection(dynamicBoxModel);
                    }
                }else if(processEnum == ItemProcessEnum.DELETED){
                    deleteDynamicBox(dynamicBoxModel);
                }
            }
        });
        dynamicStructListAdapter.setReturnViewCallback(this);
        taxTypeRv.setAdapter(dynamicStructListAdapter);
    }

    private void handleDynamicBoxSelection(DynamicBoxModel dynamicBoxModel) {

        if(dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()){
            categoryId = dynamicBoxModel.getItemId();
            onStructClick(DynamicStructEnum.PRODUCT_SET, true);
        }else {
            categoryId = 0;

            if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                Product product = ProductDBHelper.getProduct(dynamicBoxModel.getItemId());
                handleProductSelect(product, PRODUCT_SELECT_FROM_DYNAMIC_BOX);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()){
                Discount discount = DiscountDBHelper.getDiscount(dynamicBoxModel.getItemId());
                saleCalculateCallback.onDiscountSelected(discount);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){

                if(dynamicBoxModel.getItemId() == PaymentTypeEnum.CASH.getId()){

                    if(amount > 0d){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_select_tax_rate));
                    }else {
                        orderManager.setRemainAmountByDiscountedAmount();

                        Log.i("Info", KeypadFragment.class.getName() + " getRemainAmount:" + orderManager.getRemainAmount());

                        createInitialTransaction();
                        initCashSelectFragment();
                        mFragmentNavigation.pushFragment(cashSelectFragment);
                    }

                }else if(dynamicBoxModel.getItemId() == PaymentTypeEnum.CREDIT_CARD.getId()){

                    if(amount > 0d){
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.please_select_tax_rate));
                    }else {
                        orderManager.setRemainAmountByDiscountedAmount();
                        createInitialTransaction();
                        initCreditCardSelectFragment();
                        mFragmentNavigation.pushFragment(creditCardSelectFragment);
                    }
                }else {

                    //TODO - Diger odeme tipleri icin burada aksiyon alacagiz
                }


            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()){

                TaxModel taxModel = new TaxModel();
                if(dynamicBoxModel.getItemId() < 0){
                    TaxRateEnum taxRateEnum = TaxRateEnum.getById(dynamicBoxModel.getItemId());
                    taxModel.setId(taxRateEnum.getId());
                    taxModel.setName(taxRateEnum.getLabel());
                    taxModel.setTaxRate(taxRateEnum.getRateValue());
                }else
                    taxModel = TaxDBHelper.getTax(dynamicBoxModel.getItemId());

                saleCalculateCallback.OnTaxSelected(taxModel);
            }
        }
    }

    private void initCashSelectFragment(){
        cashSelectFragment = new CashSelectFragment(mTransaction);
        cashSelectFragment.setPaymentStatusCallback(this);
    }

    private void initCreditCardSelectFragment(){
        creditCardSelectFragment = new CreditCardSelectFragment(mTransaction);
        creditCardSelectFragment.setPaymentStatusCallback(this);
    }

    private void createInitialTransaction() {
        mTransaction = orderManager.addTransactionToOrder(orderManager.getRemainAmount());
    }

    private void deleteDynamicBox(DynamicBoxModel dynamicBoxModel){
        new CustomDialogBox.Builder((Activity) getContext())
                .setMessage(getContext().getResources().getString(R.string.sure_to_delete_dynamic_box))
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(getContext().getResources().getString(R.string.cancel))
                .setNegativeBtnBackground(getContext().getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.yes))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEditTextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        DynamicBoxModelDBHelper.deleteDynamicBoxByStructAndItemId(dynamicBoxModel.getStructId(), dynamicBoxModel.getItemId(), user.getUuid(), new CompleteCallback() {
                            @Override
                            public void onComplete(BaseResponse baseResponse) {
                                CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                                if(baseResponse.isSuccess()){
                                    setDynamicBoxAdapter();
                                }
                            }
                        });

                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                }).build();
    }

    @Override
    public void onStructClick(DynamicStructEnum dynamicStructEnum, boolean fromCategory) {
        dynamicItemSelectFragmant = new DynamicItemSelectFragmant(dynamicStructEnum);
        dynamicItemSelectFragmant.setDynamicItemSelectListener(this);

        if(fromCategory)
            dynamicItemSelectFragmant.setCategoryId(categoryId);
        else
            dynamicItemSelectFragmant.setCategoryId(0);

        dynamicItemSelectFragmant.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), dynamicItemSelectFragmant.getTag());
    }

    private void createDynamicBox(DynamicBoxModel dynamicBoxModel){
        DynamicBoxModelDBHelper.createOrUpdateDynamicBox(dynamicBoxModel, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    setDynamicBoxAdapter();
                }
                dismissDynamicFragment();
            }
        });
    }

    private void dismissDynamicFragment() {
        if(dynamicItemSelectFragmant != null)
            dynamicItemSelectFragmant.dismiss();
    }

    @Override
    public void onProductClick(Product product) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(product.getId());
        dynamicBoxModel.setCreateUserId(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.PRODUCT_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onDiscountClick(Discount discount) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(discount.getId());
        dynamicBoxModel.setCreateUserId(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.DISCOUNT_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onCategoryClick(Category category) {

        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(category.getId());
        dynamicBoxModel.setCreateUserId(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.CATEGORY_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onTaxClick(TaxModel taxModel) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(taxModel.getId());
        dynamicBoxModel.setCreateUserId(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.TAX_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onPaymentClick(PaymentTypeEnum paymentType) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(paymentType.getId());
        //dynamicBoxModel.setItemName(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? paymentType.getLabelTr() : paymentType.getLabelEn());
        dynamicBoxModel.setCreateUserId(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.PAYMENT_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onCategoryProductSelected(Product product) {
        handleProductSelect(product, PRODUCT_SELECT_FROM_CATEGORY_LIST);
        dismissDynamicFragment();
    }

    DynamicStructListAdapter getDynamicStructListAdapter() {
        return dynamicStructListAdapter;
    }

    private void handleProductSelect(Product product, int productSelectFrom){
        if(product.getAmount() == 0){

            mFragmentNavigation.pushFragment(new DynamicAmountFragment(product, new AmountCallback() {
                @Override
                public void OnDynamicAmountReturn(double amount) {
                    saleCalculateCallback.onProductSelected(product, amount, true);
                }
            }));
        }else {
            saleCalculateCallback.onProductSelected(product, product.getAmount(), false);
        }
    }

    @Override
    public void OnPaymentReturn(int status) {
        try{
            if(cashSelectFragment != null)
                Objects.requireNonNull(cashSelectFragment.getActivity()).onBackPressed();

        }catch (Exception e){
            Log.i("Info", "Error:" + e);
        }

        try{
            if(creditCardSelectFragment != null)
                Objects.requireNonNull(creditCardSelectFragment.getActivity()).onBackPressed();

        }catch (Exception e){
            Log.i("Info", "Error:" + e);
        }

        paymentStatusCallback.OnPaymentReturn(status);
    }

    public TextView getSaleAmountTv() {
        return saleAmountTv;
    }

    @Override
    public void OnViewCallback(View view) {
        returnViewCallback.OnViewCallback(view);
    }
}