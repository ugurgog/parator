package com.paypad.vuk507.menu.transactions.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.OrderRefundItemDBHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.SaleItemDBHelper;
import com.paypad.vuk507.menu.transactions.model.RefundedTrxModel;
import com.paypad.vuk507.model.Order;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RefundedTransactionAdapter extends RecyclerView.Adapter<RefundedTransactionAdapter.RefundItemHolder> {

    private Context context;
    private List<RefundedTrxModel> refundedTrxModels = new ArrayList<>();
    private double orderItemsTotalAmount = 0d;
    private String orderId;

    public RefundedTransactionAdapter(Context context, List<RefundedTrxModel> refundedTrxModels, String orderId) {
        this.context = context;
        this.refundedTrxModels.addAll(refundedTrxModels);
        this.orderId = orderId;
        calculateDiscountAmount();
    }

    @NonNull
    @Override
    public RefundedTransactionAdapter.RefundItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_refund_trx, parent, false);
        return new RefundedTransactionAdapter.RefundItemHolder(itemView);
    }

    public class RefundItemHolder extends RecyclerView.ViewHolder {

        private ImageView refundImgv;
        private TextView refundDateTv;
        private TextView refundReasonTv;
        private RecyclerView itemsRv;
        private RecyclerView paymentsRv;
        private TextView trxSeqDescTv;

        private ImageView paymentTypeImgv;
        private TextView paymentTypeTv;
        private TextView paymTypeAmountTv;

        int position;
        private RefundedTrxModel refundedTrxModel;

        public RefundItemHolder(View view) {
            super(view);
            refundImgv = view.findViewById(R.id.refundImgv);
            refundDateTv = view.findViewById(R.id.refundDateTv);
            refundReasonTv= view.findViewById(R.id.refundReasonTv);
            itemsRv = view.findViewById(R.id.itemsRv);
            paymentsRv = view.findViewById(R.id.paymentsRv);

            paymentTypeImgv = view.findViewById(R.id.paymentTypeImgv);
            paymentTypeTv = view.findViewById(R.id.paymentTypeTv);
            paymTypeAmountTv = view.findViewById(R.id.paymTypeAmountTv);
            trxSeqDescTv = view.findViewById(R.id.trxSeqDescTv);
        }

        public void setData(RefundedTrxModel refundedTrxModel, int position) {
            this.refundedTrxModel = refundedTrxModel;
            this.position = position;
            setRefundImgv();
            setRefundReasonTv();
            setRefundDateTv();
            setItemsAdapter();
            setPaymentTypesAdapter();
            //setPaymentTypeImgv();
            //setTrxZnoFno();
        }

        /*private void setTrxZnoFno() {
            @SuppressLint("DefaultLocale") String desc = context.getResources().getString(R.string.z_no_upper)
                    .concat(" : ")
                    .concat(String.format("%06d", refund.getzNum()))
                    .concat(" / ")
                    .concat(context.getResources().getString(R.string.f_no_upper))
                    .concat(" : ")
                    .concat(String.format("%06d", refund.getfNum()));
            trxSeqDescTv.setText(desc);
        }*/

        private void setRefundImgv(){
            Glide.with(context).load(R.drawable.ic_arrow_back_white_24dp).into(refundImgv);
            refundImgv.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Orange, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
        }

        private void setRefundReasonTv() {
            refundReasonTv.setText(refundedTrxModel.getRefunds().get(0).getRefundReason());
        }

        private void setRefundDateTv() {
            @SuppressLint("SimpleDateFormat")
            String trxDate = new SimpleDateFormat("dd MM yyyy HH:mm").format(refundedTrxModel.getRefunds().get(0).getCreateDate());
            refundDateTv.setText(trxDate);
        }

        private void setItemsAdapter() {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            itemsRv.setLayoutManager(linearLayoutManager);
            List<OrderRefundItem> saleItems = new ArrayList<>();
            double paymTypeAmount = 0d;

            if(refundedTrxModel.getRefunds().get(0).isRefundByAmount()){
                OrderRefundItem saleItem = new OrderRefundItem();
                saleItem.setName(context.getResources().getString(R.string.custom_amount));

                double totalRefundAmount = 0d;
                for(Refund refund : refundedTrxModel.getRefunds())
                    totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);

                saleItem.setAmount(totalRefundAmount);
                paymTypeAmount = totalRefundAmount;
                saleItems.add(saleItem);
            }else {


                RealmResults<OrderRefundItem> refundItems = OrderRefundItemDBHelper.getRefundItemsByRefundGroupId(refundedTrxModel.getRefundGroupId());

                //RealmList<OrderRefundItem> refundItems = refund.getRefundItems();

                for (OrderRefundItem orderRefundItem : refundItems) {
                    //double discountedByRateAmount = OrderManager.getTotalDiscountAmountOfOrderRefundItem(orderRefundItem);
                    //double discountedByAmountAmount = getDiscountAmountByAmount(orderItemsTotalAmount, orderRefundItem.getAmount() * orderRefundItem.getQuantity());

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    OrderRefundItem orderRefundItem1 = realm.copyFromRealm(orderRefundItem);
                    //orderRefundItem1.setAmount(CommonUtils.round((orderRefundItem.getAmount() * orderRefundItem.getQuantity()) - (discountedByRateAmount + discountedByAmountAmount), 2));
                    orderRefundItem1.setAmount(CommonUtils.round((orderRefundItem.getAmount() ), 2));

                    realm.commitTransaction();

                    paymTypeAmount = CommonUtils.round(paymTypeAmount + orderRefundItem.getAmount(), 2);
                    saleItems.add(orderRefundItem1);
                }
            }

            RefundedItemsServicesAdapter refundedItemsServicesAdapter = new RefundedItemsServicesAdapter(context, saleItems);
            itemsRv.setAdapter(refundedItemsServicesAdapter);

            paymTypeAmountTv.setText(CommonUtils.getAmountTextWithCurrency(paymTypeAmount));
        }

        private void setPaymentTypesAdapter(){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            paymentsRv.setLayoutManager(linearLayoutManager);

            RefundedPaymentsAdapter refundedPaymentsAdapter = new RefundedPaymentsAdapter(context, refundedTrxModel.getRefunds());
            paymentsRv.setAdapter(refundedPaymentsAdapter);
        }

        private double getDiscountAmountByAmount(double orderItemsTotalAmount, double orderItemAmount){
            double discountedByAmount = 0d;

            Order order = SaleDBHelper.getSaleById(refundedTrxModel.getRefunds().get(0).getOrderId());

            for(OrderItemDiscount discount : order.getDiscounts()){
                if(discount.getAmount() > 0d){
                    discountedByAmount = CommonUtils.round(discountedByAmount + (
                            (discount.getAmount() / orderItemsTotalAmount) * orderItemAmount), 2);
                }
            }
            return discountedByAmount;
        }

        /*private void setPaymentTypeImgv(){
            Transaction transaction = TransactionDBHelper.getTransactionById(refund.getTransactionId());

            if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                Glide.with(context).load(R.drawable.icon_payment_cash_type).into(paymentTypeImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.cash));
            } else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){
                Glide.with(context).load(R.drawable.icon_payment_card_type).into(paymentTypeImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.card));
            }

        }*/
    }

    private void calculateDiscountAmount(){
        RealmResults<OrderItem> orderItems = SaleItemDBHelper.getSaleItemsBySaleId(orderId);
        List<OrderItem> orderItemList = new ArrayList(orderItems);

        for(Iterator<OrderItem> it = orderItemList.iterator(); it.hasNext();) {
            OrderItem orderItem = it.next();
            orderItemsTotalAmount = CommonUtils.round(orderItemsTotalAmount + orderItem.getAmount(), 2);
        }
    }

    @Override
    public void onBindViewHolder(final RefundedTransactionAdapter.RefundItemHolder holder, final int position) {
        RefundedTrxModel refundedTrxModel = refundedTrxModels.get(position);
        holder.setData(refundedTrxModel, position);
    }

    @Override
    public int getItemCount() {
        if(refundedTrxModels != null)
            return refundedTrxModels.size();
        else
            return 0;
    }
}