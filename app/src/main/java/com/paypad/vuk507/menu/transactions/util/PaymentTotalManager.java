package com.paypad.vuk507.menu.transactions.util;

import android.content.Context;
import android.util.Log;

import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.PaymentDetailModel;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class PaymentTotalManager {

    private SaleModel mSaleModel;
    private Context mContext;
    private List<PaymentDetailModel> paymentDetailModels = new ArrayList<>();
    private OrderManager orderManager;
    private double totalTipAmount = 0d;
    private List<Long> taxes = new ArrayList<>();

    public PaymentTotalManager(Context context, SaleModel saleModel) {
        mContext = context;
        mSaleModel = saleModel;
        orderManager = new OrderManager();
    }

    public List<PaymentDetailModel> getPaymentDetails(){
        addDiscountItems();
        addSubtotalModel();
        addTaxModels();
        addTipModel();
        addTotalAmountModel();
        return paymentDetailModels;
    }

    private void addDiscountItems(){
        List<SaleItem> saleItems = new ArrayList<>();

        for(SaleItem saleItem : mSaleModel.getSaleItems()){
            SaleItem saleItem1 = new SaleItem();
            saleItem1.setDiscounts(saleItem.getDiscounts());
            saleItem1.setAmount(saleItem.getAmount() * saleItem.getQuantity());
            saleItems.add(saleItem1);
        }

        for(Discount discount : mSaleModel.getSale().getDiscounts()){

            PaymentDetailModel paymentDetailModel = new PaymentDetailModel();
            paymentDetailModel.setDescBold(false);
            paymentDetailModel.setDrawableId(R.drawable.icon_paym_total_discount);

            if(discount.getAmount() > 0d){
                paymentDetailModel.setItemName(discount.getName());
                String amountStr = CommonUtils.getDoubleStrValueForView(discount.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                paymentDetailModel.setItemDesc("-".concat(amountStr));
            }else if(discount.getRate() > 0d){
                paymentDetailModel.setItemName(discount.getName()
                        .concat(" (%").concat(CommonUtils.getDoubleStrValueForView(discount.getRate(), TYPE_RATE)).concat(")"));

                double discountAmount = 0d;
                double totalDiscAmount = 0d;

                for(SaleItem saleItem : saleItems){
                    if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
                        for(Discount discount1 : saleItem.getDiscounts()){

                            if(discount.getId() == discount1.getId()){
                                discountAmount = discountAmount + ((saleItem.getAmount() / 100d)  * discount.getRate());

                                saleItem.setAmount(CommonUtils.round(saleItem.getAmount() - discountAmount, 2));

                                totalDiscAmount = totalDiscAmount + discountAmount;

                                break;
                            }
                        }
                        discountAmount = 0d;
                    }
                }

                String amountStr = CommonUtils.getDoubleStrValueForView(totalDiscAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                paymentDetailModel.setItemDesc("-".concat(amountStr));
            }

            paymentDetailModels.add(paymentDetailModel);
        }
    }

    private void addSubtotalModel(){
        PaymentDetailModel paymentDetailModel = new PaymentDetailModel();
        paymentDetailModel.setDescBold(false);
        paymentDetailModel.setDrawableId(R.drawable.icon_paym_total_subtotal);
        paymentDetailModel.setItemName(mContext.getResources().getString(R.string.subtotal));
        String amountStr = CommonUtils.getDoubleStrValueForView(mSaleModel.getSale().getSubTotalAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        paymentDetailModel.setItemDesc(amountStr);
        paymentDetailModels.add(paymentDetailModel);
    }

    private void addTaxModels(){

        for(SaleItem saleItem : mSaleModel.getSaleItems()){
            OrderItemTax orderItemTax = saleItem.getOrderItemTaxes().get(0);

            if(!taxes.contains(orderItemTax.getTaxId())){
                PaymentDetailModel paymentDetailModel = new PaymentDetailModel();
                paymentDetailModel.setDescBold(false);
                paymentDetailModel.setDrawableId(R.drawable.icon_paym_total_tax);
                paymentDetailModel.setItemName(mContext.getResources().getString(R.string.sales_tax)
                        .concat(" (").concat(CommonUtils.getDoubleStrValueForView(orderItemTax.getTaxRate(), TYPE_RATE))
                        .concat("%)"));
                paymentDetailModel.setItemDesc(mContext.getResources().getString(R.string.included));
                paymentDetailModels.add(paymentDetailModel);
                taxes.add(orderItemTax.getTaxId());
            }
        }
    }

    private void addTipModel(){
        for(Transaction transaction : mSaleModel.getTransactions()){
            if(transaction.getTipAmount() > 0)
                totalTipAmount = totalTipAmount + transaction.getTipAmount();
        }

        if(totalTipAmount > 0d){
            PaymentDetailModel paymentDetailModel = new PaymentDetailModel();
            paymentDetailModel.setDescBold(false);
            paymentDetailModel.setDrawableId(R.drawable.icon_paym_item_tip);
            paymentDetailModel.setItemName(mContext.getResources().getString(R.string.tip));
            String amountStr = CommonUtils.getDoubleStrValueForView(totalTipAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            paymentDetailModel.setItemDesc(amountStr);
            paymentDetailModels.add(paymentDetailModel);
        }
    }

    private void addTotalAmountModel(){
        PaymentDetailModel paymentDetailModel = new PaymentDetailModel();
        paymentDetailModel.setDescBold(true);
        paymentDetailModel.setDrawableId(R.drawable.icon_paym_total_subtotal);
        paymentDetailModel.setItemName(mContext.getResources().getString(R.string.total));
        double totalAmount = CommonUtils.round(mSaleModel.getSale().getSubTotalAmount() + totalTipAmount, 2);
        String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        paymentDetailModel.setItemDesc(amountStr);
        paymentDetailModels.add(paymentDetailModel);
    }
}