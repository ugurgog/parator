package com.paypad.vuk507.menu.transactions.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.vuk507.R;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.PaymentDetailModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class PaymentTotalAdapter extends RecyclerView.Adapter<PaymentTotalAdapter.SaleItemHolder> {

    private Context context;
    private List<PaymentDetailModel> paymentDetailModels = new ArrayList<>();

    public PaymentTotalAdapter(Context context, List<PaymentDetailModel> paymentDetailModels) {
        this.context = context;
        this.paymentDetailModels.addAll(paymentDetailModels);
    }

    @NonNull
    @Override
    public PaymentTotalAdapter.SaleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_saleitem_list, parent, false);
        return new PaymentTotalAdapter.SaleItemHolder(itemView);
    }

    public class SaleItemHolder extends RecyclerView.ViewHolder {

        private ImageView itemImgv;
        private TextView itemNameTv;
        private TextView itemDescTv;

        int position;
        private  PaymentDetailModel paymentDetailModel;

        public SaleItemHolder(View view) {
            super(view);

            itemImgv = view.findViewById(R.id.itemImgv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemDescTv = view.findViewById(R.id.itemAmountTv);
        }

        public void setData(PaymentDetailModel paymentDetailModel, int position) {
            this.paymentDetailModel = paymentDetailModel;
            this.position = position;
            setItemTile();
            setItemName();
            setItemDesc();
        }

        private void setItemTile() {
            itemImgv.setColorFilter(ContextCompat.getColor(context, R.color.White), android.graphics.PorterDuff.Mode.SRC_IN);

            int padding = CommonUtils.getPaddingInPixels(context, 15);
            itemImgv.setPadding(padding,padding,padding,padding);

            if(paymentDetailModel.getDrawableId() != 0)
                Glide.with(context).load(paymentDetailModel.getDrawableId()).into(itemImgv);
        }

        private void setItemName() {
            if (paymentDetailModel.getItemName() != null)
                itemNameTv.setText(paymentDetailModel.getItemName());
        }

        private void setItemDesc(){
            if(paymentDetailModel.getItemDesc() != null)
                itemDescTv.setText(paymentDetailModel.getItemDesc());

            if(paymentDetailModel.isDescBold()){
                itemDescTv.setTypeface(Typeface.DEFAULT_BOLD);
                itemDescTv.setTextColor(context.getResources().getColor(R.color.Black, null));
            } else{
                itemDescTv.setTypeface(Typeface.DEFAULT);
                itemDescTv.setTextColor(context.getResources().getColor(R.color.Gray, null));
            }
        }
    }

    @Override
    public void onBindViewHolder(final PaymentTotalAdapter.SaleItemHolder holder, final int position) {
        PaymentDetailModel paymentDetailModel = paymentDetailModels.get(position);
        holder.setData(paymentDetailModel, position);
    }

    @Override
    public int getItemCount() {
        if(paymentDetailModels != null)
            return paymentDetailModels.size();
        else
            return 0;
    }
}