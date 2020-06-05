package com.paypad.vuk507.menu.unit;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;

import java.util.ArrayList;
import java.util.List;

public class UnitListAdapter extends RecyclerView.Adapter<UnitListAdapter.UnitHolder> {

    private Context context;
    private List<UnitModel> unitModels = new ArrayList<>();
    private List<UnitModel> orgUnitModels = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnUnitCallback returnUnitCallback;

    public UnitListAdapter(Context context, List<UnitModel> unitModels,
                               BaseFragment.FragmentNavigation fragmentNavigation,
                               ReturnUnitCallback returnUnitCallback) {
        this.context = context;
        this.unitModels.addAll(unitModels);
        this.orgUnitModels.addAll(unitModels);
        this.fragmentNavigation = fragmentNavigation;
        this.returnUnitCallback = returnUnitCallback;
    }

    @Override
    public UnitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit_list, parent, false);
        return new UnitHolder(itemView);
    }

    public class UnitHolder extends RecyclerView.ViewHolder {

        CardView unitItemCv;
        TextView unitNameTv;
        UnitModel unitModel;

        int position;

        public UnitHolder(View view) {
            super(view);

            unitItemCv = view.findViewById(R.id.unitItemCv);
            unitNameTv = view.findViewById(R.id.unitNameTv);

            unitItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentNavigation.pushFragment(new UnitEditFragment(unitModel, new ReturnUnitCallback() {
                        @Override
                        public void OnReturn(UnitModel unitModel) {
                            if(unitModel != null){
                                unitModels.set(position, unitModel);
                                unitChangedResult(position);
                            }
                        }
                    }));
                }
            });
        }

        public void setData(UnitModel unitModel, int position) {
            this.unitModel = unitModel;
            this.position = position;
            unitNameTv.setText(unitModel.getName());
        }
    }

    public void unitRemoveResult(int position){
        unitModels.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
        //this.notifyDataSetChanged();
    }

    public void unitChangedResult(int position){
        this.notifyItemChanged(position);
    }

    public void addUnit(UnitModel unitModel){
        if(unitModels != null && unitModel != null){
            unitModels.add(unitModel);
            this.notifyItemInserted(unitModels.size() - 1);
            //this.notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final UnitHolder holder, final int position) {
        UnitModel unitModel = unitModels.get(position);
        holder.setData(unitModel, position);
    }

    @Override
    public int getItemCount() {
        if(unitModels != null)
            return unitModels.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            unitModels = orgUnitModels;
        } else {

            List<UnitModel> tempUnitList = new ArrayList<>();
            for (UnitModel unit : orgUnitModels) {
                if (unit.getName() != null &&
                        unit.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempUnitList.add(unit);
            }
            unitModels = tempUnitList;
        }

        this.notifyDataSetChanged();

        if (unitModels != null)
            returnSizeCallback.OnReturn(unitModels.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}