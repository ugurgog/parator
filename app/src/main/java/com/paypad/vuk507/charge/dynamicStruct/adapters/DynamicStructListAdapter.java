package com.paypad.vuk507.charge.dynamicStruct.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paypad.vuk507.constants.CustomConstants.DYNAMIC_BOX_COUNT;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class DynamicStructListAdapter extends RecyclerView.Adapter<DynamicStructListAdapter.StructListHolder> {

    private Context context;
    private List<DynamicBoxModel> boxModels = new ArrayList<>();
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnDynamicBoxListener dynamicBoxListener;
    private boolean maxBoxExceeded= false;

    public DynamicStructListAdapter(Context context, List<DynamicBoxModel> boxModels,
                                    BaseFragment.FragmentNavigation fragmentNavigation,
                                    ReturnDynamicBoxListener listener) {
        this.context = context;
        this.boxModels.addAll(boxModels);
        this.fragmentNavigation = fragmentNavigation;
        this.dynamicBoxListener = listener;
        setMaxBoxExceeded();
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

        private DynamicBoxModel dynamicBoxModel;
        private int position;
        private long mLastClickTime = 0;

        StructListHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemNameTv = view.findViewById(R.id.itemNameTv);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    dynamicBoxListener.onReturn(dynamicBoxModel, ItemProcessEnum.SELECTED);
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
        }

        private void setBoxWidth() {
            int width = CommonUtils.getScreenWidth(context)
                    - CommonUtils.getPaddingInPixels(context, 16f);

            LinearLayout.LayoutParams params;

            if(maxBoxExceeded){
                params = new LinearLayout.LayoutParams(width / 4
                        - CommonUtils.getPaddingInPixels(context, 10f),
                        (int)context.getResources().getDimension(R.dimen.dynamic_box_heigth));
            }else {
                params = new LinearLayout.LayoutParams(width / 4,
                        (int)context.getResources().getDimension(R.dimen.dynamic_box_heigth));
            }

            int margin = CommonUtils.getPaddingInPixels(context, 2f);
            params.setMargins(margin, margin, margin, margin);
            itemCv.setLayoutParams(params);
        }

        private void setItemName() {
            if(dynamicBoxModel.getStructId() == 0){
                itemNameTv.setText("+");
            }else {
                if(dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()){
                    Discount discount = DiscountDBHelper.getDiscount(dynamicBoxModel.getItemId());
                    itemNameTv.setText(discount.getName());
                    setEnabilityOfDiscount(discount);
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                    Product product = ProductDBHelper.getProduct(dynamicBoxModel.getItemId());
                    itemNameTv.setText(product.getName());
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()){
                    Category category = CategoryDBHelper.getCategory(dynamicBoxModel.getItemId());
                    itemNameTv.setText(category.getName());
                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.TAX_SET.getId()){

                    if(dynamicBoxModel.getItemId() < 0){
                        TaxRateEnum taxRateEnum = TaxRateEnum.getById(dynamicBoxModel.getItemId());
                        itemNameTv.setText(Objects.requireNonNull(taxRateEnum).getLabel());

                    }else {
                        TaxModel taxModel = TaxDBHelper.getTax(dynamicBoxModel.getItemId());
                        itemNameTv.setText(taxModel.getName());
                    }

                }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){
                    if(dynamicBoxModel.getItemId() < 0){
                        PaymentTypeEnum paymentType = PaymentTypeEnum.getById(dynamicBoxModel.getItemId());
                        itemNameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ?
                                Objects.requireNonNull(paymentType).getLabelTr() : Objects.requireNonNull(paymentType).getLabelEn());
                    }
                }
            }
        }

        private void setEnabilityOfDiscount(Discount discount) {
            if(SaleModelInstance.getInstance().getSaleModel().isDiscountInSale(discount)){
                itemCv.setEnabled(false);
                itemNameTv.setTextColor(context.getResources().getColor(R.color.Gray, null));
            }else {
                itemCv.setEnabled(true);
                itemNameTv.setTextColor(context.getResources().getColor(R.color.Black, null));
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