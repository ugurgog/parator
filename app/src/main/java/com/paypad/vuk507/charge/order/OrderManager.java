package com.paypad.vuk507.charge.order;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ConversionHelper;
import com.paypad.vuk507.utils.DataUtils;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;

public class OrderManager implements IOrderManager{

    @Override
    public String addProductToOrder(Product product, double amount, boolean isDynamicAmount) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();

        SaleItem saleItem = new SaleItem();
        saleItem.setProductId(product.getId());
        saleItem.setName(product.getName());
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setDynamicAmount(isDynamicAmount);
        saleItem.setSaleUuid(sale.getSaleUuid());

        setOrderItemTaxForProduct(saleItem, product);

        sale.setSaleCount(getOrderItemCount() + 1);

        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmountIncludingTax());

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }

    @Override
    public String addCustomItemToOrder(SaleItem saleItem, TaxModel taxModel) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();

        setOrderItemTaxForCustomItem(saleItem, taxModel);

        sale.setSaleCount(getOrderItemCount() + 1);
        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmountIncludingTax());

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }

    @Override
    public void setOrderItemTaxForProduct(SaleItem saleItem, Product product) {
        if(saleItem.getOrderItemTaxes() != null && saleItem.getOrderItemTaxes().size() > 0)
            saleItem.getOrderItemTaxes().clear();

        TaxModel taxModel = DataUtils.getTaxModelById(product.getTaxId());

        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);

        if(saleItem.getOrderItemTaxes() == null)
            saleItem.setOrderItemTaxes(new RealmList<>());

        saleItem.getOrderItemTaxes().add(orderItemTax);
        saleItem.setAmountIncludingTax(saleItem.getAmount());
    }

    @Override
    public void setOrderItemTaxForCustomItem(SaleItem saleItem, TaxModel taxModel) {
        if(saleItem.getOrderItemTaxes() != null && saleItem.getOrderItemTaxes().size() > 0)
            saleItem.getOrderItemTaxes().clear();

        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);

        if(saleItem.getOrderItemTaxes() == null)
            saleItem.setOrderItemTaxes(new RealmList<>());

        saleItem.getOrderItemTaxes().add(orderItemTax);
        saleItem.setAmountIncludingTax(saleItem.getAmount() + ((saleItem.getAmount() / 100d) * taxModel.getTaxRate()));
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
            for (Discount discount : sale.getDiscounts()) {
                if (discount.getRate() > 0d) {

                    if (saleItem.getDiscounts() == null)
                        saleItem.setDiscounts(new RealmList<>());

                    saleItem.getDiscounts().add(discount);
                }
            }
            discountedAmount = totalAmount - getTotalDiscountAmountOfSale();
            discountedAmount = discountedAmount - getTotalDiscountAmountOfSaleItem(saleItem);
        }

        return discountedAmount;
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
        for(Discount discount1 : sale.getDiscounts()){
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
        sale.setRemainAmount(amount);
    }

    @Override
    public void addDiscountToOrder(Discount discount) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        sale.getDiscounts().add(discount);
        setDiscountedAmountOfSale();
    }

    @Override
    public void setUserIdToOrder(String userId) {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        if(sale.getUserUuid() == null || sale.getUserUuid().isEmpty())
            sale.setUserUuid(userId);
    }

    @Override
    public void setRemainAmountByDiscountedAmount() {
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();
        sale.setRemainAmount(sale.getDiscountedAmount());
    }

    @Override
    public void addDiscountRateToAllSaleItems(Discount discount) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        for(SaleItem saleItem : saleItems){

            if(saleItem.getDiscounts() == null)
                saleItem.setDiscounts(new RealmList<>());

            saleItem.getDiscounts().add(discount);
        }
        sale.getDiscounts().add(discount);
        setDiscountedAmountOfSale();
    }

    @Override
    public void setDiscountedAmountOfSale(){
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        double totalAmnt = 0d;
        for(SaleItem saleItem : saleItems)
            totalAmnt = totalAmnt + (saleItem.getAmountIncludingTax() * saleItem.getQuantity());

        sale.setTotalAmount(totalAmnt);

        if(sale.getTotalAmount() > 0)
            sale.setDiscountedAmount(sale.getTotalAmount() - getTotalDiscountAmountOfSale());
        else
            sale.setDiscountedAmount(0d);

        if(sale.getDiscountedAmount() <= 0)
            sale.setDiscountedAmount(0d);
    }

    @Override
    public Transaction addTransactionToOrder(double splitAmount) {
        List<Transaction> transactions = SaleModelInstance.getInstance().getSaleModel().getTransactions();

        Transaction transaction = new Transaction();
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setSaleUuid(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid());
        transaction.setTransactionAmount(splitAmount);
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
            for(Discount discount : sale.getDiscounts()){
                if(discount.getAmount() > 0)
                    totalDiscountAmount = totalDiscountAmount + discount.getAmount();
            }
        }

        if(saleItems != null && saleItems.size() > 0){
            for(SaleItem saleItem : saleItems){
                totalDiscountAmount = totalDiscountAmount + getTotalDiscountAmountOfSaleItem(saleItem);
            }
        }

        return totalDiscountAmount;
    }

    @Override
    public void addDiscountToSaleItem(String saleItemUuid, Discount discount) {
        List<SaleItem> saleItems = SaleModelInstance.getInstance().getSaleModel().getSaleItems();
        Sale sale = SaleModelInstance.getInstance().getSaleModel().getSale();

        if(saleItems != null){
            for(SaleItem saleItem : saleItems){
                if(saleItem.getUuid().equals(saleItemUuid)){
                    saleItem.getDiscounts().add(discount);
                    break;
                }
            }
        }

        if(sale.getDiscounts() != null){
            if(!sale.getDiscounts().contains(discount))
                sale.getDiscounts().add(discount);
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
            for(Discount discount : sale.getDiscounts()){
                if(discount.getRate() > 0d){

                    if(saleItem.getDiscounts() == null)
                        saleItem.setDiscounts(new RealmList<>());

                    saleItem.getDiscounts().add(discount);
                }
            }
            setDiscountedAmountOfSale();
        }
    }

    public double getTotalDiscountAmountOfSaleItem(SaleItem saleItem){
        double totalAmount = saleItem.getAmountIncludingTax() * (double) saleItem.getQuantity();
        double discountAmount = 0d;

        if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
            for(Discount discount : saleItem.getDiscounts()){
                discountAmount = discountAmount + ((totalAmount / 100d)  * discount.getRate());
                totalAmount = totalAmount - discountAmount;
            }
        }
        return discountAmount;
    }
}
