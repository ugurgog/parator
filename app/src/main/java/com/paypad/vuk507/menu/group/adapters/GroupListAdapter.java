package com.paypad.vuk507.menu.group.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupHolder> {

    private List<Group> groups = new ArrayList<>();
    private List<Group> orgGroups = new ArrayList<>();

    private ReturnGroupCallback returnGroupCallback;

    public GroupListAdapter(List<Group> groups, ReturnGroupCallback returnGroupCallback) {
        this.groups.addAll(groups);
        this.orgGroups.addAll(groups);
        this.returnGroupCallback = returnGroupCallback;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_list_no_pic_with_arrow, parent, false);
        return new GroupHolder(itemView);
    }

    public class GroupHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private TextView nameTv;

        private Group group;
        private int position;

        public GroupHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            nameTv = view.findViewById(R.id.nameTv);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnGroupCallback.OnGroupReturn(group, ItemProcessEnum.SELECTED);
                }
            });
        }

        public void setData(Group group, int position) {
            this.group = group;
            this.position = position;
            nameTv.setText(group.getName());
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

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            groups = orgGroups;
        } else {

            List<Group> tempGroupList = new ArrayList<>();

            for (Group grp : orgGroups) {
                if (grp.getName() != null && grp.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempGroupList.add(grp);
            }
            groups = tempGroupList;
        }

        notifyDataSetChanged();

        if (groups != null)
            returnSizeCallback.OnReturn(groups.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}