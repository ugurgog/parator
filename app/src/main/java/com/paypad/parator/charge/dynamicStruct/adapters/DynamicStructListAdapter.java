package com.paypad.parator.charge.dynamicStruct.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.DiscountDBHelper;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.enums.DynamicStructEnum;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TaxRateEnum;
import com.paypad.parator.interfaces.ReturnViewCallback;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.DynamicBoxModel;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paypad.parator.constants.CustomConstants.DYNAMIC_BOX_COUNT;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;

public class DynamicStructListAdapter extends RecyclerView.Adapter<DynamicStructListAdapter.StructListHolder> {

    private Context context;
    private List<DynamicBoxModel> boxModels = new ArrayList<>();
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnDynamicBoxListener dynamicBoxListener;
    private boolean maxBoxExceeded= false;
    private IOrderManager orderManager;
    private ReturnViewCallback returnViewCallback;

    public DynamicStructListAdapter(Context context, List<DynamicBoxModel> boxModels,
                                    BaseFragment.FragmentNavigation fragmentNavigation,
                                    ReturnDynamicBoxListener listener) {
        this.context = context;
        this.boxModels.addAll(boxModels);
        this.fragmentNavigation = fragmentNavigation;
        this.dynamicBoxListener = listener;
        orderManager = new OrderManager();
        setMaxBoxExceeded();
    }

    public void setReturnViewCallback(ReturnViewCallback returnViewCallback) {
        this.returnViewCallback = returnViewCallback;
    }

    private void setMaxBoxExceeded(){
        if(boxModels.size() > DYNAMIC_BOX_COUNT)
            maxBoxExceeded = true;
        else
            maxBoxExceeded = false;
    }

