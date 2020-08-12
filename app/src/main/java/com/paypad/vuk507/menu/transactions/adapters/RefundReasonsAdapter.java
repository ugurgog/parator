package com.paypad.vuk507.menu.transactions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.ItemsEnum;
import com.paypad.vuk507.enums.RefundReasonEnum;
import com.paypad.vuk507.interfaces.RefundReasonTypeCallback;
import com.paypad.vuk507.interfaces.ReturnOrderItemCallback;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class RefundReasonsAdapter extends RecyclerView.Adapter<RefundReasonsAdapter.RefundReasonHolder> {

    private Context context;
    private RefundReasonEnum[] values;
    private RefundReasonTypeCallback refundReasonTypeCallback;
    private int previousSelectedPosition = -1;
    private RefundReasonEnum selectedReasonType;

    public RefundReasonsAdapter(Context context) {
        this.context = context;
        this.values = RefundReasonEnum.values();
    }

    public void setRefundReasonTypeCallback(RefundReasonTypeCallback refundReasonTypeCallback) {
        this.refundReasonTypeCallback = refundReasonTypeCallback;
    }

    @NonNull
    @Override
    public RefundReasonsAdapter.RefundReasonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_refund_item, parent, false);
        return new RefundReasonsAdapter.RefundReasonHolder(itemView);
    }

    public class RefundReasonHolder extends RecyclerView.ViewHolder {

        private CardView saleItemCv;
        private TextView itemNameTv;
        private TextView itemAmountTv;
        private CheckBox checkb;

        int position;
        private RefundReasonEnum refundReasonType;

        public RefundReasonHolder(View view) {
            super(view);

            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
            checkb = view.findViewById(R.id.checkb);
            saleItemCv = view.findViewById(R.id.saleItemCv);

            itemAmountTv.setVisibility(View.GONE);

            saleItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageSelectedItem();
                    refundReasonTypeCallback.OnRefundReasonReturn(refundReasonType);
                }
            });

            checkb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageSelectedItem();
                    if(checkb.isChecked())
                        refundReasonTypeCallback.OnRefundReasonReturn(refundReasonType);
                }
            });
        }

        public void setData(RefundReasonEnum refundReasonType, int position) {
            this.refundReasonType = refundReasonType;
            this.position = position;
            updateCb();
            setItemName();
        }

        private void manageSelectedItem() {
            selectedReasonType = refundReasonType;
            notifyItemChanged(position);

            if (previousSelectedPosition > -1)
                notifyItemChanged(previousSelectedPosition);

            previousSelectedPosition = position;
        }

        private void updateCb() {
            if (selectedReasonType != null && refundReasonType != null) {

                if (selectedReasonType == refundReasonType)
                    checkb.setChecked(true);
                else
                    checkb.setChecked(false);
            }
        }

        private void setItemName(){
            itemNameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? refundReasonType.getLabelTr() : refundReasonType.getLabelEn());
        }
    }

    @Override
    public void onBindViewHolder(final RefundReasonsAdapter.RefundReasonHolder holder, final int position) {
        RefundReasonEnum refundReasonType = values[position];
        holder.setData(refundReasonType, position);
    }

    @Override
    public int getItemCount() {
        if(values != null)
            return values.length;
        else
            return 0;
    }
}