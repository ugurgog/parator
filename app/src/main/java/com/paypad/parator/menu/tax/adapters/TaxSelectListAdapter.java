package com.paypad.parator.menu.tax.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.tax.TaxEditFragment;
import com.paypad.parator.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.parator.model.TaxModel;

import java.util.ArrayList;
import java.util.List;

public class TaxSelectListAdapter extends RecyclerView.Adapter {

    private List<TaxModel> taxModels = new ArrayList<>();
    private List<TaxModel> orgTaxModels = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnTaxCallback returnTaxCallback;
    private ItemProcessEnum mProcessType;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_NONE = 1;

    public TaxSelectListAdapter(List<TaxModel> taxModels,
                                BaseFragment.FragmentNavigation fragmentNavigation,
                                ReturnTaxCallback returnTaxCallback,
                                ItemProcessEnum processType) {
        this.taxModels.addAll(taxModels);
        this.orgTaxModels.addAll(taxModels);
        this.fragmentNavigation = fragmentNavigation;
        this.returnTaxCallback = returnTaxCallback;
        mProcessType = processType;
    }

    @Override
    public int getItemViewType(int position) {
        if(taxModels.get(position).getId() != 0)
            return VIEW_ITEM;
        else
            return VIEW_NONE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == VIEW_ITEM){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_tax_list, parent, false);
            return new TaxHolder(itemView);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_custom_list_no_pic_with_arrow, parent, false);

            return new TaxNoItemHolder(v);
        }
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

                    if(mProcessType == ItemProcessEnum.SELECTED)
                        returnTaxCallback.OnReturn(taxModel, ItemProcessEnum.SELECTED);
                    else
                        if(taxModel.getId() > 0){
                            fragmentNavigation.pushFragment(new TaxEditFragment(taxModel, new ReturnTaxCallback() {
                                @Override
                                public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                                    returnTaxCallback.OnReturn(taxModel, processEnum);
                                }
                            }));
                        }
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

    public class TaxNoItemHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private ImageView arrowImgv;

        TaxModel taxModel;
        int position;

        TaxNoItemHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.nameTv);
            arrowImgv = view.findViewById(R.id.arrowImgv);
        }

        void setData(TaxModel taxModel, int position) {
            this.taxModel = taxModel;
            this.position = position;
            arrowImgv.setVisibility(View.GONE);
            nameTv.setText(taxModel.getName());

        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        TaxModel taxModel = taxModels.get(position);
        if (holder instanceof TaxHolder) {
            ((TaxHolder) holder).setData(taxModel, position);
        } else {
            ((TaxNoItemHolder) holder).setData(taxModel, position);
        }
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
