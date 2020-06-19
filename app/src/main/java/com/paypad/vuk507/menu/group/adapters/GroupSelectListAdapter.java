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
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.model.Group;

import java.util.ArrayList;
import java.util.List;


public class GroupSelectListAdapter extends RecyclerView.Adapter<GroupSelectListAdapter.GroupHolder> {

    private List<Group> groups = new ArrayList<>();
    private List<Group> orgGroups = new ArrayList<>();
    private Group selectedGroup;
    private ReturnGroupCallback returnGroupCallback;
    private int previousSelectedPosition = -1;

    public GroupSelectListAdapter(List<Group> groups, ReturnGroupCallback returnGroupCallback) {
        this.groups.addAll(groups);
        this.orgGroups.addAll(groups);
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
                    manageSelectedItem();
                    returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.SELECTED);
                }
            });

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkbox.isChecked())
                        returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.SELECTED);
                }
            });
        }

        public void setData(Group group, int position) {
            this.group = group;
            this.position = position;
            nameTv.setText(group.getName());
            updateCb();
        }

        private void manageSelectedItem() {
            selectedGroup = group;
            notifyItemChanged(position);

            if (previousSelectedPosition > -1)
                notifyItemChanged(previousSelectedPosition);

            previousSelectedPosition = position;
        }

        public void updateCb() {
            if (selectedGroup != null && group != null) {
                if (selectedGroup.getId() == group.getId())
                    checkbox.setChecked(true);
                else
                    checkbox.setChecked(false);
            }
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