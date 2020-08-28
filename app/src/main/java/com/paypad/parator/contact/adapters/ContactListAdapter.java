package com.paypad.parator.contact.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.R;
import com.paypad.parator.contact.interfaces.ReturnContactListener;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.model.pojo.Contact;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactHolder> {

    private Context context;
    private List<Contact> contacts = new ArrayList<>();
    private List<Contact> orgContacts = new ArrayList<>();
    private ReturnContactListener returnContactListener;

    public ContactListAdapter(Context context, List<Contact> contacts, ReturnContactListener returnContactListener) {
        this.context = context;
        this.contacts.addAll(contacts);
        this.orgContacts.addAll(contacts);
        this.returnContactListener = returnContactListener;
    }

    @Override
    public ContactListAdapter.ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_list, parent, false);
        return new ContactListAdapter.ContactHolder(itemView);
    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        private CardView customerItemCv;
        private TextView shortCustomerNameTv;
        private TextView customerNameTv;
        private TextView customerInfoTv;

        private Contact contact;
        int position;

        public ContactHolder(View view) {
            super(view);

            customerItemCv = view.findViewById(R.id.customerItemCv);
            shortCustomerNameTv = view.findViewById(R.id.shortCustomerNameTv);
            customerNameTv = view.findViewById(R.id.customerNameTv);
            customerInfoTv = view.findViewById(R.id.customerInfoTv);

            customerItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnContactListener.OnReturn(contact);
                }
            });
        }

        public void setData(Contact contact, int position) {
            this.contact = contact;
            this.position = position;
            customerNameTv.setText(contact.getName());
            customerInfoTv.setText(contact.getPhoneNumber().trim());
            setCustomerShortName();
            setShortenNameColor();
        }

        private void setShortenNameColor() {
            int colorCode = CommonUtils.getDarkRandomColor(context);

            if(contact.getColorId() != 0)
                colorCode = contact.getColorId();

            GradientDrawable imageShape = ShapeUtil.getShape(context.getResources().getColor(colorCode, null),
                    context.getResources().getColor(colorCode, null),
                    GradientDrawable.OVAL, 50, 0);
            shortCustomerNameTv.setBackground(imageShape);
        }

        private void setCustomerShortName() {
            shortCustomerNameTv.setText(DataUtils.getContactShortName(contact.getName()));
        }
    }

    @Override
    public void onBindViewHolder(final ContactListAdapter.ContactHolder holder, final int position) {
        Contact contact = contacts.get(position);
        holder.setData(contact, position);
    }

    @Override
    public int getItemCount() {
        if(contacts != null)
            return contacts.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            contacts = orgContacts;
        } else {

            List<Contact> tempContactList = new ArrayList<>();
            for (Contact cont : orgContacts) {
                if (cont.getName() != null &&  cont.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempContactList.add(cont);
            }
            contacts = tempContactList;
        }

        this.notifyDataSetChanged();

        if (contacts != null)
            returnSizeCallback.OnReturn(contacts.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}