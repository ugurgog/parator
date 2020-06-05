package com.paypad.vuk507.menu.discount;

import android.app.Activity;
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
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.enums.CurrencyEnum;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class DiscountListAdapter extends RecyclerView.Adapter<DiscountListAdapter.DiscountHolder> {

    private Context context;
    private List<Discount> discounts = new ArrayList<>();
    private List<Discount> orgDiscounts = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnDiscountCallback returnDiscountCallback;

    public DiscountListAdapter(Context context, List<Discount> discounts,
                               BaseFragment.FragmentNavigation fragmentNavigation,
                               ReturnDiscountCallback returnDiscountCallback) {
        this.context = context;
        this.discounts.addAll(discounts);
        this.orgDiscounts.addAll(discounts);
        this.fragmentNavigation = fragmentNavigation;
        this.returnDiscountCallback = returnDiscountCallback;
    }

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
        private ImageView deleteImgv;
        private Discount discount;
        private int position;

        public DiscountHolder(View view) {
            super(view);

            discountItemCv = view.findViewById(R.id.discountItemCv);
            discountNameTv = view.findViewById(R.id.discountNameTv);
            rateOrAmountTv = view.findViewById(R.id.rateOrAmountTv);
            deleteImgv = view.findViewById(R.id.deleteImgv);

            discountItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentNavigation.pushFragment(new DiscountEditFragment(discount, new ReturnDiscountCallback() {
                        @Override
                        public void OnReturn(Discount discount) {
                            discounts.set(position, discount);
                            notifyItemChanged(position);
                            //notifyDataSetChanged();
                        }
                    }));
                }
            });

            deleteImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomDialogBox.Builder((Activity) context)
                            .setMessage(context.getResources().getString(R.string.sure_to_delete_discount))
                            .setNegativeBtnVisibility(View.VISIBLE)
                            .setNegativeBtnText(context.getResources().getString(R.string.cancel))
                            .setNegativeBtnBackground(context.getResources().getColor(R.color.Silver, null))
                            .setPositiveBtnVisibility(View.VISIBLE)
                            .setPositiveBtnText(context.getResources().getString(R.string.yes))
                            .setPositiveBtnBackground(context.getResources().getColor(R.color.bg_screen1, null))
                            .setDurationTime(0)
                            .isCancellable(true)
                            .setEditTextVisibility(View.GONE)
                            .OnPositiveClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {
                                    DiscountDBHelper.deleteDiscount(discount.getId(), new CompleteCallback() {
                                        @Override
                                        public void onComplete(BaseResponse baseResponse) {
                                            CommonUtils.showToastShort(context, baseResponse.getMessage());
                                            if(baseResponse.isSuccess()){

                                                returnDiscountCallback.OnReturn((Discount) baseResponse.getObject());

                                                //discounts.remove(position);
                                                //notifyItemRemoved(position);
                                                //notifyItemRangeChanged(position, getItemCount());
                                                //notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            })
                            .OnNegativeClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            }).build();
                }
            });
        }

        public void setData(Discount discount, int position) {
            this.discount = discount;
            this.position = position;
            discountNameTv.setText(discount.getName());
            rateOrAmountTv.setText(getRateOrAmountVal(discount));
        }

        private String getRateOrAmountVal(Discount discount) {
            String rateOrAmounttr = "";
            if(discount.getRate() != 0){
                rateOrAmounttr = "% ".concat(Integer.toString(discount.getRate()));
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