package com.paypad.vuk507.charge.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmResults;

public class OrderManager1 implements IOrderManager1{

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
        saleItem.setTransferred(false);

        setOrderItemTaxForProduct(saleItem, product);

        sale.setTotalItemCount(getOrderItemCount() + 1);

        sale.setTotalAmount(CommonUtils.round(sale.getTotalAmount(), 2));

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }

    @Override
    public String addCustomItemToOrder(SaleItem saleItem, OrderItemTax orderItemTax) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();

        setOrderItemTaxForCustomItem(saleItem, orderItemTax);

        sale.setTotalItemCount(getOrderItemCount() + 1);
        sale.setTotalAmount(CommonUtils.round(sale.getTotalAmount(), 2));

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }

    private void setOrderItemTaxForProduct(SaleItem saleItem, Product product) {
        TaxModel taxModel = DataUtils.getTaxModelById(product.getTaxId());
        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);

        saleItem.setTaxModel(orderItemTax);
        saleItem.setGrossAmount(getGrossAmount(saleItem.getAmount(), orderItemTax));
        saleItem.setTaxAmount(getTaxAmount(saleItem.getAmount(), orderItemTax));
    }

    public static void setOrderItemTaxForCustomItem(SaleItem saleItem, OrderItemTax orderItemTax) {
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

        int totalItemCount = 0;

        for(SaleItem saleItem : saleItems)
            totalItemCount = totalItemCount + saleItem.getQuantity();

        sale.setTotalItemCount(totalItemCount);
        return totalItemCount;
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

        double totalAmount = CommonUtils.round(sale.getTotalAmount() + saleItem.getAmount(), 2);
        double discountedAmount = 0d;

        if (sale.getDiscounts() != null) {
            for (OrderItemDiscount discount : sale.getDiscounts()) {
                if (saleItem.getDiscounts() == null)
                    saleItem.setDiscounts(new RealmList<>());

                saleItem.getDiscounts().add(discount);
            }

            discountedAmount = CommonUtils.round(totalAmount - sale.getTotalDiscountAmount(), 2);
            discountedAmount = CommonUtils.round(discountedAmount - getTotalDiscountAmountOfOrderItem(SaleModelInstance.getInstance().getSaleModel(), saleItem), 2);
        }

        return CommonUtils.round(discountedAmount, 2);
    }


    /*@Override
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
    }*/


    private long getMaxSplitSequenceNumber() {
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

        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        for (SaleItem saleItem : saleItems) {

            if (saleItem.getDiscounts() == null)
                saleItem.setDiscounts(new RealmList<>());

            saleItem.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
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

        if(!exist)
            sale.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));

        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
    }

    @Override
    public void setDiscountedAmountOfSale(){
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        double totalAmnt = 0d;
        for(SaleItem saleItem : saleItems)
            totalAmnt = totalAmnt + (saleItem.getAmount() * saleItem.getQuantity());

        sale.setTotalAmount(CommonUtils.round(totalAmnt, 2));
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
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
    public void addDiscountToSaleItem(SaleItem saleItem, Discount discount) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(saleItem.getDiscounts() == null)
            saleItem.setDiscounts(new RealmList<>());

        saleItem.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));

        boolean discountExistInOrder = false;

        if(sale.getDiscounts() != null){
            for(Iterator<OrderItemDiscount> its = sale.getDiscounts().iterator(); its.hasNext();) {
                OrderItemDiscount discount1 = its.next();

                if(discount.getId() == discount1.getId()){
                    discountExistInOrder = true;
                    break;
                }
            }
        }

        if(!discountExistInOrder){
            if(sale.getDiscounts() == null)
                sale.setDiscounts(new RealmList<>());

            sale.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
        }

        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
    }

    @Override
    public void removeDiscountFromSaleItem(SaleItem saleItem, Discount discount) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        //Parametre olarak verilen saleItem dan indirim tanımı cıkalrılır
        for(Iterator<OrderItemDiscount> its = saleItem.getDiscounts().iterator(); its.hasNext();) {
            OrderItemDiscount discount1 = its.next();

            if(discount.getId() == discount1.getId()){
                its.remove();
                break;
            }
        }

        //Order icindeki diger itemlarda discount tanımı var mı kontrol edilir
        boolean discountExistAnotherItem = false;
        for(SaleItem saleItem1 : saleItems){

            for(Iterator<OrderItemDiscount> its = saleItem1.getDiscounts().iterator(); its.hasNext();) {
                OrderItemDiscount discount1 = its.next();

                if(discount.getId() == discount1.getId()){
                    discountExistAnotherItem = true;
                    break;
                }
            }

            if(discountExistAnotherItem)
                break;
        }

        //Order daki diger item larda discounttanımı yoksa order dan discount cıkarılır
        if(!discountExistAnotherItem){
            if(sale.getDiscounts() != null){

                for(Iterator<OrderItemDiscount> its = sale.getDiscounts().iterator(); its.hasNext();) {
                    OrderItemDiscount discount1 = its.next();

                    if(discount.getId() == discount1.getId()){
                        its.remove();
                        break;
                    }
                }
            }
        }
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());


        /*if(saleItems != null){
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
            calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
        }*/
    }

    private void addAllDiscountsToSaleItem(SaleItem saleItem) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(sale.getDiscounts() != null){
            for(OrderItemDiscount discount : sale.getDiscounts()){
                if(saleItem.getDiscounts() == null)
                    saleItem.setDiscounts(new RealmList<>());



                saleItem.getDiscounts().add(discount);
            }
        }
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

    public static double getTotalDiscountAmountOfOrderItem(SaleModel saleModel, SaleItem saleItem){
        double totalSaleItemsAmount = 0d;
        double totalDiscountAmountOfOrder = 0d;

        //Tutar tanimli indirimlerde toplam item tutari uzerinden hesaplama yapacagız
        for(SaleItem saleItem1 : saleModel.getSaleItems())
            totalSaleItemsAmount = CommonUtils.round(totalSaleItemsAmount + (saleItem1.getAmount() * saleItem1.getQuantity()), 2);


        totalSaleItemsAmount = CommonUtils.round(totalSaleItemsAmount + (saleItem.getAmount() * saleItem.getQuantity()), 2);

        double totalAmount = CommonUtils.round(saleItem.getAmount() * (double) saleItem.getQuantity(), 2);
        double totalDiscountAmountOfItem = 0d;

        if(saleItem.getDiscounts() != null){
            for (OrderItemDiscount orderItemDiscount : saleItem.getDiscounts()) {

                double discountAmount = 0d;

                if (orderItemDiscount.getRate() > 0d)
                    discountAmount = CommonUtils.round((totalAmount / 100d) * orderItemDiscount.getRate(), 2);
                else if (orderItemDiscount.getAmount() > 0d) {
                    double amount1 = CommonUtils.round((orderItemDiscount.getAmount() / totalSaleItemsAmount), 2);
                    discountAmount = CommonUtils.round(amount1 * (saleItem.getAmount() * saleItem.getQuantity()), 2);
                }

                totalAmount = CommonUtils.round(totalAmount - discountAmount, 2);
                totalDiscountAmountOfItem = CommonUtils.round(totalDiscountAmountOfItem + discountAmount, 2);
                totalDiscountAmountOfOrder = CommonUtils.round(totalDiscountAmountOfOrder + discountAmount, 2);
            }
        }

        return totalDiscountAmountOfItem;
    }

    public static void calculateDiscounts(SaleModel saleModel){

        Log.i("Info", "::calculateDiscounts   ");
        Log.i("Info", "::calculateDiscounts   ");
        Log.i("Info", "::calculateDiscounts -------------Discount calculate ----------------");

        double totalSaleItemsAmount = 0d;
        double totalDiscountAmountOfOrder = 0d;

        //Tutar tanimli indirimlerde toplam item tutari uzerinden hesaplama yapacagız
        for(SaleItem saleItem : saleModel.getSaleItems())
            totalSaleItemsAmount = CommonUtils.round(totalSaleItemsAmount + (saleItem.getAmount() * saleItem.getQuantity()), 2);

        Log.i("Info", "::calculateDiscounts totalSaleItemsAmount:" + totalSaleItemsAmount);

        //Her item ozelinde tanımlı indirim tutarları hesaplanır
        for(SaleItem saleItem : saleModel.getSaleItems()){

            double totalAmount = CommonUtils.round(saleItem.getAmount() * (double) saleItem.getQuantity(), 2);
            double totalDiscountAmountOfItem = 0d;

            if(saleItem.getDiscounts() != null){
                for(OrderItemDiscount orderItemDiscount : saleItem.getDiscounts()){

                    double discountAmount = 0d;

                    if(orderItemDiscount.getRate() > 0d)
                        discountAmount = CommonUtils.round((totalAmount / 100d) * orderItemDiscount.getRate(), 2);
                    else if(orderItemDiscount.getAmount() > 0d){
                        discountAmount = CommonUtils.round((orderItemDiscount.getAmount() / totalSaleItemsAmount) * (saleItem.getAmount() * saleItem.getQuantity()), 2);
                    }


                    totalAmount = CommonUtils.round(totalAmount - discountAmount, 2);
                    totalDiscountAmountOfItem = CommonUtils.round(totalDiscountAmountOfItem + discountAmount, 2);
                    orderItemDiscount.setDiscountAmount(discountAmount);
                    totalDiscountAmountOfOrder = CommonUtils.round(totalDiscountAmountOfOrder + discountAmount, 2);

                    Log.i("Info", "::calculateDiscounts orderItemDiscount amount  :" + orderItemDiscount.getAmount() + "  rate:" + orderItemDiscount.getRate());
                    Log.i("Info", "::calculateDiscounts discountAmount            :" + discountAmount);
                    Log.i("Info", "::calculateDiscounts totalAmount               :" + totalAmount);
                    Log.i("Info", "::calculateDiscounts totalDiscountAmountOfItem :" + totalDiscountAmountOfItem);
                    Log.i("Info", "::calculateDiscounts totalDiscountAmountOfOrder:" + totalDiscountAmountOfOrder);

                }


            }

            saleItem.setTotalDiscountAmount(totalDiscountAmountOfItem);
        }
        //Order toplam indirim tutari set edilir
        saleModel.getSale().setTotalDiscountAmount(totalDiscountAmountOfOrder);

        Log.i("Info", "::calculateDiscounts Discounted amount calc-----------");
        Log.i("Info", "::calculateDiscounts saleModel.getSale().getTotalAmount():" + saleModel.getSale().getTotalAmount());
        Log.i("Info", "::calculateDiscounts totalDiscountAmountOfOrder          :" + totalDiscountAmountOfOrder);

        //Order indirimli tutar set edilir
        if(saleModel.getSale().getTotalAmount() > 0)
            saleModel.getSale().setDiscountedAmount(CommonUtils.round(saleModel.getSale().getTotalAmount() - totalDiscountAmountOfOrder, 2));
        else
            saleModel.getSale().setDiscountedAmount(0d);

        if(saleModel.getSale().getDiscountedAmount() <= 0)
            saleModel.getSale().setDiscountedAmount(0d);

        Log.i("Info", "::calculateDiscounts getDiscountedAmount          :" + saleModel.getSale().getDiscountedAmount());
        Log.i("Info", "::calculateDiscounts getTotalDiscountAmount       :" + saleModel.getSale().getTotalDiscountAmount());

        //Order a bagli indirim tutarlari set edilir
        for(OrderItemDiscount orderItemDiscount : saleModel.getSale().getDiscounts()){

            double totalDiscountAmount = 0d;

            for(SaleItem saleItem : saleModel.getSaleItems()){
                if(saleItem.getDiscounts() != null){
                    for(OrderItemDiscount orderItemDiscount1 : saleItem.getDiscounts()){
                        if(orderItemDiscount.getId() == orderItemDiscount1.getId())
                            totalDiscountAmount = CommonUtils.round(totalDiscountAmount + orderItemDiscount1.getDiscountAmount(), 2);
                    }
                }
            }
            orderItemDiscount.setDiscountAmount(totalDiscountAmount);
        }
    }
}
