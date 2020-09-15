package com.paypad.parator.charge;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.dynamicStruct.DynamicItemSelectFragmant;
import com.paypad.parator.charge.dynamicStruct.StructSelectFragment;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicStructListAdapter;
import com.paypad.parator.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.parator.charge.interfaces.AmountCallback;
import com.paypad.parator.charge.interfaces.SaleCalculateCallback;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.charge.payment.CashSelectFragment;
import com.paypad.parator.charge.payment.CreditCardSelectFragment;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.charge.sale.AddNoteToSaleFragment;
import com.paypad.parator.charge.sale.DynamicAmountFragment;
import com.paypad.parator.db.DiscountDBHelper;
import com.paypad.parator.db.DynamicBoxModelDBHelper;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.DynamicStructEnum;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TaxRateEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.ReturnViewCallback;
import com.paypad.parator.menu.reports.saleReport.SalesTopItemsFragment;
import com.paypad.parator.menu.transactions.adapters.SaleModelListAdapter;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.DynamicBoxModel;
import com.paypad.parator.model.OrderRefundItem;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.ReportOrderItem;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.uiUtils.keypad.KeyPadClick;
import com.paypad.parator.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.parator.uiUtils.keypad.KeyPadWithoutAdd;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBox;
import com.paypad.parator.utils.CustomDialogBoxVert;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static com.paypad.parator.constants.CustomConstants.DYNAMIC_BOX_COUNT;
import static com.paypad.parator.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.parator.constants.CustomConstants.TYPE_ORDER_PAYMENT;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

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

    //Order note variables
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
    private SharedPreferences loginPreferences;


    static final int PRODUCT_SELECT_FROM_DYNAMIC_BOX = 0;
    static final int PRODUCT_SELECT_FROM_CATEGORY_LIST = 1;

    private double amount = 0d;

    private SaleCalculateCallback saleCalculateCallback;

    private Transaction mTransaction;
    private IOrderManager orderManager;
    private Context mContext;

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
        Log.i("Info", "KeypadFragment onResume");
        initVariables();
        initListeners();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        mContext = context;
        Log.i("Info", "KeypadFragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mContext = null;
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(getContext());
    }

    private void initVariables() {
        orderManager = new OrderManager();
        loginPreferences = mContext.getSharedPreferences("disabledPaymentTypes", MODE_PRIVATE);
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

                        if(orderManager.getOrderItemCount() == 0)
                            saleCalculateCallback.onItemsCleared();

                    }else {
                        if(SaleModelInstance.getInstance().getSaleModel().getOrder().getTotalItemCount() > 0 ||
                                (SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscounts() != null &&
                                        SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscounts().size() > 0)){
                            clearItems();
                        }else {
                            clearAmountFields();
                        }
                    }
                }else if(number == -2){

                    /*if(amount > 0){
                        saleCalculateCallback.OnCustomItemAdd(CUSTOM_ITEM_ADD_FROM_KEYPAD);
                    }*/

                    if(amount > 0d) {
                        amount = CommonUtils.round((amount / 10), 2);

                        String amountStr = CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                        saleAmountTv.setText(amountStr);

                        saleCalculateCallback.onCustomAmountReturn(amount);
                    }

                }else {
                    if(amount == 0){
                        amount = (amount  + number) / 100;

                        if(amount > 0d)
                            saleCalculateCallback.onNewSaleCreated();
                    }else {
                        amount = (amount * 10) + (number / 100.00d);
                    }

                    if(amount > MAX_PRICE_VALUE){
                        amount = MAX_PRICE_VALUE;
                    }

                    if(amount <= 0d) return;

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
        String clearSaleDesc = mContext.getResources().getString(R.string.question_are_you_sure)
                .concat(" ")
                .concat( mContext.getResources().getString(R.string.items_and_discounts_deleted_from_sale));

        new CustomDialogBox.Builder((Activity) mContext)
                .setTitle(mContext.getResources().getString(R.string.clear_sale))
                .setMessage(clearSaleDesc)
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(mContext.getResources().getString(R.string.cancel))
                .setNegativeBtnBackground(mContext.getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(mContext.getResources().getString(R.string.yes))
                .setPositiveBtnBackground(mContext.getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
        taxTypeRv.setLayoutManager(gridLayoutManager);
        setDynamicBoxAdapter();
    }

    public static class DynamicBoxSeqNumComparator implements Comparator<DynamicBoxModel> {
        @Override
        public int compare(DynamicBoxModel o1, DynamicBoxModel o2) {
            return (int) (o1.getSequenceNumber() - o2.getSequenceNumber());
        }
    }

    public void setDynamicBoxAdapter(){
        if(user == null || user.getId() == null)
            return;

        RealmResults<DynamicBoxModel> dynamicBoxModels = DynamicBoxModelDBHelper.getAllDynamicBoxes(user.getId());
        List<DynamicBoxModel> dynamicBoxModelList = new ArrayList(dynamicBoxModels);

        //Payment type enable olmayanlar icin
        for(Iterator<DynamicBoxModel> its = dynamicBoxModelList.iterator(); its.hasNext();) {
            DynamicBoxModel dynamicBoxModel = its.next();

            if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){

                PaymentTypeEnum paymentTypeEnum = PaymentTypeEnum.getById(dynamicBoxModel.getItemId());

                if(!loginPreferences.getBoolean(String.valueOf(paymentTypeEnum.getId()), false))
                    its.remove();
            }
        }

        Collections.sort(dynamicBoxModelList, new DynamicBoxSeqNumComparator());

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                DynamicBoxModel draggedBox = ((DynamicStructListAdapter.StructListHolder) dragged).getDynamicBoxModel();
                DynamicBoxModel targetBox = ((DynamicStructListAdapter.StructListHolder) target).getDynamicBoxModel();

                if(draggedBox.getId() == null  || targetBox.getId() == null)
                    return false;

                try{
                    Collections.swap(dynamicBoxModelList, position_dragged, position_target);
                    dynamicStructListAdapter.notifyItemMoved(position_dragged, position_target);

                    int draggedSeqNum = draggedBox.getSequenceNumber();
                    int targetSeqNum = targetBox.getSequenceNumber();

                    updateDynamicBox(draggedBox, targetSeqNum);
                    updateDynamicBox(targetBox, draggedSeqNum);
                }catch (Exception e){

                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemTouchHelper.attachToRecyclerView(taxTypeRv);
    }

    private void updateDynamicBox(DynamicBoxModel dynamicBoxModel, int sequenceNumber){
        realm.beginTransaction();

        DynamicBoxModel dynamicBoxModel1 = realm.copyFromRealm(dynamicBoxModel);
        dynamicBoxModel1.setSequenceNumber(sequenceNumber);
        dynamicBoxModel1.setUpdateDate(new Date());
        dynamicBoxModel1.setUpdateUserId(user.getId());

        realm.commitTransaction();

        BaseResponse baseResponse = DynamicBoxModelDBHelper.createOrUpdateDynamicBox(dynamicBoxModel1);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);
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
                Discount discount = DiscountDBHelper.getDiscountById(dynamicBoxModel.getItemId());
                saleCalculateCallback.onDiscountSelected(discount);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){

                handlePaymentBoxSelection(dynamicBoxModel);


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

    private void handlePaymentBoxSelection(DynamicBoxModel dynamicBoxModel){
        if(amount > 0d) {
            CommonUtils.showCustomToast(getContext(), getResources().getString(R.string.please_select_tax_rate), ToastEnum.TOAST_WARNING);
            return;
        }

        //Check transaction amount
        if(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount() <= 0d){
            CommonUtils.showCustomToast(getContext(), getContext().getResources().getString(R.string.sale_amount_zero), ToastEnum.TOAST_WARNING);
            return;
        }

        OrderManager.setRemainAmountByDiscountedAmount();
        createInitialTransaction();

        if(dynamicBoxModel.getItemId() == PaymentTypeEnum.CASH.getId()){

            initCashSelectFragment();
            mFragmentNavigation.pushFragment(cashSelectFragment);

        }else if(dynamicBoxModel.getItemId() == PaymentTypeEnum.CREDIT_CARD.getId()){

            initCreditCardSelectFragment();
            mFragmentNavigation.pushFragment(creditCardSelectFragment);
        }else {

            //TODO - Diger odeme tipleri icin burada aksiyon alacagiz
        }
    }

    private void initCashSelectFragment(){
        cashSelectFragment = new CashSelectFragment(mTransaction, TYPE_ORDER_PAYMENT, WALK_THROUGH_END);
        cashSelectFragment.setPaymentStatusCallback(this);
    }

    private void initCreditCardSelectFragment(){
        creditCardSelectFragment = new CreditCardSelectFragment(mTransaction, TYPE_ORDER_PAYMENT);
        creditCardSelectFragment.setPaymentStatusCallback(this);
    }

    private void createInitialTransaction() {
        mTransaction = OrderManager.addTransactionToOrder(
                SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount(),
                SaleModelInstance.getInstance().getSaleModel().getTransactions(),
                SaleModelInstance.getInstance().getSaleModel().getOrder().getId()
        );
    }

    private void deleteDynamicBox(DynamicBoxModel dynamicBoxModel){
        new CustomDialogBoxVert.Builder((Activity) getContext())
                .setMessage(getContext().getResources().getString(R.string.sure_to_delete_dynamic_box))
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(getContext().getResources().getString(R.string.cancel))
                .setNegativeBtnBackground(getContext().getResources().getColor(R.color.custom_btn_bg_color, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.confirm_delete))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.DodgerBlue, null))
                .setpBtnTextColor(getContext().getResources().getColor(R.color.White, null))
                .setnBtnTextColor(getContext().getResources().getColor(R.color.DodgerBlue, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        BaseResponse baseResponse = DynamicBoxModelDBHelper.deleteDynamicBoxById(dynamicBoxModel.getId());
                        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

                        if(baseResponse.isSuccess()){
                            setDynamicBoxAdapter();
                        }
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
        dynamicItemSelectFragmant = new DynamicItemSelectFragmant(dynamicStructEnum, mFragmentNavigation);
        dynamicItemSelectFragmant.setDynamicItemSelectListener(this);

        if(fromCategory)
            dynamicItemSelectFragmant.setCategoryId(categoryId);
        else
            dynamicItemSelectFragmant.setCategoryId(0);

        dynamicItemSelectFragmant.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), dynamicItemSelectFragmant.getTag());
    }

    private void createDynamicBox(DynamicBoxModel dynamicBoxModel){
        BaseResponse baseResponse = DynamicBoxModelDBHelper.createOrUpdateDynamicBox(dynamicBoxModel);

        DataUtils.showBaseResponseMessage(getContext(), baseResponse);
        if(baseResponse.isSuccess()){
            setDynamicBoxAdapter();
        }
        dismissDynamicFragment();
    }

    private void dismissDynamicFragment() {
        if(dynamicItemSelectFragmant != null)
            dynamicItemSelectFragmant.dismiss();
    }

    @Override
    public void onProductClick(Product product) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setId(UUID.randomUUID().toString());
        dynamicBoxModel.setStructId(DynamicStructEnum.PRODUCT_SET.getId());
        dynamicBoxModel.setItemId(product.getId());
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setUserId(user.getId());
        dynamicBoxModel.setUpdateDate(new Date());
        dynamicBoxModel.setUpdateUserId(user.getId());
        dynamicBoxModel.setDeleted(false);
        dynamicBoxModel.setSequenceNumber(DynamicBoxModelDBHelper.getCurrentSequenceNumber());

        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onDiscountClick(Discount discount) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setId(UUID.randomUUID().toString());
        dynamicBoxModel.setStructId(DynamicStructEnum.DISCOUNT_SET.getId());
        dynamicBoxModel.setItemId(discount.getId());
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setUserId(user.getId());
        dynamicBoxModel.setUpdateDate(new Date());
        dynamicBoxModel.setUpdateUserId(user.getId());
        dynamicBoxModel.setDeleted(false);
        dynamicBoxModel.setSequenceNumber(DynamicBoxModelDBHelper.getCurrentSequenceNumber());

        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onCategoryClick(Category category) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setId(UUID.randomUUID().toString());
        dynamicBoxModel.setStructId(DynamicStructEnum.CATEGORY_SET.getId());
        dynamicBoxModel.setItemId(category.getId());
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setUserId(user.getId());
        dynamicBoxModel.setUpdateDate(new Date());
        dynamicBoxModel.setUpdateUserId(user.getId());
        dynamicBoxModel.setDeleted(false);
        dynamicBoxModel.setSequenceNumber(DynamicBoxModelDBHelper.getCurrentSequenceNumber());

        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onTaxClick(TaxModel taxModel) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setId(UUID.randomUUID().toString());
        dynamicBoxModel.setStructId(DynamicStructEnum.TAX_SET.getId());
        dynamicBoxModel.setItemId(taxModel.getId());
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setUserId(user.getId());
        dynamicBoxModel.setUpdateDate(new Date());
        dynamicBoxModel.setUpdateUserId(user.getId());
        dynamicBoxModel.setDeleted(false);
        dynamicBoxModel.setSequenceNumber(DynamicBoxModelDBHelper.getCurrentSequenceNumber());

        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onPaymentClick(PaymentTypeEnum paymentType) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setId(UUID.randomUUID().toString());
        dynamicBoxModel.setStructId(DynamicStructEnum.PAYMENT_SET.getId());
        dynamicBoxModel.setItemId(paymentType.getId());
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setUserId(user.getId());
        dynamicBoxModel.setUpdateDate(new Date());
        dynamicBoxModel.setUpdateUserId(user.getId());
        dynamicBoxModel.setDeleted(false);
        dynamicBoxModel.setSequenceNumber(DynamicBoxModelDBHelper.getCurrentSequenceNumber());

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