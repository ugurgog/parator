package com.paypad.parator.menu.settings.passcode.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.PasscodeTimeoutEnum;
import com.paypad.parator.interfaces.PasscodeTimeoutCallback;
import com.paypad.parator.interfaces.PasscodeTypeCallback;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.category.CategoryEditFragment;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.model.Category;

import java.util.ArrayList;
import java.util.List;

public class PasscodeTimeoutSelectAdapter extends RecyclerView.Adapter<PasscodeTimeoutSelectAdapter.TimeoutHolder> {

    private PasscodeTimeoutCallback passcodeTimeoutCallback;
    private PasscodeTimeoutEnum[] passcodeTimeoutEnums;
    private PasscodeTimeoutEnum selected;
    private int previousSelectedPosition = -1;
    private Context mContext;

    public PasscodeTimeoutSelectAdapter(Context context, PasscodeTimeoutEnum passcodeTimeoutType, PasscodeTimeoutCallback passcodeTimeoutCallback) {
        mContext = context;
        this.selected = passcodeTimeoutType;
        this.passcodeTimeoutCallback = passcodeTimeoutCallback;
        passcodeTimeoutEnums = PasscodeTimeoutEnum.values();
    }

    @NonNull
    @Override
    public PasscodeTimeoutSelectAdapter.TimeoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_passcode_timeout, parent, false);
        return new PasscodeTimeoutSelectAdapter.TimeoutHolder(itemView);
    }

    public class TimeoutHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private TextView itemTv;
        private CheckBox checkbox;

        private PasscodeTimeoutEnum passcodeTimeoutEnum;

        int position;

        public TimeoutHolder(View view) {
            super(view);

            itemTv = view.findViewById(R.id.itemTv);
            itemCv = view.findViewById(R.id.itemCv);
            checkbox = view.findViewById(R.id.checkbox);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageSelectedItem();
                    passcodeTimeoutCallback.OnTimeoutReturn(passcodeTimeoutEnum);
                }
            });

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageSelectedItem();
                    passcodeTimeoutCallback.OnTimeoutReturn(passcodeTimeoutEnum);
                    //if(checkbox.isChecked())
                    //    passcodeTimeoutCallback.OnTimeoutReturn(passcodeTimeoutEnum);
                }
            });
        }

        public void setData(PasscodeTimeoutEnum passcodeTimeoutEnum, int position) {
            this.passcodeTimeoutEnum = passcodeTimeoutEnum;
            this.position = position;
            setItemName();
            updateCb();
        }

        private void setItemName() {
            if(passcodeTimeoutEnum.getId() == PasscodeTimeoutEnum.NEVER.getId())
                itemTv.setText(mContext.getResources().getString(R.string.never));
            else if(passcodeTimeoutEnum.getId() == PasscodeTimeoutEnum.SECOND_30.getId())
                itemTv.setText(mContext.getResources().getString(R.string.thirty_seconds));
            else if(passcodeTimeoutEnum.getId() == PasscodeTimeoutEnum.MINUTE_1.getId())
                itemTv.setText(mContext.getResources().getString(R.string.one_minute));
            else if(passcodeTimeoutEnum.getId() == PasscodeTimeoutEnum.MINUTE_5.getId())
                itemTv.setText(mContext.getResources().getString(R.string.five_minute));
        }

        private void manageSelectedItem() {
            selected = passcodeTimeoutEnum;
            notifyItemChanged(position);

            if (previousSelectedPosition > -1)
                notifyItemChanged(previousSelectedPosition);

            previousSelectedPosition = position;
        }

        public void updateCb() {
            if(selected.getId() == passcodeTimeoutEnum.getId()){
                previousSelectedPosition = position;
                checkbox.setChecked(true);
            } else
                checkbox.setChecked(false);
        }
    }

    @Override
    public void onBindViewHolder(final PasscodeTimeoutSelectAdapter.TimeoutHolder holder, final int position) {
        PasscodeTimeoutEnum passcodeTimeoutEnum = passcodeTimeoutEnums[position];
        holder.setData(passcodeTimeoutEnum, position);
    }

    @Override
    public int getItemCount() {
        if(passcodeTimeoutEnums != null)
            return passcodeTimeoutEnums.length;
        else
            return 0;
    }

}