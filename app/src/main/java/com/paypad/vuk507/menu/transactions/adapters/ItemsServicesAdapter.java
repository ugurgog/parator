package com.paypad.vuk507.menu.transactions.adapters;

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

import com.paypad.vuk507.R;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class ItemsServicesAdapter extends RecyclerView.Adapter<ItemsServicesAdapter.SaleItemHolder> {

    private Context context;
    private List<OrderItem> orderItems = new ArrayList<>();

    public ItemsServicesAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems.addAll(orderItems);
    }

    @NonNull
    @Override
    public ItemsServicesAdapter.SaleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_saleitem_list, parent, false);
        return new ItemsServicesAdapter.SaleItemHolder(itemView);
    }

    public class SaleItemHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private ImageView itemImgv;
        private TextView shortNameTv;
        private TextView itemNameTv;
        private TextView itemNoteTv;
        private TextView itemAmountTv;

        int position;
        private OrderItem orderItem;

        public SaleItemHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemImgv = view.findViewById(R.id.itemImgv);
            shortNameTv = view.findViewById(R.id.shortNameTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemNoteTv = view.findViewById(R.id.itemNoteTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
        }

        public void setData(OrderItem orderItem, int position) {
            this.orderItem = orderItem;
            this.position = position;
            setItemTile();
            setItemName();
            setItemNote();
            setItemAmount();
        }

        private void setItemTile() {
            shortNameTv.setText(DataUtils.getProductNameShortenName(orderItem.getName()));

            if(orderItem.getItemImage() != null){
                shortNameTv.setText("");
                itemImgv.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = BitmapFactory.decodeByteArray(orderItem.getItemImage(), 0, orderItem.getItemImage().length);
                        itemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, itemImgv.getWidth(),
                                itemImgv.getHeight(), false));
                    }
                });
            }else if(orderItem.getColorId() != 0){
                itemImgv.setBackgroundColor(context.getResources().getColor(orderItem.getColorId(), null));
            }else {
                itemImgv.setBackgroundColor(CommonUtils.getItemColors()[0]);
            }
        }

        private void setItemName(){
            if(orderItem.getName() != null){
                if(orderItem.getQuantity() > 1)
                    itemNameTv.setText(orderItem.getName().concat(" x ").concat(String.valueOf(orderItem.getQuantity())));
                else
                    itemNameTv.setText(orderItem.getName());
            }
        }

        private void setItemNote(){
            if(orderItem.getNote() != null)
                itemNoteTv.setText(orderItem.getNote());
        }

        private void setItemAmount(){
            double itemAmount = CommonUtils.round(orderItem.getAmount() * orderItem.getQuantity() , 2);
            String amountStr = CommonUtils.getDoubleStrValueForView(itemAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            itemAmountTv.setText(amountStr);
        }
    }

    @Override
    public void onBindViewHolder(final ItemsServicesAdapter.SaleItemHolder holder, final int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.setData(orderItem, position);
    }

    @Override
    public int getItemCount() {
        if(orderItems != null)
            return orderItems.size();
        else
            return 0;
    }
}