package com.paypad.parator.menu.discount.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.enums.CurrencyEnum;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.pojo.SaleModelInstance;

import java.util.ArrayList;
import java.util.List;

public class DiscountListAdapter extends RecyclerView.Adapter<DiscountListAdapter.DiscountHolder> {

    private Context context;
    private List<Discount> discounts = new ArrayList<>();
    private List<Discount> orgDiscounts = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnDiscountCallback returnDiscountCallback;
    private ItemProcessEnum processType;
    private IOrderManager orderManager;

    public DiscountListAdapter(Context context, List<Discount> discounts,
                               BaseFragment.FragmentNavigation fragmentNavigation,
                               ReturnDiscountCallback returnDiscountCallback,
                               ItemProcessEnum processType) {
        this.context = context;
        this.discounts.addAll(discounts);
        this.orgDiscounts.addAll(discounts);
        this.fragmentNavigation = fragmentNavigation;
        this.returnDiscountCallback = returnDiscountCallback;
        this.processType = processType;
        orderManager = new OrderManager();
    }

    @NonNull
    @Override
    public DiscountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discount_list, parent, false);
        return new DiscountHolder(itemView);
    }

    public class DiscountHolder extends RecyclerView.ViewHolder {

        private CardView discountItemCv;
        private TextView discountNameTv;
        private TextView rateOrAmountTv;
        private Discount discount;
        private int position;

        DiscountHolder(View view) {
            super(view);

            discountItemCv = view.findViewById(R.id.discountItemCv);
            discountNameTv = view.findViewById(R.id.discountNameTv);
            rateOrAmountTv = view.findViewById(R.id.rateOrAmountTv);

            discountItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(processType == ItemProcessEnum.SELECTED){
                        returnDiscountCallback.OnReturn(discount, ItemProcessEnum.SELECTED);
                        notifyItemChanged(position);
                    } else {
                        returnDiscountCallback.OnReturn(discount, ItemProcessEnum.SELECTED);
                        /*fragmentNavigation.pushFragment(new DiscountEditFragment(discount, new ReturnDiscountCallback() {
                            @Override
                            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                                returnDiscountCallback.OnReturn(discount, processType);
                                //discounts.set(position, discount);
                                //notifyItemChanged(position);
                                //notifyDataSetChanged();
                            }
                        }));*/
                    }

                }
            });
        }

        public void setData(Discount discount, int position) {
            this.discount = discount;
            this.position = position;
            discountNameTv.setText(discount.getName());
            rateOrAmountTv.setText(getRateOrAmountVal(discount));
            setEnabilityOfDiscount();
        }

        private void setEnabilityOfDiscount() {
            if(processType == ItemProcessEnum.SELECTED){
                if(OrderManager.isDiscountInSale(SaleModelInstance.getInstance().getSaleModel().getOrder(), discount)){
                    discountItemCv.setEnabled(false);
                    discountNameTv.setTextColor(context.getResources().getColor(R.color.Gray, null));
                    rateOrAmountTv.setTextColor(context.getResources().getColor(R.color.Gray, null));
                }else {
                    discountItemCv.setEnabled(true);
                    discountNameTv.setTextColor(context.getResources().getColor(R.color.Black, null));
                    rateOrAmountTv.setTextColor(context.getResources().getColor(R.color.Black, null));
                }
            }
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

    public void addDiscount(Discount discount1){
        //TODO - categories listesi sorunlu bakacagiz
        if(discounts != null && discount1 != null){
            discounts.add(discount1);
            notifyDataSetChanged();
            //notifyItemInserted(discounts.size() - 1); //TODO calismiyor
        }
    }

    @Override
    public void onBindViewHolder(final DiscountHolder holder, final int position) {
        Discount discount = discounts.get(position);
        holder.setData(discount, position);
    }

    @Override
    public int getItemCount() {
        if(discounts != null)
            return discounts.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            discounts = orgDiscounts;
        } else {

            List<Discount> tempDiscountList = new ArrayList<>();
            for (Discount discount : orgDiscounts) {
                if (discount.getName() != null &&
                        discount.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempDiscountList.add(discount);
            }
            discounts = tempDiscountList;
        }

        notifyDataSetChanged();

        if (discounts != null)
            returnSizeCallback.OnReturn(discounts.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}