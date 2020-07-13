package com.paypad.vuk507.model.pojo;

import android.util.Log;

import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.utils.ConversionHelper;
import com.paypad.vuk507.utils.DataUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;

public class SaleModel implements Serializable {

    private Sale sale;
    private List<SaleItem> saleItems;
    private List<Transaction> transactions;

    public SaleModel() {
        sale = new Sale();
        sale.setDiscounts(new RealmList<>());
        setTransactions(new ArrayList<>());
        setSaleItems(new ArrayList<>());
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;

    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /*public void setSaleUserUuid(String uuid){
        if(sale.getUserUuid() == null || sale.getUserUuid().isEmpty())
            sale.setUserUuid(uuid);
    }*/

    /*public String addProduct(Product product, double amount, boolean isDynamicAmount){
        SaleItem saleItem = new SaleItem();
        saleItem.setProductId(product.getId());
        saleItem.setName(product.getName());
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setDynamicAmount(isDynamicAmount);
        saleItem.setSaleUuid(sale.getSaleUuid());

        setOrderItemTaxForProduct(saleItem, product);

        sale.setSaleCount(getSaleCount() + 1);

        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmountIncludingTax());

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }*/

    /*public void setOrderItemTaxForCustomItem(SaleItem saleItem,TaxModel taxModel) {

        if(saleItem.getOrderItemTaxes() != null && saleItem.getOrderItemTaxes().size() > 0)
            saleItem.getOrderItemTaxes().clear();

        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);

        if(saleItem.getOrderItemTaxes() == null)
            saleItem.setOrderItemTaxes(new RealmList<>());

        saleItem.getOrderItemTaxes().add(orderItemTax);
        saleItem.setAmountIncludingTax(saleItem.getAmount() + ((saleItem.getAmount() / 100d) * taxModel.getTaxRate()));
    }*/

    /*public void setOrderItemTaxForProduct(SaleItem saleItem, Product product) {

        if(saleItem.getOrderItemTaxes() != null && saleItem.getOrderItemTaxes().size() > 0)
            saleItem.getOrderItemTaxes().clear();

        TaxModel taxModel = DataUtils.getTaxModelById(product.getTaxId());

        OrderItemTax orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);

        if(saleItem.getOrderItemTaxes() == null)
            saleItem.setOrderItemTaxes(new RealmList<>());

        saleItem.getOrderItemTaxes().add(orderItemTax);

        saleItem.setAmountIncludingTax(saleItem.getAmount());
    }*/

    /*public OrderItemTax getOrderItemTax(TaxModel taxModelx) {

        if(taxModelx == null)
            return null;

        String orderItemTaxId = UUID.randomUUID().toString();

        TaxModel taxModel;

        if(taxModelx.getId() < 0){
            TaxRateEnum taxRateEnum = TaxRateEnum.getById(taxModelx.getId());
            taxModel = new TaxModel();
            taxModel.setId(taxRateEnum.getId());
            taxModel.setName(taxRateEnum.getLabel());
            taxModel.setTaxRate(taxRateEnum.getRateValue());
        }else
            taxModel = TaxDBHelper.getTax(taxModelx.getId());

        OrderItemTax orderItemTax = new OrderItemTax();
        orderItemTax.setOrderItemTaxId(orderItemTaxId);
        orderItemTax.setName(taxModel.getName());
        orderItemTax.setTaxRate(taxModel.getTaxRate());
        return orderItemTax;
    }*/

    /*public String addCustomAmount(String name, double amount, String note, TaxModel taxModel){
        SaleItem saleItem = new SaleItem();
        saleItem.setName(name);
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setDynamicAmount(true);
        saleItem.setNote(note != null ? note : "");
        saleItem.setSaleUuid(sale.getSaleUuid());


        setOrderItemTaxForCustomItem(saleItem, taxModel);


        //sale.getSaleIds().add(saleItem.getUuid());
        sale.setSaleCount(getSaleCount() + 1);
        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmount());



        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        Log.i("Info", "::addCustomAmount -> totaAmount:" + sale.getTotalAmount());

        return saleItem.getUuid();
    }*/

    /*public String addCustomItem(SaleItem saleItem, TaxModel taxModel){
        setOrderItemTaxForCustomItem(saleItem, taxModel);

        //saleItem.setAmount(saleItem.getAmount() + ((saleItem.getAmount() / 100d) * taxModel.getTaxRate()));



        sale.setSaleCount(getSaleCount() + 1);
        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmountIncludingTax());

        addAllDiscountsToSaleItem(saleItem);
        saleItems.add(saleItem);

        setDiscountedAmountOfSale();

        return saleItem.getUuid();
    }*/

    /* Burada herhangi bir satisa ekleme yapilmaz. Keypad den girilen her tutar degerinde
       Charge butonu uzerinde gorunecek tutar icin bir hesaplama yapilir.
       Hesaplanan indirimli tutar geri donulur */
    /*public double getDynamicCustomAmount(String name, double amount, String note){
        SaleItem saleItem = new SaleItem();
        saleItem.setName(name);
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setNote(note != null ? note : "");

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
    }*/

    /*public double getDynamicCustomAmount2(SaleItem saleItem){
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
    }*/

    /*private void addAllDiscountsToSaleItem(SaleItem saleItem) {
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
    }*/

    /*public void removeSaleItem(String uuid){
        int index = 0;
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItems.remove(index);
                break;
            }
            index++;
        }
        //sale.getSaleIds().remove(uuid);
        sale.setSaleCount(getSaleCount() - 1);
    }*/

    public void updateNoteToSaleItem(String uuid, String note){
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItem.setNote(note);
                break;
            }
        }
    }

    public void setQuantityOfSaleItem(String uuid, int quantity){
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItem.setQuantity(quantity);
                break;
            }
        }
    }

    public void updateAmountOfSaleItem(String uuid, double amount){
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItem.setAmount(amount);
                break;
            }
        }
    }

    /*public void addDiscountRateToAllSaleItems(Discount discount){
        for(SaleItem saleItem : saleItems){

            if(saleItem.getDiscounts() == null)
                saleItem.setDiscounts(new RealmList<>());

            saleItem.getDiscounts().add(discount);
        }
        sale.getDiscounts().add(discount);
        setDiscountedAmountOfSale();
    }*/

    /*public void addDiscountAmount(Discount discount){
        sale.getDiscounts().add(discount);
        setDiscountedAmountOfSale();
    }*/


    /*public void addDiscountToSaleItem(String saleItemUuid, Discount discount){

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
    }*/

    /*public void removeDiscountFromSaleItem(String saleItemUuid, Discount discount){
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
    }*/

    public void removeDiscountAmount(Discount discount){
        sale.getDiscounts().remove(discount);
    }

    public void addDiscountRateToSingleSaleItems(String uuid, Discount discount){
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItem.getDiscounts().add(discount);
                break;
            }
        }
    }

    public void removeDiscountRateFromSingleSaleItems(String uuid, Discount discount){
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItem.getDiscounts().remove(discount);
                break;
            }
        }
    }

    /*public double getTotalDiscountAmountOfSale(){
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
    }*/

    /*public void setDiscountedAmountOfSale(){
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
    }*/

    /*public double getTotalDiscountAmountOfSaleItem(SaleItem saleItem){
        double totalAmount = saleItem.getAmountIncludingTax() * (double) saleItem.getQuantity();
        double discountAmount = 0d;

        if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
            for(Discount discount : saleItem.getDiscounts()){
                discountAmount = discountAmount + ((totalAmount / 100d)  * discount.getRate());
                totalAmount = totalAmount - discountAmount;
            }
        }
        return discountAmount;
    }*/

    /*public int getSaleCount(){
        int totalSaleCount = 0;
        for(SaleItem saleItem : saleItems)
            totalSaleCount = totalSaleCount + saleItem.getQuantity();

        sale.setSaleCount(totalSaleCount);
        return totalSaleCount;
    }*/

    /*public void setRemainAmount(){
        sale.setRemainAmount(sale.getDiscountedAmount());
    }*/

    /*public long getMaxSplitId(){
        long maxValue = 0;

        if(getTransactions() != null){
            for(Transaction transaction : getTransactions()){
                if(transaction.getSeqNumber() > maxValue)
                    maxValue = transaction.getSeqNumber();
            }
        }
        return maxValue;
    }*/

    /*public Transaction getTransactionWillBePaid(){
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
    }*/

    /*public  double getTotalDiscountedAmount(){
        double amountx = sale.getTotalAmount() - getTotalDiscountAmountOfSale();
        return  amountx;
    }*/

    /*public boolean isExistNotCompletedTransaction(){

        for(Transaction transaction : transactions){
            if(!transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }*/

    /*public boolean isExistPaymentCompletedTransaction(){
        for(Transaction transaction : transactions){
            if(transaction.isPaymentCompleted())
                return true;
        }
        return false;
    }*/

    /*public boolean isDiscountInSale(Discount discount){
        for(Discount discount1 : sale.getDiscounts()){
            if(discount.getId() == discount1.getId())
                return true;
        }
        return false;
    }*/

    /*public boolean isSaleItemInSale(SaleItem saleItem){
        for(SaleItem saleItem1 : saleItems){
            if(saleItem.getUuid().equals(saleItem1.getUuid()))
                return true;
        }
        return false;
    }*/
}
