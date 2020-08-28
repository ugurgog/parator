package com.paypad.parator.menu.customer.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.parator.model.Customer;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerHolder> {

    private Context context;
    private List<Customer> customers = new ArrayList<>();
    private List<Customer> orgCustomers = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnCustomerCallback returnCustomerCallback;

    public CustomerListAdapter(Context context, List<Customer> customers,
                               BaseFragment.FragmentNavigation fragmentNavigation,
                               ReturnCustomerCallback returnCustomerCallback) {
        this.context = context;
        this.customers.addAll(customers);
        this.orgCustomers.addAll(customers);
        this.fragmentNavigation = fragmentNavigation;
        this.returnCustomerCallback = returnCustomerCallback;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_list, parent, false);
        return new CustomerHolder(itemView);
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {

        private CardView customerItemCv;
        private TextView customerNameTv;
        private TextView customerInfoTv;
        private TextView shortCustomerNameTv;

        private Customer customer;
        private int position;

        public CustomerHolder(View view) {
            super(view);

            customerItemCv = view.findViewById(R.id.customerItemCv);
            shortCustomerNameTv = view.findViewById(R.id.shortCustomerNameTv);
            customerNameTv = view.findViewById(R.id.customerNameTv);
            customerInfoTv = view.findViewById(R.id.customerInfoTv);

            customerItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnCustomerCallback.OnReturn(customer, ItemProcessEnum.SELECTED);
                }
            });
        }

        public void setData(Customer customer, int position) {
            this.customer = customer;
            this.position = position;

            setCustomerName();
            setCustomerInfo();
            setCustomerShortName();
            setShortenNameColor();
        }

        private void setCustomerName() {
            String fullName = (customer.getName() != null ? customer.getName() : "")
                    .concat(" ")
                    .concat(customer.getSurname() != null ? customer.getSurname() : "").trim();
            customerNameTv.setText(fullName);
        }

        private void setShortenNameColor() {
            if(customer.getColorId() != 0){
                GradientDrawable imageShape = ShapeUtil.getShape(context.getResources().getColor(customer.getColorId(), null),
                        context.getResources().getColor(customer.getColorId(), null),
                        GradientDrawable.OVAL, 50, 0);
                shortCustomerNameTv.setBackground(imageShape);
            }
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
                customerInfoTv.setVisibility(View.GONE);
            }else {
                customerInfoTv.setVisibility(View.VISIBLE);
                customerInfoTv.setText(customerInfo);
            }
        }

        private void setCustomerShortName() {
            shortCustomerNameTv.setText(DataUtils.getCustomerShortName(customer.getName(), customer.getSurname()));
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