package com.paypad.vuk507.menu.group.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.model.Group;

import java.util.ArrayList;
import java.util.List;

public class MultiGroupSelectListAdapter extends RecyclerView.Adapter<MultiGroupSelectListAdapter.GroupHolder> {

    private List<Group> groups = new ArrayList<>();
    private ReturnGroupCallback returnGroupCallback;
    private List<Group> selectedGroupList;

    public MultiGroupSelectListAdapter(List<Group> selectedGroupList, List<Group> groups, ReturnGroupCallback returnGroupCallback) {
        this.groups.addAll(groups);
        this.selectedGroupList = selectedGroupList;
        this.returnGroupCallback = returnGroupCallback;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_list_no_pic_with_cb, parent, false);
        return new GroupHolder(itemView);
    }

    public class GroupHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private CheckBox checkbox;
        private TextView nameTv;
        private TextView infoTv;

        private Group group;
        private int position;

        public GroupHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            nameTv = view.findViewById(R.id.nameTv);
            checkbox = view.findViewById(R.id.checkbox);
            infoTv = view.findViewById(R.id.infoTv);
            infoTv.setVisibility(View.GONE);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkbox.isChecked())
                        checkbox.setChecked(false);
                    else
                        checkbox.setChecked(true);

                    if(checkbox.isChecked())
                        returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.SELECTED);
                    else
                        returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.UNSELECTED);
                }
            });

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkbox.isChecked())
                        returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.SELECTED);
                    else
                        returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.UNSELECTED);
                }
            });
        }

        public void setData(Group group, int position) {
            this.group = group;
            this.position = position;
            nameTv.setText(group.getName());

            if(selectedGroupList.contains(group))
                checkbox.setChecked(true);
            else
                checkbox.setChecked(false);
        }

    }

    @Override
    public void onBindViewHolder(final GroupHolder holder, final int position) {
        Group group = groups.get(position);
        holder.setData(group, position);
    }

    @Override
    public int getItemCount() {
        if(groups != null)
            return groups.size();
        else
            return 0;
    }
}