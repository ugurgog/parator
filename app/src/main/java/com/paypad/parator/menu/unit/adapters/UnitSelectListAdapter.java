package com.paypad.parator.menu.unit.adapters;

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
import com.paypad.parator.interfaces.ClickCallback;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.unit.UnitEditFragment;
import com.paypad.parator.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.parator.model.UnitModel;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class UnitSelectListAdapter extends RecyclerView.Adapter {

    private List<UnitModel> unitModels = new ArrayList<>();
    private List<UnitModel> orgUnitModels = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnUnitCallback returnUnitCallback;
    private ItemProcessEnum processType;
    private ClickCallback clickCallback;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_NONE = 1;

    public UnitSelectListAdapter(List<UnitModel> unitModels,
                                 BaseFragment.FragmentNavigation fragmentNavigation,
                                 ItemProcessEnum processType,
                                ReturnUnitCallback returnUnitCallback) {
        this.unitModels.addAll(unitModels);
        this.orgUnitModels.addAll(unitModels);
        this.returnUnitCallback = returnUnitCallback;
        this.processType = processType;
        this.fragmentNavigation = fragmentNavigation;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    public int getItemViewType(int position) {
        if(unitModels.get(position).getId() != 0)
            return VIEW_ITEM;
        else
            return VIEW_NONE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_ITEM){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_unit_list, parent, false);
            return new UnitHolder(itemView);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_custom_list_no_pic_with_arrow, parent, false);
            return new UnitNoItemHolder(v);
        }
    }

    public class UnitNoItemHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private ImageView arrowImgv;

        UnitModel unitModel;
        int position;

        UnitNoItemHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.nameTv);
            arrowImgv = view.findViewById(R.id.arrowImgv);
        }

        void setData(UnitModel unitModel, int position) {
            this.unitModel = unitModel;
            this.position = position;
            arrowImgv.setVisibility(View.GONE);
            nameTv.setText(unitModel.getName());
        }
    }

    public class UnitHolder extends RecyclerView.ViewHolder{

        CardView unitItemCv;
        TextView unitNameTv;
        UnitModel unitModel;

        int position;

        UnitHolder(View view) {
            super(view);

            unitItemCv = view.findViewById(R.id.unitItemCv);
            unitNameTv = view.findViewById(R.id.unitNameTv);

            unitItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(processType == ItemProcessEnum.SELECTED)
                        returnUnitCallback.OnReturn(unitModel, ItemProcessEnum.SELECTED);
                    else{
                        if(unitModel.getId() > 0){

                            if(clickCallback != null)
                                clickCallback.OnClicked();

                            fragmentNavigation.pushFragment(new UnitEditFragment(unitModel, WALK_THROUGH_END,new ReturnUnitCallback() {
                                @Override
                                public void OnReturn(UnitModel unitModel, ItemProcessEnum processEnum) {
                                    returnUnitCallback.OnReturn(unitModel, processEnum);
                                }
                            }));
                        }
                    }
                }
            });
        }

        public void setData(UnitModel unitModel, int position) {
            this.unitModel = unitModel;
            this.position = position;
            unitNameTv.setText(unitModel.getName());
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        UnitModel unitModel = unitModels.get(position);
        if (holder instanceof UnitHolder) {
            ((UnitHolder) holder).setData(unitModel, position);
        } else {
            ((UnitNoItemHolder) holder).setData(unitModel, position);
        }
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