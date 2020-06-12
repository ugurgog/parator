package com.paypad.vuk507.menu.tax.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.TaxModel;

import java.util.ArrayList;
import java.util.List;

public class TaxSelectListAdapter extends RecyclerView.Adapter<TaxSelectListAdapter.TaxHolder> {

    private Context context;
    private List<TaxModel> taxModels = new ArrayList<>();
    private List<TaxModel> orgTaxModels = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnTaxCallback returnTaxCallback;

    public TaxSelectListAdapter(Context context, List<TaxModel> taxModels,
                   BaseFragment.FragmentNavigation fragmentNavigation,
                   ReturnTaxCallback returnTaxCallback) {
        this.context = context;
        this.taxModels.addAll(taxModels);
        this.orgTaxModels.addAll(taxModels);
        this.fragmentNavigation = fragmentNavigation;
        this.returnTaxCallback = returnTaxCallback;
    }

    @NonNull
    @Override
    public TaxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tax_list, parent, false);
        return new TaxHolder(itemView);
    }

    public class TaxHolder extends RecyclerView.ViewHolder {

        CardView taxItemCv;
        TextView taxNameTv;
        TextView taxRateTv;

        TaxModel taxModel;
        int position;

        TaxHolder(View view) {
            super(view);

            taxItemCv = view.findViewById(R.id.taxItemCv);
            taxNameTv = view.findViewById(R.id.taxNameTv);
            taxRateTv = view.findViewById(R.id.taxRateTv);

            taxItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnTaxCallback.OnReturn(taxModel, ItemProcessEnum.SELECTED);
                }
            });
        }

        void setData(TaxModel taxModel, int position) {
            this.taxModel = taxModel;
            this.position = position;
            taxNameTv.setText(taxModel.getName());
            taxRateTv.setText("% ".concat(String.valueOf(taxModel.getTaxRate())));
        }
    }

    public void taxRemoveResult(int position){
        taxModels.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
        //this.notifyDataSetChanged();
    }

    public void taxChangedResult(int position){
        this.notifyItemChanged(position);
    }

    public void addTax(TaxModel taxModel){
        if(taxModels != null && taxModel != null){
            taxModels.add(taxModel);
            this.notifyItemInserted(taxModels.size() - 1);
            //this.notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final TaxHolder holder, final int position) {
        TaxModel taxModel = taxModels.get(position);
        holder.setData(taxModel, position);
    }

    @Override
    public int getItemCount() {
        if(taxModels != null)
            return taxModels.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            taxModels = orgTaxModels;
        } else {

            List<TaxModel> tempTaxList = new ArrayList<>();
            for (TaxModel tax : orgTaxModels) {
                if (tax.getName() != null &&
                        tax.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempTaxList.add(tax);
            }
            taxModels = tempTaxList;
        }

        this.notifyDataSetChanged();

        if (taxModels != null)
            returnSizeCallback.OnReturn(taxModels.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}
