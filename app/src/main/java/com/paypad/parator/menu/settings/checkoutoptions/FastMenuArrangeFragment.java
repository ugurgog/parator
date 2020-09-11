package com.paypad.parator.menu.settings.checkoutoptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.KeypadFragment;
import com.paypad.parator.charge.dynamicStruct.DynamicItemSelectFragmant;
import com.paypad.parator.charge.dynamicStruct.StructSelectFragment;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicStructListAdapter;
import com.paypad.parator.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.DiscountDBHelper;
import com.paypad.parator.db.DynamicBoxModelDBHelper;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.DynamicStructEnum;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TaxRateEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.DynamicBoxModel;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBoxVert;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;

public class FastMenuArrangeFragment extends BaseFragment implements StructSelectFragment.StructSelectListener,
        DynamicItemSelectFragmant.DynamicItemSelectListener {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;
    @BindView(R.id.createBoxBtn)
    Button createBoxBtn;

    private Context mContext;
    private User user;
    private SharedPreferences loginPreferences;
    private FastMenuAdapter fastMenuAdapter;
    private Realm realm;
    private StructSelectFragment structSelectFragment;
    private DynamicItemSelectFragmant dynamicItemSelectFragmant;

    public FastMenuArrangeFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_arrange_fast_menu, container, false);
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
                ((Activity)mContext).onBackPressed();
            }
        });

        createBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initStructSelectFragment();
                structSelectFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), structSelectFragment.getTag());
            }
        });
    }

    private void initStructSelectFragment(){
        structSelectFragment = new StructSelectFragment();
        structSelectFragment.setStructListener(this);
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        loginPreferences = mContext.getSharedPreferences("disabledPaymentTypes", MODE_PRIVATE);
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.fast_menu));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemsRv.setLayoutManager(linearLayoutManager);
        setDynamicBoxAdapter();
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

        Collections.sort(dynamicBoxModelList, new KeypadFragment.DynamicBoxSeqNumComparator());

        fastMenuAdapter = new FastMenuAdapter(getContext(), dynamicBoxModelList, new ReturnDynamicBoxListener() {
            @Override
            public void onReturn(DynamicBoxModel dynamicBoxModel, ItemProcessEnum processEnum) {
                if(processEnum == ItemProcessEnum.DELETED){
                    deleteDynamicBox(dynamicBoxModel);
                }
            }
        });

        itemsRv.setAdapter(fastMenuAdapter);

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

                DynamicBoxModel draggedBox = ((FastMenuAdapter.ItemHolder) dragged).getDynamicBoxModel();
                DynamicBoxModel targetBox = ((FastMenuAdapter.ItemHolder) target).getDynamicBoxModel();

                if(draggedBox.getId() == null  || targetBox.getId() == null)
                    return false;

                try{
                    Collections.swap(dynamicBoxModelList, position_dragged, position_target);
                    fastMenuAdapter.notifyItemMoved(position_dragged, position_target);

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
        itemTouchHelper.attachToRecyclerView(itemsRv);
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

                        if(baseResponse.isSuccess())
                            setDynamicBoxAdapter();
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
        dynamicItemSelectFragmant.setCategoryId(0);
        dynamicItemSelectFragmant.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), dynamicItemSelectFragmant.getTag());
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

    public class FastMenuAdapter extends RecyclerView.Adapter<FastMenuAdapter.ItemHolder> {

        private Context context;
        private List<DynamicBoxModel> boxModels = new ArrayList<>();
        private ReturnDynamicBoxListener dynamicBoxListener;

        FastMenuAdapter(Context context, List<DynamicBoxModel> boxModels, ReturnDynamicBoxListener listener) {
            this.context = context;
            this.boxModels.addAll(boxModels);
            this.dynamicBoxListener = listener;
        }

        @NonNull
        @Override
        public FastMenuAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_fast_menu_box, parent, false);
            return new FastMenuAdapter.ItemHolder(itemView);
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private ImageView itemIconImgv;
            private TextView nameTv;
            private TextView dateTv;
            private ImageView deleteImgv;
            public DynamicBoxModel dynamicBoxModel;
            private int position;

            public DynamicBoxModel getDynamicBoxModel() {
                return dynamicBoxModel;
            }

            public ItemHolder(View view) {
                super(view);
                itemIconImgv = view.findViewById(R.id.itemIconImgv);
                nameTv = view.findViewById(R.id.nameTv);
                dateTv = view.findViewById(R.id.dateTv);
                deleteImgv = view.findViewById(R.id.deleteImgv);

                deleteImgv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dynamicBoxListener.onReturn(dynamicBoxModel, ItemProcessEnum.DELETED);
                    }
                });
            }

            public void setData(DynamicBoxModel dynamicBoxModel, int position) {
                this.dynamicBoxModel = dynamicBoxModel;
                this.position = position;
                setItemName();
                setIcon();
                setTransactionDate();
            }

            private void setTransactionDate() {
                @SuppressLint("SimpleDateFormat")
                String trxDate = new SimpleDateFormat("dd MM yyyy HH:mm").format(dynamicBoxModel.getCreateDate());
                dateTv.setText(trxDate);
            }

            private void setIcon() {
                if(dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()){
                    Glide.with(context)
                            .load(R.drawable.icon_discount_box_white_64dp)
                            .into(itemIconImgv);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                    Glide.with(context)
                            .load(R.drawable.icon_product_white_64dp)
                            .into(itemIconImgv);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()){
                    Glide.with(context)
                            .load(R.drawable.icon_category_white_64dp)
                            .into(itemIconImgv);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()){
                    Glide.with(context)
                            .load(R.drawable.icon_tax_white_64dp)
                            .into(itemIconImgv);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){
                    Glide.with(context)
                            .load(R.drawable.icon_payment_white_64dp)
                            .into(itemIconImgv);
                }
            }

            private void setItemName() {
                if (dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()) {
                    Discount discount = DiscountDBHelper.getDiscountById(dynamicBoxModel.getItemId());
                    setDiscountNameText(discount);
                } else if (dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()) {
                    Product product = ProductDBHelper.getProduct(dynamicBoxModel.getItemId());

                    if (product != null)
                        setProductNameText(product);
                } else if (dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()) {
                    Category category = CategoryDBHelper.getCategory(dynamicBoxModel.getItemId());
                    itemIconImgv.setBackgroundColor(context.getResources().getColor(category.getColorId(), null));
                    nameTv.setText(category.getName());
                } else if (dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()) {
                    setTaxNameText(dynamicBoxModel);
                } else if (dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()) {
                    if (dynamicBoxModel.getItemId() < 0) {
                        PaymentTypeEnum paymentType = PaymentTypeEnum.getById(dynamicBoxModel.getItemId());
                        nameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ?
                                Objects.requireNonNull(paymentType).getLabelTr() : Objects.requireNonNull(paymentType).getLabelEn());
                    }
                }
            }

            private void setDiscountNameText(Discount discount){
                String value;
                if(discount.getAmount() > 0d)
                    value = CommonUtils.getDoubleStrValueForView(discount.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                else
                    value = "% ".concat(CommonUtils.getDoubleStrValueForView(discount.getRate(), TYPE_RATE));

                nameTv.setText(discount.getName().concat(" (").concat(value).concat(")"));
            }

            private void setProductNameText(Product product){
                if(product!= null && product.getColorId() > 0)
                    itemIconImgv.setBackgroundColor(context.getResources().getColor(product.getColorId(), null));

                if(product != null && product.getAmount() > 0d)
                    nameTv.setText(product.getName().concat(" ").concat(CommonUtils.getDoubleStrValueForView(product.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())));
                else
                    nameTv.setText(product.getName());
            }

            private void setTaxNameText(DynamicBoxModel dynamicBoxModel){
                if(dynamicBoxModel.getItemId() < 0){
                    TaxRateEnum taxRateEnum = TaxRateEnum.getById(dynamicBoxModel.getItemId());
                    nameTv.setText(Objects.requireNonNull(taxRateEnum).getLabel().concat(
                            " (% ".concat(CommonUtils.getDoubleStrValueForView(taxRateEnum.getRateValue(), TYPE_RATE)).concat(")")));
                }else {
                    TaxModel taxModel = TaxDBHelper.getTax(dynamicBoxModel.getItemId());
                    nameTv.setText(taxModel.getName().concat(
                            " (% ".concat(CommonUtils.getDoubleStrValueForView(taxModel.getTaxRate(), TYPE_RATE)).concat(")")));
                }
            }
        }

        @Override
        public void onBindViewHolder(final FastMenuAdapter.ItemHolder holder, final int position) {
            DynamicBoxModel dynamicBoxModel = boxModels.get(position);
            holder.setData(dynamicBoxModel, position);
        }

        @Override
        public int getItemCount() {
            if(boxModels != null)
                return boxModels.size();
            else
                return 0;
        }
    }
}