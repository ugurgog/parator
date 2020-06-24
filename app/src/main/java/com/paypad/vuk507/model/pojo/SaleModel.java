package com.paypad.vuk507.model.pojo;

import android.util.Log;

import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Split;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;

public class SaleModel {

    private Sale sale;
    private List<SaleItem> saleItems;
    private List<Split> splits;

    public SaleModel() {
        sale = new Sale();
        saleItems = new ArrayList<>();
        splits = new ArrayList<>();
        sale.setSaleIds(new RealmList<>());
        sale.setDiscounts(new RealmList<>());
        sale.setSplitIds(new RealmList<>());
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

    public List<Split> getSplits() {
        return splits;
    }

    public void setSplits(List<Split> splits) {
        this.splits = splits;
    }

    public String addProduct(Product product, double amount){
        SaleItem saleItem = new SaleItem();
        saleItem.setProductId(product.getId());
        saleItem.setName(product.getName());
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setTaxRate(product.getTaxId() < 0 ? TaxRateEnum.getById(product.getTaxId()).getRateValue() : TaxDBHelper.getTax(product.getTaxId()).getTaxRate());
        saleItems.add(saleItem);
        sale.getSaleIds().add(saleItem.getUuid());
        sale.setSaleCount(sale.getSaleCount() + 1);
        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmount());
        setSaleTotalAmount(saleItem);

        Log.i("Info", "::addProduct -> totaAmount:" + sale.getTotalAmount());

        return saleItem.getUuid();
    }

    public String addCustomAmount(String name, double amount, String note){
        SaleItem saleItem = new SaleItem();
        saleItem.setName(name);
        saleItem.setUuid(UUID.randomUUID().toString());
        saleItem.setAmount(amount);
        saleItem.setQuantity(1);
        saleItem.setNote(note != null ? note : "");
        saleItems.add(saleItem);
        sale.getSaleIds().add(saleItem.getUuid());
        sale.setSaleCount(sale.getSaleCount() + 1);
        sale.setTotalAmount(sale.getTotalAmount() + saleItem.getAmount());
        setSaleTotalAmount(saleItem);

        Log.i("Info", "::addCustomAmount -> totaAmount:" + sale.getTotalAmount());

        return saleItem.getUuid();
    }

    public void removeSaleItem(String uuid){
        int index = 0;
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItems.remove(index);
                break;
            }
            index++;
        }
        sale.getSaleIds().remove(uuid);
        sale.setSaleCount(sale.getSaleCount() - 1);
    }

    public void addTaxToCustomAmount(String uuid, double taxRate){
        for(SaleItem saleItem : saleItems){
            if(saleItem.getUuid().equals(uuid)){
                saleItem.setTaxRate(taxRate);
                break;
            }
        }
    }

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

    public void addDiscountRateToAllSaleItems(Discount discount){
        for(SaleItem saleItem : saleItems){

            if(saleItem.getDiscounts() == null)
                saleItem.setDiscounts(new RealmList<>());

            saleItem.getDiscounts().add(discount);



            setSaleTotalAmount(saleItem);
        }
        sale.getDiscounts().add(discount);
    }

    public void addDiscountAmount(Discount discount){
        sale.getDiscounts().add(discount);

        double amount = sale.getTotalAmount() - discount.getAmount();

        if(amount < 0d)
            amount = 0d;

        sale.setTotalAmount(amount);
    }

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

    public void setSaleTotalAmount(SaleItem saleItem){
        /*double totalAmount = saleItem.getAmount();
        double discountAmount = 0d;

        if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
            for(Discount discount : saleItem.getDiscounts()){

                discountAmount = discountAmount + ((totalAmount / 100d)  * discount.getRate());

                Log.i("Info", "::setSaleTotalAmount -> discountAmount_1:" + discountAmount);

                totalAmount = totalAmount - discountAmount;
                Log.i("Info", "::setSaleTotalAmount -> totalAmount:" + totalAmount);
            }
        }

        Log.i("Info", "::setSaleTotalAmount -> discountAmount:" + discountAmount);
        Log.i("Info", "::setSaleTotalAmount -> sale.getTotalAmount()_1:" + sale.getTotalAmount());*/


        double discountAmount = getTotalDiscountAmountOfSaleItem(saleItem);
        sale.setTotalAmount(sale.getTotalAmount() - discountAmount);
        Log.i("Info", "::setSaleTotalAmount -> sale.getTotalAmount()_2:" + sale.getTotalAmount());
    }

    public double getTotalDiscountAmountOfSale(){
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

    public double getTotalDiscountAmountOfSaleItem(SaleItem saleItem){
        double totalAmount = saleItem.getAmount();
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
