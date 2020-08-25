package com.paypad.vuk507.charge.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.paypad.vuk507.db.OrderRefundItemDBHelper;
import com.paypad.vuk507.db.RefundDBHelper;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Order;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderItemTax;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Refund;
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

public class OrderManager implements IOrderManager {

    @Override
    public String addProductToOrder(Context context, Product product, double amount, boolean isDynamicAmount) {
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setName(product.getName());
        orderItem.setId(UUID.randomUUID().toString());
        orderItem.setAmount(CommonUtils.round(amount, 2));
        orderItem.setQuantity(1);
        orderItem.setDynamicAmount(isDynamicAmount);
        orderItem.setOrderId(order.getId());
        orderItem.setColorId(product.getColorId());
        orderItem.setItemImage(product.getProductImage());
        orderItem.setCategoryName(DataUtils.getCategoryName(context, product.getCategoryId()));
        orderItem.setTransferred(false);

        setOrderItemTaxForProduct(orderItem, product);

        order.setTotalItemCount(getOrderItemCount() + 1);

        order.setTotalAmount(CommonUtils.round(order.getTotalAmount(), 2));

        addAllDiscountsToSaleItem(orderItem);
        orderItems.add(orderItem);
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());

        setDiscountedAmountOfSale();

        return orderItem.getId();
    }

    @Override
    public String addCustomItemToOrder(OrderItem orderItem, OrderItemTax orderItemTax) {
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();

        setOrderItemTaxForCustomItem(orderItem, orderItemTax);

        order.setTotalItemCount(getOrderItemCount() + 1);
        order.setTotalAmount(CommonUtils.round(order.getTotalAmount(), 2));

        addAllDiscountsToSaleItem(orderItem);
        orderItems.add(orderItem);
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());

        setDiscountedAmountOfSale();

        return orderItem.getId();
    }

    private void setOrderItemTaxForProduct(OrderItem orderItem, Product product) {
        TaxModel taxModel = DataUtils.getTaxModelById(product.getTaxId());
        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);

        orderItem.setTaxModel(orderItemTax);
        orderItem.setGrossAmount(getGrossAmount(orderItem.getAmount(), orderItemTax));
        orderItem.setTaxAmount(getTaxAmount(orderItem.getAmount(), orderItemTax));
    }

    public static void setOrderItemTaxForCustomItem(OrderItem orderItem, OrderItemTax orderItemTax) {
        orderItem.setTaxModel(orderItemTax);
        orderItem.setGrossAmount(getGrossAmount(orderItem.getAmount(), orderItemTax));
        orderItem.setTaxAmount(getTaxAmount(orderItem.getAmount(), orderItemTax));
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
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();

        int totalItemCount = 0;

        for(OrderItem orderItem : orderItems)
            totalItemCount = totalItemCount + orderItem.getQuantity();

        order.setTotalItemCount(totalItemCount);
        return totalItemCount;
    }

    public static Transaction getTransactionWillBePaid(List<Transaction> transactions) {
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

    public static double getDiscountedAmountByAddingCustomItem(Order order, OrderItem orderItem) {

        double totalAmount = CommonUtils.round(order.getTotalAmount() + orderItem.getAmount(), 2);
        double discountedAmount = 0d;

        if (order.getDiscounts() != null) {
            for (OrderItemDiscount discount : order.getDiscounts()) {
                if (orderItem.getDiscounts() == null)
                    orderItem.setDiscounts(new RealmList<>());

                orderItem.getDiscounts().add(discount);
            }

            //discountedAmount = CommonUtils.round(totalAmount - order.getTotalDiscountAmount(), 2);
            //discountedAmount = CommonUtils.round(discountedAmount - getTotalDiscountAmountOfOrderItem(SaleModelInstance.getInstance().getSaleModel(), orderItem), 2);


            discountedAmount = CommonUtils.round(totalAmount - getOrderTotalDiscountAmount(), 2);
            discountedAmount = CommonUtils.round(discountedAmount - getTotalDiscountAmountOfOrderItem(SaleModelInstance.getInstance().getSaleModel(), orderItem), 2);
        }

        orderItem.setDiscounts(new RealmList<>());

        return CommonUtils.round(discountedAmount, 2);
    }

    private static double getOrderTotalDiscountAmount(){
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();

        double totalDiscountAmount = 0d;

        for(Iterator<OrderItem> its = orderItems.iterator(); its.hasNext();) {
            OrderItem orderItem = its.next();

            if(orderItem.getDiscounts() != null){
                for(Iterator<OrderItemDiscount> it1 = orderItem.getDiscounts().iterator(); it1.hasNext();) {
                    OrderItemDiscount orderItemDiscount = it1.next();

                    if(orderItemDiscount.getRate() > 0d)
                        totalDiscountAmount = CommonUtils.round(totalDiscountAmount + orderItemDiscount.getDiscountAmount(), 2);
                }
            }
        }

        if(order.getDiscounts() != null){
            for(Iterator<OrderItemDiscount> its = order.getDiscounts().iterator(); its.hasNext();) {
                OrderItemDiscount orderItemDiscount = its.next();

                if(orderItemDiscount.getAmount() > 0d)
                    totalDiscountAmount = CommonUtils.round(totalDiscountAmount + orderItemDiscount.getAmount(), 2);
            }
        }

        return totalDiscountAmount;
    }


    /*@Override
    public double getTotalDiscountAmountOfSale(){
        List<OrderItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();
        Order sale = SaleModelInstance.getInstance().getSaleModel().getOrder();

        double totalDiscountAmount = 0;

        if(sale.getDiscounts() != null && sale.getDiscounts().size() > 0){
            for(OrderItemDiscount discount : sale.getDiscounts()){
                if(discount.getAmount() > 0)
                    totalDiscountAmount = totalDiscountAmount + discount.getAmount();
            }
        }

        if(saleItems != null && saleItems.size() > 0){
            for(OrderItem saleItem : saleItems){
                totalDiscountAmount = totalDiscountAmount + getTotalDiscountAmountOfSaleItem(saleItem);
            }
        }

        return CommonUtils.round(totalDiscountAmount, 2);
    }*/


    private static long getMaxSplitSequenceNumber(List<Transaction> transactions) {
        long maxValue = 0;

        if(transactions != null){
            for(Transaction transaction : transactions){
                if(transaction.getSeqNumber() > maxValue)
                    maxValue = transaction.getSeqNumber();
            }
        }
        return maxValue;
    }

    public static boolean isExistNotCompletedTransaction(List<Transaction> transactions) {
        for(Transaction transaction : transactions){
            if(!transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }

    public static boolean isExistPaymentCompletedTransaction(List<Transaction> transactions) {
        for(Transaction transaction : transactions){
            if(transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }

    public static boolean isDiscountInSale(Order order, Discount discount) {
        for(OrderItemDiscount discount1 : order.getDiscounts()){
            if(discount.getId() == discount1.getId())
                return true;
        }
        return false;
    }

    public static boolean isSaleItemInSale(List<OrderItem> orderItems, OrderItem orderItem) {
        for(OrderItem orderItem1 : orderItems){
            if(orderItem.getId().equals(orderItem1.getId()))
                return true;
        }
        return false;
    }

    public static void setRemainAmount(double amount) {
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();
        order.setRemainAmount(CommonUtils.round(amount, 2));
    }

    public static void setUserIdToOrder(Order order, String userId) {
        if(order.getUserId() == null || order.getUserId().isEmpty())
            order.setUserId(userId);
    }

    @SuppressLint("HardwareIds")
    public static void setDeviceIdToOrder(Context context, Order order) {
        order.setDeviceId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    public static void setRemainAmountByDiscountedAmount() {
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();
        order.setRemainAmount(CommonUtils.round(order.getDiscountedAmount(), 2));
    }

    public static void addDiscount(Order order, Discount discount) {
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();
        for (OrderItem orderItem : orderItems) {

            if (orderItem.getDiscounts() == null)
                orderItem.setDiscounts(new RealmList<>());

            orderItem.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
        }

        if(order.getDiscounts() == null)
            order.setDiscounts(new RealmList<>());

        boolean exist = false;
        for(OrderItemDiscount discount1 : order.getDiscounts()){
            if(discount1.getId() == discount.getId()){
                exist = true;
                break;
            }
        }

        if(!exist)
            order.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));

        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
    }

    @Override
    public void setDiscountedAmountOfSale(){
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();

        double totalAmnt = 0d;
        for(OrderItem orderItem : orderItems)
            totalAmnt = totalAmnt + (orderItem.getAmount() * orderItem.getQuantity());

        order.setTotalAmount(CommonUtils.round(totalAmnt, 2));
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
    }

    public static Transaction addTransactionToOrder(double splitAmount, List<Transaction> transactions, String orderId) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setOrderId(orderId);
        transaction.setTransactionAmount(CommonUtils.round(splitAmount, 2));
        transaction.setSeqNumber(getMaxSplitSequenceNumber(transactions) + 1);
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public void addDiscountToSaleItem(OrderItem orderItem, Discount discount) {
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();

        if(orderItem.getDiscounts() == null)
            orderItem.setDiscounts(new RealmList<>());

        orderItem.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));

        boolean discountExistInOrder = false;

        if(order.getDiscounts() != null){
            for(Iterator<OrderItemDiscount> its = order.getDiscounts().iterator(); its.hasNext();) {
                OrderItemDiscount discount1 = its.next();

                if(discount.getId() == discount1.getId()){
                    discountExistInOrder = true;
                    break;
                }
            }
        }

        if(!discountExistInOrder){
            if(order.getDiscounts() == null)
                order.setDiscounts(new RealmList<>());

            order.getDiscounts().add(ConversionHelper.convertDiscountToOrderItemDiscount(discount));
        }

        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
    }

    @Override
    public void removeDiscountFromSaleItem(OrderItem orderItem, Discount discount) {
        List<OrderItem> orderItems = SaleModelInstance.getInstance().getSaleModel().getOrderItems();
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();

        //Parametre olarak verilen orderItem dan indirim tanımı cıkalrılır
        for(Iterator<OrderItemDiscount> its = orderItem.getDiscounts().iterator(); its.hasNext();) {
            OrderItemDiscount discount1 = its.next();

            if(discount.getId() == discount1.getId()){
                its.remove();
                break;
            }
        }

        //Order icindeki diger itemlarda discount tanımı var mı kontrol edilir
        boolean discountExistAnotherItem = false;
        for(OrderItem orderItem1 : orderItems){

            for(Iterator<OrderItemDiscount> its = orderItem1.getDiscounts().iterator(); its.hasNext();) {
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
            if(order.getDiscounts() != null){

                for(Iterator<OrderItemDiscount> its = order.getDiscounts().iterator(); its.hasNext();) {
                    OrderItemDiscount discount1 = its.next();

                    if(discount.getId() == discount1.getId()){
                        its.remove();
                        break;
                    }
                }
            }
        }
        calculateDiscounts(SaleModelInstance.getInstance().getSaleModel());
    }

    private void addAllDiscountsToSaleItem(OrderItem orderItem) {
        Order order = SaleModelInstance.getInstance().getSaleModel().getOrder();

        if(order.getDiscounts() != null){
            for(OrderItemDiscount discount : order.getDiscounts()){
                if(orderItem.getDiscounts() == null)
                    orderItem.setDiscounts(new RealmList<>());

                orderItem.getDiscounts().add(discount);
            }
        }
    }

    public static double getTotalTipAmountOfSale(SaleModel saleModel){
        double totalTipAmount = 0d;
        for(Transaction transaction : saleModel.getTransactions())
            totalTipAmount = CommonUtils.round(totalTipAmount + transaction.getTipAmount(), 2);

        return totalTipAmount;
    }

    public static boolean isSaleItemRefunded(OrderItem orderItem, String orderId){
        RealmResults<OrderRefundItem> refundsItems = OrderRefundItemDBHelper.getRefundItemsByOrderId(orderId);

        for(OrderRefundItem orderRefundItem : refundsItems){
            if(orderRefundItem.getOrderItemId().equals(orderItem.getId()))
                return true;
        }
        return false;
    }

    public static double getTotalDiscountAmountOfOrderItem(SaleModel saleModel, OrderItem orderItem){
        double totalSaleItemsAmount = 0d;
        double totalDiscountAmountOfOrder = 0d;

        //Tutar tanimli indirimlerde toplam item tutari uzerinden hesaplama yapacagız
        for(OrderItem orderItem1 : saleModel.getOrderItems())
            totalSaleItemsAmount = CommonUtils.round(totalSaleItemsAmount + (orderItem1.getAmount() * orderItem1.getQuantity()), 2);


        totalSaleItemsAmount = CommonUtils.round(totalSaleItemsAmount + (orderItem.getAmount() * orderItem.getQuantity()), 2);

        double totalAmount = CommonUtils.round(orderItem.getAmount() * (double) orderItem.getQuantity(), 2);
        double totalDiscountAmountOfItem = 0d;

        if(orderItem.getDiscounts() != null){
            for (OrderItemDiscount orderItemDiscount : orderItem.getDiscounts()) {

                double discountAmount = 0d;

                if (orderItemDiscount.getRate() > 0d)
                    discountAmount = CommonUtils.round((totalAmount / 100d) * orderItemDiscount.getRate(), 2);
                /*else if (orderItemDiscount.getAmount() > 0d) {
                    //double amount1 = CommonUtils.round((orderItemDiscount.getAmount() / totalSaleItemsAmount), 2);
                    double amount1 = orderItemDiscount.getAmount() / totalSaleItemsAmount;
                    discountAmount = CommonUtils.round(amount1 * (orderItem.getAmount() * orderItem.getQuantity()), 2);
                }*/

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
        for(OrderItem orderItem : saleModel.getOrderItems())
            totalSaleItemsAmount = CommonUtils.round(totalSaleItemsAmount + (orderItem.getAmount() * orderItem.getQuantity()), 2);

        Log.i("Info", "::calculateDiscounts totalSaleItemsAmount:" + totalSaleItemsAmount);

        //Her item ozelinde tanımlı indirim tutarları hesaplanır
        for(OrderItem orderItem : saleModel.getOrderItems()){

            double totalAmount = CommonUtils.round(orderItem.getAmount() * (double) orderItem.getQuantity(), 2);
            double totalDiscountAmountOfItem = 0d;

            if(orderItem.getDiscounts() != null){
                for(OrderItemDiscount orderItemDiscount : orderItem.getDiscounts()){

                    double discountAmount = 0d;

                    if(orderItemDiscount.getRate() > 0d)
                        discountAmount = CommonUtils.round((totalAmount / 100d) * orderItemDiscount.getRate(), 2);
                    else if(orderItemDiscount.getAmount() > 0d){
                        //double amount1 = CommonUtils.round((orderItemDiscount.getAmount() / totalSaleItemsAmount), 2);
                        double amount1 = orderItemDiscount.getAmount() / totalSaleItemsAmount;
                        discountAmount = CommonUtils.round(amount1 * (orderItem.getAmount() * orderItem.getQuantity()), 2);
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

            orderItem.setTotalDiscountAmount(totalDiscountAmountOfItem);
        }
        //Order toplam indirim tutari set edilir
        saleModel.getOrder().setTotalDiscountAmount(totalDiscountAmountOfOrder);

        Log.i("Info", "::calculateDiscounts Discounted amount calc-----------");
        Log.i("Info", "::calculateDiscounts saleModel.getOrder().getTotalAmount():" + saleModel.getOrder().getTotalAmount());
        Log.i("Info", "::calculateDiscounts totalDiscountAmountOfOrder          :" + totalDiscountAmountOfOrder);

        //Order indirimli tutar set edilir
        if(saleModel.getOrder().getTotalAmount() > 0)
            saleModel.getOrder().setDiscountedAmount(CommonUtils.round(saleModel.getOrder().getTotalAmount() - totalDiscountAmountOfOrder, 2));
        else
            saleModel.getOrder().setDiscountedAmount(0d);

        if(saleModel.getOrder().getDiscountedAmount() <= 0)
            saleModel.getOrder().setDiscountedAmount(0d);

        Log.i("Info", "::calculateDiscounts getDiscountedAmount          :" + saleModel.getOrder().getDiscountedAmount());
        Log.i("Info", "::calculateDiscounts getTotalDiscountAmount       :" + saleModel.getOrder().getTotalDiscountAmount());

        //Order a bagli indirim tutarlari set edilir
        for(OrderItemDiscount orderItemDiscount : saleModel.getOrder().getDiscounts()){

            double totalDiscountAmount = 0d;

            for(OrderItem orderItem : saleModel.getOrderItems()){
                if(orderItem.getDiscounts() != null){
                    for(OrderItemDiscount orderItemDiscount1 : orderItem.getDiscounts()){
                        if(orderItemDiscount.getId() == orderItemDiscount1.getId())
                            totalDiscountAmount = CommonUtils.round(totalDiscountAmount + orderItemDiscount1.getDiscountAmount(), 2);
                    }
                }
            }
            orderItemDiscount.setDiscountAmount(totalDiscountAmount);
        }
    }

    public static double getAvailableRefundAmount(SaleModel saleModel){
        RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(saleModel.getOrder().getId(), true);

        double totalRefundAmount = 0d;
        for(Refund refund : refunds){
            totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);
        }

        double totalSaleAmount = 0d;
        for(Transaction transaction : saleModel.getTransactions()){
            if(transaction.getTransactionType() == TransactionTypeEnum.SALE.getId() &&
                transaction.isPaymentCompleted())
                totalSaleAmount = CommonUtils.round(totalSaleAmount + transaction.getTotalAmount(), 2);
        }

        return CommonUtils.round(totalSaleAmount - totalRefundAmount, 2);
    }
}
