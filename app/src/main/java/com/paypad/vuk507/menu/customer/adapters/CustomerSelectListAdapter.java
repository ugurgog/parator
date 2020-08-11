package com.paypad.vuk507.menu.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerSelectListAdapter extends RecyclerView.Adapter<CustomerSelectListAdapter.CustomerHolder> {

    private Context context;
    private List<Customer> customers = new ArrayList<>();
    private List<Customer> orgCustomers = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnCustomerCallback returnCustomerCallback;
    private List<Customer> selectedCustomerList;
    private boolean selectAllChecked = false;

    public CustomerSelectListAdapter(Context context, List<Customer> customers, List<Customer> selectedCustomerList,
                               BaseFragment.FragmentNavigation fragmentNavigation,
                               ReturnCustomerCallback returnCustomerCallback) {
        this.context = context;
        this.customers.addAll(customers);
        this.orgCustomers.addAll(customers);
        this.selectedCustomerList = selectedCustomerList;
        this.fragmentNavigation = fragmentNavigation;
        this.returnCustomerCallback = returnCustomerCallback;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_list_no_pic_with_cb, parent, false);
        return new CustomerHolder(itemView);
    }

    public void setSelectedCustomerList(List<Customer> selectedCustomerList) {
        this.selectedCustomerList = selectedCustomerList;
    }

    public void setSelectAllChecked(boolean selectAllChecked) {
        this.selectAllChecked = selectAllChecked;
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private TextView nameTv;
        private TextView infoTv;
        private CheckBox checkbox;

        private Customer customer;
        private int position;

        public CustomerHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            nameTv = view.findViewById(R.id.nameTv);
            infoTv = view.findViewById(R.id.infoTv);
            checkbox = view.findViewById(R.id.checkbox);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkbox.isChecked())
                        checkbox.setChecked(false);
                    else
                        checkbox.setChecked(true);

                    if(checkbox.isChecked())
                        returnCustomerCallback.OnReturn(customer, ItemProcessEnum.SELECTED);
                    else
                        returnCustomerCallback.OnReturn(customer, ItemProcessEnum.UNSELECTED);
                }
            });

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkbox.isChecked())
                        returnCustomerCallback.OnReturn(customer, ItemProcessEnum.SELECTED);
                    else
                        returnCustomerCallback.OnReturn(customer, ItemProcessEnum.UNSELECTED);
                }
            });
        }

        public void setData(Customer customer, int position) {
            this.customer = customer;
            this.position = position;

            setCustomerName();
            setCustomerInfo();
            setCheckboxValue();
        }

        private void setCheckboxValue() {
            if(selectedCustomerList.size() > 0 && (selectedCustomerList.size() != customers.size())){
                boolean exist = false;
                for(Customer customer1 : selectedCustomerList){
                    if(customer.getId() == customer1.getId()){
                        exist = true;
                        break;
                    }
                }

                if(exist)
                    checkbox.setChecked(true);
                else
                    checkbox.setChecked(false);
            }else {
                if(selectAllChecked){
                    checkbox.setChecked(true);
                }else
                    checkbox.setChecked(false);
            }
        }

        private void setCustomerName() {
            String fullName = (customer.getName() != null ? customer.getName() : "")
                    .concat(" ")
                    .concat(customer.getSurname() != null ? customer.getSurname() : "").trim();
            nameTv.setText(fullName);
        }

        private void setCustomerInfo() {
            String customerInfo = "";
            if(customer.getEmail() != null && !customer.getEmail().isEmpty())
                customerInfo = customer.getEmail();

            if(customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty()){
                if(!customerInfo.isEmpty())
                    customerInfo = customerInfo.concat(" | ").concat(customer.getPhoneNumber());
                else
                    customerInfo = customer.getPhoneNumber();
            }

            if(customerInfo.trim().isEmpty()){
                infoTv.setVisibility(View.GONE);
            }else {
                infoTv.setVisibility(View.VISIBLE);
                infoTv.setText(customerInfo);
            }
        }
    }

    @Override
    public void onBindViewHolder(final CustomerHolder holder, final int position) {
        Customer customer = customers.get(position);
        holder.setData(customer, position);
    }

    @Override
    public int getItemCount() {
        if(customers != null)
            return customers.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            customers = orgCustomers;
        } else {

            List<Customer> tempCustomerList = new ArrayList<>();

            for (Customer cust : orgCustomers) {
                String fullName = (cust.getName() != null ? cust.getName() : "")
                        .concat(" ")
                        .concat(cust.getSurname() != null ? cust.getSurname() : "").trim();

                if (fullName.toLowerCase().contains(searchText.toLowerCase()))
                    tempCustomerList.add(cust);
            }
            customers = tempCustomerList;
        }

        notifyDataSetChanged();

        if (customers != null)
            returnSizeCallback.OnReturn(customers.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}