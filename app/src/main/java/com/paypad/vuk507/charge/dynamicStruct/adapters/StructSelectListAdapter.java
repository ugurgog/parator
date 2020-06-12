package com.paypad.vuk507.charge.dynamicStruct.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.TaxModel;

import java.util.ArrayList;
import java.util.List;

public class StructSelectListAdapter extends RecyclerView.Adapter<StructSelectListAdapter.StructHolder> {

    private Context context;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private DynamicStructEnum[] structList;

    public StructSelectListAdapter(Context context, BaseFragment.FragmentNavigation fragmentNavigation) {
        this.context = context;
        this.fragmentNavigation = fragmentNavigation;
        this.structList = DynamicStructEnum.values();
    }

    @NonNull
    @Override
    public StructHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamic_struct_list, parent, false);
        return new StructHolder(itemView);
    }

    public class StructHolder extends RecyclerView.ViewHolder {

        private LinearLayout structItemll;
        private TextView structNameTv;

        DynamicStructEnum dynamicStructEnum;
        int position;

        StructHolder(View view) {
            super(view);

            structItemll = view.findViewById(R.id.structItemll);
            structNameTv = view.findViewById(R.id.structNameTv);

            structItemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*fragmentNavigation.pushFragment(new TaxEditFragment(taxModel, new ReturnTaxCallback() {
                        @Override
                        public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                            returnTaxCallback.OnReturn(taxModel, processEnum);
                        }
                    }));*/
                }
            });
        }

        void setData(DynamicStructEnum dynamicStructEnum, int position) {
            this.dynamicStructEnum = dynamicStructEnum;
            this.position = position;
        }
    }

    @Override
    public void onBindViewHolder(final StructHolder holder, final int position) {
        DynamicStructEnum dynamicStructEnum = structList[position];
        holder.setData(dynamicStructEnum, position);
    }

    @Override
    public int getItemCount() {
        if(structList != null)
            return structList.length;
        else
            return 0;
    }
}