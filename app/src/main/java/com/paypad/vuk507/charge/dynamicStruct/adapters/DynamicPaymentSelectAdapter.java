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

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class DynamicPaymentSelectAdapter extends RecyclerView.Adapter<DynamicPaymentSelectAdapter.ViewHolder> {

    private Context context;
    private List<PaymentTypeEnum> paymentTypes;
    private ReturnPaymentCallback returnPaymentCallback;

    public DynamicPaymentSelectAdapter(Context context, List<PaymentTypeEnum> paymentTypes, ReturnPaymentCallback returnPaymentCallback) {
        this.context = context;
        this.paymentTypes = paymentTypes;
        this.returnPaymentCallback = returnPaymentCallback;
    }

    @Override
    public DynamicPaymentSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_item_list, parent, false);
        return new DynamicPaymentSelectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DynamicPaymentSelectAdapter.ViewHolder holder, int position) {
        PaymentTypeEnum paymentType = paymentTypes.get(position);
        holder.setData(paymentType, position);
    }

    @Override
    public int getItemCount() {
        return paymentTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout structItemll;
        private TextView colorfulTv;
        private TextView itemNameTv;

        int position;
        PaymentTypeEnum paymentType;

        ViewHolder(View itemView) {
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
}