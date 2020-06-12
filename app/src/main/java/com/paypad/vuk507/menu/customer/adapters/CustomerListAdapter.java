package com.paypad.vuk507.menu.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.CurrencyEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.customer.CustomerEditFragment;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.discount.DiscountEditFragment;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;

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
        private ImageView itemImgv;
        private TextView shortNameTv;
        private TextView customerNameTv;
        private TextView customerInfoTv;

        private Customer customer;
        private int position;

        public CustomerHolder(View view) {
            super(view);

            customerItemCv = view.findViewById(R.id.customerItemCv);
            itemImgv = view.findViewById(R.id.itemImgv);
            shortNameTv = view.findViewById(R.id.shortNameTv);
            customerNameTv = view.findViewById(R.id.customerNameTv);
            customerInfoTv = view.findViewById(R.id.customerInfoTv);

            customerItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentNavigation.pushFragment(new CustomerEditFragment(customer, new ReturnCustomerCallback() {
                        @Override
                        public void OnReturn(Customer customer, ItemProcessEnum processEnum) {
                            returnCustomerCallback.OnReturn(customer, processEnum);
                            //discounts.set(position, discount);
                            //notifyItemChanged(position);
                            //notifyDataSetChanged();
                        }
                    }));
                }
            });
        }

        public void setData(Customer customer, int position) {
            this.customer = customer;
            this.position = position;

            //discountNameTv.setText(discount.getName());
            //rateOrAmountTv.setText(getRateOrAmountVal(discount));
        }

        private String getRateOrAmountVal(Discount discount) {
            String rateOrAmounttr = "";
            if(discount.getRate() != 0){
                rateOrAmounttr = "% ".concat(Double.toString(discount.getRate()));
            }else if(discount.getAmount() != 0){
                rateOrAmounttr = CurrencyEnum.TL.getSymbol().concat(" ").concat(String.valueOf(discount.getAmount()));
            }
            return rateOrAmounttr;
        }
    }

    public void addDiscount(Customer customer1){
        //TODO - categories listesi sorunlu bakacagiz
        if(customers != null && customer1 != null){
            customers.add(customer1);
            notifyDataSetChanged();
            //notifyItemInserted(discounts.size() - 1); //TODO calismiyor
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
                if (cust.getName() != null &&
                        cust.getName().toLowerCase().contains(searchText.toLowerCase()))
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