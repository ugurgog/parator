package com.paypad.vuk507.menu.transactions.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class ItemsServicesAdapter extends RecyclerView.Adapter<ItemsServicesAdapter.SaleItemHolder> {

    private Context context;
    private List<SaleItem> saleItems = new ArrayList<>();

    public ItemsServicesAdapter(Context context, List<SaleItem> saleItems) {
        this.context = context;
        this.saleItems.addAll(saleItems);
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
        private SaleItem saleItem;

        public SaleItemHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemImgv = view.findViewById(R.id.itemImgv);
            shortNameTv = view.findViewById(R.id.shortNameTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemNoteTv = view.findViewById(R.id.itemNoteTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
        }

        public void setData(SaleItem saleItem, int position) {
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
                if(saleItem.getQuantity() > 1)
                    itemNameTv.setText(saleItem.getName().concat(" x ").concat(String.valueOf(saleItem.getQuantity())));
                else
                    itemNameTv.setText(saleItem.getName());
            }
        }

        private void setItemNote(){
            if(saleItem.getNote() != null)
                itemNoteTv.setText(saleItem.getNote());
        }

        private void setItemAmount(){
            String amountStr = CommonUtils.getDoubleStrValueForView(saleItem.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            itemAmountTv.setText(amountStr);
        }
    }

    @Override
    public void onBindViewHolder(final ItemsServicesAdapter.SaleItemHolder holder, final int position) {
        SaleItem saleItem = saleItems.get(position);
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