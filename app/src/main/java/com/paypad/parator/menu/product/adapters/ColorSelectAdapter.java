package com.paypad.parator.menu.product.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.R;
import com.paypad.parator.menu.product.interfaces.ColorImageReturnCallback;

public class ColorSelectAdapter extends RecyclerView.Adapter<ColorSelectAdapter.ColorHolder> {

    private Context context;
    private int[] colors;
    private ColorImageReturnCallback colorReturnCallback;
    private Integer selectedColorId;
    private Integer mColorId;
    private int previousSelectedPosition = -1;

    public void setColorReturnCallback(ColorImageReturnCallback colorReturnCallback) {
        this.colorReturnCallback = colorReturnCallback;
    }

    public ColorSelectAdapter(Context context, int[] colors, int colorId) {
        this.context = context;
        this.colors = colors;
        this.mColorId = colorId;
    }

    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color_list, parent, false);
        return new ColorHolder(itemView);
    }

    public class ColorHolder extends RecyclerView.ViewHolder {

        RelativeLayout colorRl;
        ImageView checkImgv;
        ConstraintLayout colorCl;

        int colorId;
        int position;

        ColorHolder(View view) {
            super(view);

            colorRl = view.findViewById(R.id.colorRl);
            checkImgv = view.findViewById(R.id.checkImgv);
            colorCl = view.findViewById(R.id.colorCl);

            colorCl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageSelectedItem();
                    colorReturnCallback.OnColorReturn(colorId);
                }
            });
        }

        void setData(Integer colorId, int position) {
            this.colorId = colorId;
            this.position = position;
            colorRl.setBackgroundColor(context.getResources().getColor(colorId, null));
            updateColorSelection();
        }

        private void manageSelectedItem() {
            selectedColorId = colorId;
            notifyItemChanged(position);

            if (previousSelectedPosition > -1)
                notifyItemChanged(previousSelectedPosition);

            previousSelectedPosition = position;
        }

        public void updateColorSelection() {
            if (selectedColorId != null) {
                if (selectedColorId == colorId)
                    checkImgv.setVisibility(View.VISIBLE);
                else
                    checkImgv.setVisibility(View.GONE);
            }else {
                if(colorId == mColorId){
                    previousSelectedPosition = position;
                    checkImgv.setVisibility(View.VISIBLE);
                }
                else
                    checkImgv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(final ColorHolder holder, final int position) {
        Integer colorId = colors[position];
        holder.setData(colorId, position);
    }

    @Override
    public int getItemCount() {
        if(colors != null)
            return colors.length;
        else
            return 0;
    }
}