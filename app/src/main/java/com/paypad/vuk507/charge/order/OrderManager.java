package com.paypad.vuk507.charge.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.paypad.vuk507.db.RefundDBHelper;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderItemTax;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ConversionHelper;
import com.paypad.vuk507.utils.DataUtils;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmResults;

public class OrderManager implements IOrderManager{

    @Override
    public String addProductToOrder(Context context, Product product, double amount, boolean isDynamicAmount) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();

        SaleItem saleItem = new SaleItem();
        saleItem.setProductId(product.getId());
        saleItem.setName(product.getName());
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(CommonUtils.round(amount, 2));
        saleItem.setQuantity(1);
        saleItem.setDynamicAmount(isDynamicAmount);
        saleItem.setSaleUuid(sale.getSaleUuid());
        saleItem.setColorId(product.getColorId());
        saleItem.setItemImage(product.getProductImage());
        saleItem.setCategoryName(DataUtils.getCategoryName(context, product.getCategoryId()));

        setOrderItemTaxForProduct(saleItem, product);

        sale.setSaleCount(getOrderItemCount() + 1);

        sale.setTotalAmount(CommonUtils.round(sale.getTotalAmount(), 2));

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }

    @Override
    public String addCustomItemToOrder(SaleItem saleItem, OrderItemTax orderItemTax) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();

        setOrderItemTaxForCustomItem(saleItem, orderItemTax);

        sale.setSaleCount(getOrderItemCount() + 1);
        sale.setTotalAmount(CommonUtils.round(sale.getTotalAmount(), 2));

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }

    @Override
    public void setOrderItemTaxForProduct(SaleItem saleItem, Product product) {
        TaxModel taxModel = DataUtils.getTaxModelById(product.getTaxId());
        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);
        addTaxAmount(saleItem, orderItemTax);
    }

    @Override
    public void setOrderItemTaxForCustomItem(SaleItem saleItem, OrderItemTax orderItemTax) {
        addTaxAmount(saleItem, orderItemTax);
    }

    private void addTaxAmount(SaleItem saleItem, OrderItemTax orderItemTax){
        saleItem.setTaxModel(orderItemTax);

        saleItem.setGrossAmount(getGrossAmount(saleItem.getAmount(), orderItemTax));
        saleItem.setTaxAmount(getTaxAmount(saleItem.getAmount(), orderItemTax));
    }

    public static double getTaxAmount(double amount, OrderItemTax orderItemTax){
        double taxAmount = CommonUtils.round(amount - getGrossAmount(amount, orderItemTax), 2);
        return taxAmount;
    }

    public static double getGrossAmount(double amount, OrderItemTax orderItemTax){
        double grossAmount = CommonUtils.round(((100d * amount) / (100d + orderItemTax.getTaxRate())), 2);
        return grossAmount ;
    }

    @Override
    public int getOrderItemCount() {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        int totalSaleCount = 0;

        for(SaleItem saleItem : saleItems)
            totalSaleCount = totalSaleCount + saleItem.getQuantity();

        sale.setSaleCount(totalSaleCount);
        return totalSaleCount;
    }

    @Override
    public Transaction getTransactionWillBePaid() {
        List<Transaction> transactions = SaleModelInstance.getInstance().getSaleModel().getTransactions();

        Transaction mTransaction = null;
        long seqNumber = 999;
        for(Transaction transaction : transactions){
            if(!transaction.isPaymentCompleted()){
                if(transaction.getSeqNumber() < seqNumber){
                    mTransaction = transaction;
                    seqNumber = transaction.getSeqNumber();
                }
            }
        }
        return mTransaction;
    }

    @Override
    public double getDiscountedAmountByAddingCustomItem(SaleItem saleItem) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        double totalAmount = sale.getTotalAmount() + saleItem.getAmount();
        double discountedAmount = 0d;

        if (sale.getDiscounts() != null) {
            for (OrderItemDiscount discount : sale.getDiscounts()) {
                if (discount.getRate() > 0d) {

                    if (saleItem.getDiscounts() == null)
                        saleItem.setDiscounts(new RealmList<>());


                    saleItem.getDiscounts().add(discount);
                }
            }
            discountedAmount = totalAmount - getTotalDiscountAmountOfSale();
            discountedAmount = discountedAmount - getTotalDiscountAmountOfSaleItem(saleItem);
        }

        return CommonUtils.round(discountedAmount, 2);
    }

    @Override
    public long getMaxSplitSequenceNumber() {
        List<Transaction> transactions = SaleModelInstance.getInstance().getSaleModel().getTransactions();

        long maxValue = 0;

        if(transactions != null){
            for(Transaction transaction : transactions){
                if(transaction.getSeqNumber() > maxValue)
                    maxValue = transaction.getSeqNumber();
            }
        }
        return maxValue;
    }

    @Override
    public boolean isExistNotCompletedTransaction() {
        List<Transaction> transactions = SaleModelInstance.getInstance().getSaleModel().getTransactions();

        for(Transaction transaction : transactions){
            if(!transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }

    @Override
    public boolean isExistPaymentCompletedTransaction() {
        List<Transaction> transactions = SaleModelInstance.getInstance().getSaleModel().getTransactions();

        for(Transaction transaction : transactions){
            if(transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }

    @Override
    public boolean isDiscountInSale(Discount discount) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        for(OrderItemDiscount discount1 : sale.getDiscounts()){
            if(discount.getId() == discount1.getId())
                return true;
        }
        return false;
    }

    @Override
    public boolean isSaleItemInSale(SaleItem saleItem) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        for(SaleItem saleItem1 : saleItems){
            if(saleItem.getUuid().equals(saleItem1.getUuid()))
                return true;
        }
        return false;
    }

    @Override
    public void setRemainAmount(double amount) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        sale.setRemainAmount(CommonUtils.round(amount, 2));
    }

    @Override
    public void setUserIdToOrder(String userId) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        if(sale.getUserUuid() == null || sale.getUserUuid().isEmpty())
            sale.setUserUuid(userId);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void setDeviceIdToOrder(Context context) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        sale.setDeviceId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    @Override
    public void setRemainAmountByDiscountedAmount() {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        sale.setRemainAmount(CommonUtils.round(sale.getDiscountedAmount(), 2));
    }

    @Override
    public void addDiscount(Discount discount) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(discount.getRate() > 0){
            List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
            for(SaleItem saleItem : saleItems){

                if(saleItem.getDiscounts() == null)
                    saleItem.setDiscounts(new RealmList<>());


                saleItem.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
            }
        }

        if(sale.getDiscounts() == null)
            sale.setDiscounts(new RealmList<>());

        boolean exist = false;
        for(OrderItemDiscount discount1 : sale.getDiscounts()){
            if(discount1.getId() == discount.getId()){
                exist = true;
                break;
            }
        }

        if(!exist){
            sale.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
            setDiscountedAmountOfSale();
        }
    }

    @Override
    public void setDiscountedAmountOfSale(){
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        double totalAmnt = 0d;
        for(SaleItem saleItem : saleItems)
            totalAmnt = totalAmnt + (saleItem.getAmount() * saleItem.getQuantity());

        sale.setTotalAmount(CommonUtils.round(totalAmnt, 2));

        if(sale.getTotalAmount() > 0)
            sale.setDiscountedAmount(CommonUtils.round(sale.getTotalAmount() - getTotalDiscountAmountOfSale(), 2));
        else
            sale.setDiscountedAmount(0d);

        if(sale.getDiscountedAmount() <= 0)
            sale.setDiscountedAmount(0d);
    }

    @Override
    public Transaction addTransactionToOrder(double splitAmount) {
        List<Transaction> transactions = SaleModelInstance.getInstance().getSaleModel().getTransactions();

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setSaleUuid(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid());
        transaction.setTransactionAmount(CommonUtils.round(splitAmount, 2));
        transaction.setSeqNumber(getMaxSplitSequenceNumber() + 1);
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public double getRemainAmount() {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        return sale.getRemainAmount();
    }

    @Override
    public double getTotalDiscountAmountOfSale(){
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        double totalDiscountAmount = 0;

        if(sale.getDiscounts() != null && sale.getDiscounts().size() > 0){
            for(OrderItemDiscount discount : sale.getDiscounts()){
                if(discount.getAmount() > 0)
                    totalDiscountAmount = totalDiscountAmount + discount.getAmount();
            }
        }

        if(saleItems != null && saleItems.size() > 0){
            for(SaleItem saleItem : saleItems){
                totalDiscountAmount = totalDiscountAmount + getTotalDiscountAmountOfSaleItem(saleItem);
            }
        }

        return CommonUtils.round(totalDiscountAmount, 2);
    }

    @Override
    public void addDiscountToSaleItem(String saleItemUuid, Discount discount) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(saleItems != null){
            for(SaleItem saleItem : saleItems){
                if(saleItem.getUuid().equals(saleItemUuid)){
                    saleItem.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
                    break;
                }
            }
        }

        if(sale.getDiscounts() != null){
            if(!sale.getDiscounts().contains(discount))
                sale.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
        }
    }

    @Override
    public void removeDiscountFromSaleItem(String saleItemUuid, Discount discount) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(saleItems != null){
            for(SaleItem saleItem : saleItems){
                if(saleItem.getUuid().equals(saleItemUuid)){
                    saleItem.getDiscounts().remove(discount);
                    break;
                }
            }

            boolean discountExistAnotherItem = false;
            for(SaleItem saleItem : saleItems){
                if(saleItem.getDiscounts().contains(discount)){
                    discountExistAnotherItem = true;
                    break;
                }
            }

            if(!discountExistAnotherItem){
                if(sale.getDiscounts() != null)
                    sale.getDiscounts().remove(discount);
            }
        }
    }

    private void addAllDiscountsToSaleItem(SaleItem saleItem) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(sale.getDiscounts() != null){
            for(OrderItemDiscount discount : sale.getDiscounts()){
                if(discount.getRate() > 0d){

                    if(saleItem.getDiscounts() == null)
                        saleItem.setDiscounts(new RealmList<>());

                    saleItem.getDiscounts().add(discount);
                }
            }
            setDiscountedAmountOfSale();
        }
    }

    public static double getTotalDiscountAmountOfSaleItem(SaleItem saleItem){
        double totalAmount = saleItem.getAmount() * (double) saleItem.getQuantity();
        double discountAmount = 0d;

        if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
            for(OrderItemDiscount discount : saleItem.getDiscounts()){
                discountAmount = discountAmount + ((totalAmount / 100d)  * discount.getRate());
                totalAmount = totalAmount - discountAmount;
            }
        }
        return CommonUtils.round(discountAmount, 2);
    }

    public static double getTotalDiscountAmountOfOrderRefundItem(OrderRefundItem saleItem){
        double totalAmount = saleItem.getAmount() * (double) saleItem.getQuantity();
        double discountAmount = 0d;

        if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
            for(OrderItemDiscount discount : saleItem.getDiscounts()){
                discountAmount = discountAmount + ((totalAmount / 100d)  * discount.getRate());
                totalAmount = totalAmount - discountAmount;
            }
        }
        return CommonUtils.round(discountAmount, 2);
    }

    public static double getTotalTipAmountOfSale(SaleModel saleModel){
        double totalTipAmount = 0d;
        for(Transaction transaction : saleModel.getTransactions())
            totalTipAmount = CommonUtils.round(totalTipAmount + transaction.getTipAmount(), 2);

        return totalTipAmount;
    }

    public static double getTotalDiscountAmountOfSale(Sale sale, List<SaleItem> saleItems){

        double totalDiscountAmount = 0;

        if(sale.getDiscounts() != null && sale.getDiscounts().size() > 0){
            for(OrderItemDiscount discount : sale.getDiscounts()){
                if(discount.getAmount() > 0)
                    totalDiscountAmount = totalDiscountAmount + discount.getAmount();
            }
        }

        if(saleItems != null && saleItems.size() > 0){
            for(SaleItem saleItem : saleItems){
                totalDiscountAmount = totalDiscountAmount + getTotalDiscountAmountOfSaleItem(saleItem);
            }
        }

        return CommonUtils.round(totalDiscountAmount, 2);
    }

    public static boolean isSaleItemRefunded(SaleItem saleItem, String orderId){
        RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(orderId, true);

        for(Refund refund : refunds){
            RealmList<OrderRefundItem> orderRefundItems = refund.getRefundItems();

            for(OrderRefundItem orderRefundItem : orderRefundItems){
                if(saleItem.getUuid().equals(orderRefundItem.getUuid())){
                    return true;
                }
            }
        }
        return false;
    }
}
