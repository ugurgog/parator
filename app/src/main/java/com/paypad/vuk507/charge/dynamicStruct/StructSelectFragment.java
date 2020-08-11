package com.paypad.vuk507.charge.dynamicStruct;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.DynamicStructEnum;


public class StructSelectFragment extends BottomSheetDialogFragment {

    public StructSelectFragment() {

    }

    private StructSelectListener selectListener;

    public interface StructSelectListener {
        void onStructClick(DynamicStructEnum dynamicStructEnum, boolean fromCategory);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_struct_select, null);

        dialog.setContentView(contentView);

        View parent = ((View) contentView.getParent());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        parent.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        RecyclerView structRv = contentView.findViewById(R.id.structRv);
        ImageButton closeImgBtn = contentView.findViewById(R.id.closeImgBtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        structRv.setLayoutManager(linearLayoutManager);

        StructSelectAdapter structSelectAdapter = new StructSelectAdapter();
        structRv.setAdapter(structSelectAdapter);

        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setStructListener(StructSelectListener listener) {
        selectListener = listener;
    }

    public class StructSelectAdapter extends RecyclerView.Adapter<StructSelectAdapter.ViewHolder> {

        DynamicStructEnum[] dynamicStructEnums;

        public StructSelectAdapter() {
            this.dynamicStructEnums = DynamicStructEnum.values();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_struct_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //holder.structNameTv.setText(emojisList.get(position));
            holder.dynamicStructEnum = dynamicStructEnums[position];
            holder.structNameTv.setText(dynamicStructEnums[position].getLabelEn());
        }

        @Override
        public int getItemCount() {
            return dynamicStructEnums.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView structNameTv;
            LinearLayout structItemll;

            DynamicStructEnum dynamicStructEnum;

            ViewHolder(View itemView) {
                super(itemView);
                structNameTv = itemView.findViewById(R.id.structNameTv);
                structItemll = itemView.findViewById(R.id.structItemll);

                itemView.setOnClickListener(v -> {
                    if (selectListener != null) {
                        selectListener.onStructClick(dynamicStructEnum, false);
                    }
                    dismiss();
                });
            }
        }
    }
}