    @NonNull
    @Override
    public StructListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_box_layout, parent, false);
        return new StructListHolder(itemView);
    }

    public class StructListHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private TextView itemNameTv;
        private TextView itemValueTv;
        private ImageView iconImgv;
        private LinearLayout mainll;
        private FrameLayout content_frame;

        private DynamicBoxModel dynamicBoxModel;
        private int position;
        private long mLastClickTime = 0;

        StructListHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            iconImgv = view.findViewById(R.id.iconImgv);
            mainll = view.findViewById(R.id.mainll);
            itemValueTv = view.findViewById(R.id.itemValueTv);
            content_frame = view.findViewById(R.id.content_frame);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    dynamicBoxListener.onReturn(dynamicBoxModel, ItemProcessEnum.SELECTED);

                    if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                        Product product = ProductDBHelper.getProduct(dynamicBoxModel.getItemId());
                        if(product.getAmount() > 0d)
                            returnViewCallback.OnViewCallback(content_frame);
                    }
                }
            });

            itemCv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return false;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if (dynamicBoxModel.getStructId() != 0)
                        dynamicBoxListener.onReturn(dynamicBoxModel, ItemProcessEnum.DELETED);

                    return false;
                }
            });
        }

        void setData(DynamicBoxModel dynamicBoxModel, int position) {
            this.dynamicBoxModel = dynamicBoxModel;
            this.position = position;
            setItemName();
            setBoxWidth();
            setIcon();
        }

        private void setIcon() {
            if(dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_discount_box_white_64dp)
                        .into(iconImgv);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_product_white_64dp)
                        .into(iconImgv);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_category_white_64dp)
                        .into(iconImgv);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_tax_white_64dp)
                        .into(iconImgv);
            }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_payment_white_64dp)
                        .into(iconImgv);
            }
        }

        private void setBoxWidth() {
            int width = CommonUtils.getScreenWidth(context)
                    - CommonUtils.getPaddingInPixels(context, 16f);

            LinearLayout.LayoutParams params;

            if(maxBoxExceeded){
                params = new LinearLayout.LayoutParams(width / 4
                        - CommonUtils.getPaddingInPixels(context, 10f),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }else {
                params = new LinearLayout.LayoutParams(width / 4,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            int margin = CommonUtils.getPaddingInPixels(context, 2f);
            params.setMargins(margin, margin, margin, margin);
            itemCv.setLayoutParams(params);
        }

        private void setItemName() {
            if(dynamicBoxModel.getStructId() == 0){
                itemNameTv.setText("+");
                itemValueTv.setVisibility(View.GONE);
            }else {
                if(dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()){
                    Discount discount = DiscountDBHelper.getDiscountById(dynamicBoxModel.getItemId());
                    setDiscountNameText(discount);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                    Product product = ProductDBHelper.getProduct(dynamicBoxModel.getItemId());

                    if(product != null)
                        setProductNameText(product);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()){
                    Category category = CategoryDBHelper.getCategory(dynamicBoxModel.getItemId());
                    mainll.setBackgroundColor(context.getResources().getColor(category.getColorId(), null));
                    itemNameTv.setText(category.getName());
                    itemValueTv.setVisibility(View.GONE);

                    LogUtil.logCategory("setItemName", category);

                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()){
                    setTaxNameText(dynamicBoxModel);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){
                    itemValueTv.setVisibility(View.GONE);

                    if(dynamicBoxModel.getItemId() < 0){
                        PaymentTypeEnum paymentType = PaymentTypeEnum.getById(dynamicBoxModel.getItemId());
                        itemNameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ?
                                Objects.requireNonNull(paymentType).getLabelTr() : Objects.requireNonNull(paymentType).getLabelEn());
                    }
                }
            }
        }

        private void setDiscountNameText(Discount discount){
            String value;
            if(discount.getAmount() > 0d)
                value = CommonUtils.getDoubleStrValueForView(discount.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            else
                value = "% ".concat(CommonUtils.getDoubleStrValueForView(discount.getRate(), TYPE_RATE));

            itemValueTv.setVisibility(View.VISIBLE);
            itemNameTv.setText(discount.getName());
            itemValueTv.setText(value);
            setEnabilityOfDiscount(discount);
        }

        private void setProductNameText(Product product){
            if(product!= null && product.getColorId() > 0)
                mainll.setBackgroundColor(context.getResources().getColor(product.getColorId(), null));

            if(product != null && product.getAmount() > 0d){
                itemValueTv.setVisibility(View.VISIBLE);
                itemNameTv.setText(product.getName());
                itemValueTv.setText(CommonUtils.getDoubleStrValueForView(product.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
            } else{
                itemValueTv.setVisibility(View.GONE);
                itemNameTv.setText(product.getName());
            }
        }

        private void setTaxNameText(DynamicBoxModel dynamicBoxModel){
            itemValueTv.setVisibility(View.VISIBLE);

            if(dynamicBoxModel.getItemId() < 0){
                TaxRateEnum taxRateEnum = TaxRateEnum.getById(dynamicBoxModel.getItemId());
                itemNameTv.setText(Objects.requireNonNull(taxRateEnum).getLabel());
                itemValueTv.setText("% ".concat(CommonUtils.getDoubleStrValueForView(taxRateEnum.getRateValue(), TYPE_RATE)));

            }else {
                TaxModel taxModel = TaxDBHelper.getTax(dynamicBoxModel.getItemId());
                itemNameTv.setText(taxModel.getName());
                itemValueTv.setText("% ".concat(CommonUtils.getDoubleStrValueForView(taxModel.getTaxRate(), TYPE_RATE)));
            }
        }

        private void setEnabilityOfDiscount(Discount discount) {
            if(OrderManager.isDiscountInSale(SaleModelInstance.getInstance().getSaleModel().getOrder(), discount)){
                itemCv.setEnabled(false);
                itemNameTv.setTextColor(context.getResources().getColor(R.color.Black, null));
                itemValueTv.setTextColor(context.getResources().getColor(R.color.Black, null));
            }else {
                itemCv.setEnabled(true);
                itemNameTv.setTextColor(context.getResources().getColor(R.color.White, null));
                itemValueTv.setTextColor(context.getResources().getColor(R.color.White, null));
            }
        }
    }


    public void dynamicModelRemoveResult(int position){
        boxModels.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
    }

    public void dynamicModelChangedResult(int position){
        this.notifyItemChanged(position);
    }

    public void addDynamicModel(DynamicBoxModel dynamicBoxModel){
        if(boxModels != null && dynamicBoxModel != null){
            boxModels.add(dynamicBoxModel);
            this.notifyItemInserted(boxModels.size() - 1);
        }
    }

    @Override
    public void onBindViewHolder(final StructListHolder holder, final int position) {
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