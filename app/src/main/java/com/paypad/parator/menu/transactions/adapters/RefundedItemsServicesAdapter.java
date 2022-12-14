package com.paypad.parator.menu.transactions.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.R;
import com.paypad.parator.model.OrderRefundItem;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class RefundedItemsServicesAdapter extends RecyclerView.Adapter<RefundedItemsServicesAdapter.SaleItemHolder> {

    private Context context;
    private List<OrderRefundItem> saleItems = new ArrayList<>();

    public RefundedItemsServicesAdapter(Context context, List<OrderRefundItem> saleItems) {
        this.context = context;
        this.saleItems.addAll(saleItems);
    }

    @NonNull
    @Override
    public RefundedItemsServicesAdapter.SaleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_saleitem_list, parent, false);
        return new RefundedItemsServicesAdapter.SaleItemHolder(itemView);
    }

    public class SaleItemHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private ImageView itemImgv;
        private TextView shortNameTv;
        private TextView itemNameTv;
        private TextView itemNoteTv;
        private TextView itemAmountTv;

        int position;
        private OrderRefundItem saleItem;

        public SaleItemHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemImgv = view.findViewById(R.id.itemImgv);
            shortNameTv = view.findViewById(R.id.shortNameTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemNoteTv = view.findViewById(R.id.itemNoteTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
        }

        public void setData(OrderRefundItem saleItem, int position) {
            this.saleItem = saleItem;
            this.position = position;
            setItemTile();
            setItemName();
            setItemNote();
            setItemAmount();
        }

        private void setItemTile() {
            shortNameTv.setText(DataUtils.getProductNameShortenName(saleItem.getName()));

            if(saleItem.getItemImage() != null){
                shortNameTv.setText("");
                itemImgv.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = BitmapFactory.decodeByteArray(saleItem.getItemImage(), 0, saleItem.getItemImage().length);
                        itemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, itemImgv.getWidth(),
                                itemImgv.getHeight(), false));
                    }
                });
            }else if(saleItem.getColorId() != 0){
                itemImgv.setBackgroundColor(context.getResources().getColor(saleItem.getColorId(), null));
            }else {
                itemImgv.setBackgroundColor(CommonUtils.getItemColors()[0]);
            }
        }

        private void setItemName(){
            if(saleItem.getName() != null){
                itemNameTv.setText(saleItem.getName());
            }
        }

        private void setItemNote(){
            if(saleItem.getNote() != null)
                itemNoteTv.setText(saleItem.getNote());
        }

        private void setItemAmount(){
            double itemAmount = CommonUtils.round(saleItem.getAmount() , 2);
            String amountStr = CommonUtils.getDoubleStrValueForView(itemAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            itemAmountTv.setText(amountStr);
        }
    }

    @Override
    public void onBindViewHolder(final RefundedItemsServicesAdapter.SaleItemHolder holder, final int position) {
        OrderRefundItem saleItem = saleItems.get(position);
        holder.setData(saleItem, position);
    }

    @Override
    public int getItemCount() {
        if(saleItems != null)
            return saleItems.size();
        else
            return 0;
    }
}