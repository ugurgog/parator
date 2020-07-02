package com.paypad.vuk507.charge.dynamicStruct.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class DynamicPaymentSelectAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<PaymentTypeEnum> paymentTypes;
    private List<PaymentTypeEnum> orgPaymentTypes;
    private ReturnPaymentCallback returnPaymentCallback;
    private ProcessDirectionEnum directionType;

    public DynamicPaymentSelectAdapter(Context context, ProcessDirectionEnum directionType, List<PaymentTypeEnum> paymentTypes, ReturnPaymentCallback returnPaymentCallback) {
        this.context = context;
        this.paymentTypes = paymentTypes;
        this.returnPaymentCallback = returnPaymentCallback;
        this.directionType = directionType;
    }

    public void setOrgPaymentTypes(List<PaymentTypeEnum> orgPaymentTypes) {
        this.orgPaymentTypes = orgPaymentTypes;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if(directionType == ProcessDirectionEnum.DIRECTION_FAST_MENU){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_item_list, parent, false);
            return new DynamicPaymentSelectAdapter.FastMenuHolder(view);
        }else if(directionType == ProcessDirectionEnum.DIRECTION_PAYMENT_SELECT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_select, parent, false);
            return new DynamicPaymentSelectAdapter.PaymentHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FastMenuHolder) {
            PaymentTypeEnum paymentType = paymentTypes.get(position);
            ((FastMenuHolder) holder).setData(paymentType, position);
        } else {
            PaymentTypeEnum paymentType = paymentTypes.get(position);
            ((PaymentHolder) holder).setData(paymentType, position);
        }
    }

    @Override
    public int getItemCount() {
        return paymentTypes.size();
    }

    class FastMenuHolder extends RecyclerView.ViewHolder {
        private LinearLayout structItemll;
        private TextView colorfulTv;
        private TextView itemNameTv;

        int position;
        PaymentTypeEnum paymentType;

        FastMenuHolder(View itemView) {
            super(itemView);
            structItemll = itemView.findViewById(R.id.structItemll);
            colorfulTv = itemView.findViewById(R.id.colorfulTv);
            itemNameTv = itemView.findViewById(R.id.itemNameTv);

            structItemll.setOnClickListener(v -> {
                returnPaymentCallback.onReturn(paymentType);
            });
        }

        void setData(PaymentTypeEnum paymentType, int position) {
            this.paymentType = paymentType;
            this.position = position;
            itemNameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? paymentType.getLabelTr() : paymentType.getLabelEn());
            setItemColor();
        }

        private void setItemColor() {
            int colorCode = CommonUtils.getDarkRandomColor(context);
            GradientDrawable imageShape = ShapeUtil.getShape(context.getResources().getColor(colorCode, null),
                    context.getResources().getColor(colorCode, null),
                    GradientDrawable.OVAL, 50, 0);
            colorfulTv.setBackground(imageShape);
        }
    }

    class PaymentHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemll;
        private TextView itemNameTv;

        int position;
        PaymentTypeEnum paymentType;

        PaymentHolder(View itemView) {
            super(itemView);
            itemll = itemView.findViewById(R.id.itemll);
            itemNameTv = itemView.findViewById(R.id.itemNameTv);

            itemll.setOnClickListener(v -> {
                returnPaymentCallback.onReturn(paymentType);
            });
        }

        void setData(PaymentTypeEnum paymentType, int position) {
            this.paymentType = paymentType;
            this.position = position;
            itemNameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? paymentType.getLabelTr() : paymentType.getLabelEn());
        }
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            paymentTypes = orgPaymentTypes;
        } else {

            List<PaymentTypeEnum> tempPayments = new ArrayList<>();

            for (PaymentTypeEnum disc : orgPaymentTypes) {

                if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
                    if(disc.getLabelTr().toLowerCase().contains(searchText.toLowerCase()))
                        tempPayments.add(disc);
                }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
                    if(disc.getLabelEn().toLowerCase().contains(searchText.toLowerCase()))
                        tempPayments.add(disc);
                }
            }
            paymentTypes = tempPayments;
        }

        this.notifyDataSetChanged();

        if (paymentTypes != null)
            returnSizeCallback.OnReturn(paymentTypes.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}