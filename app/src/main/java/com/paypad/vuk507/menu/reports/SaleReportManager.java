package com.paypad.vuk507.menu.reports;

import android.util.Log;

import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.DiscountPojo;
import com.paypad.vuk507.model.pojo.ReportDiscountModel;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.model.pojo.ReportOrderItem;
import com.paypad.vuk507.model.pojo.SaleItemPojo;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ConversionHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleReportManager {

    private List<SaleModel> saleModels = new ArrayList<>();
    private ReportModel reportModel;

    public SaleReportManager(List<SaleModel> saleModels, Date startDate, Date endDate) {
        this.saleModels = saleModels;
        reportModel = new ReportModel();
        reportModel.setStartDate(startDate);
        reportModel.setEndDate(endDate);
    }

    public ReportModel getReportModel(){

        setGroosAmount();
        setDiscounts();
        setTotalDiscountAmount();
        setRefundsAmount();
        setNetSalesAmount();
        setTaxAmount();
        setTotalTipAmount();
        setRefundsByAmount();
        setTotalAmount();
        setSaleCount();
        setAmountsByPaymentType();
        setFeeAmount();
        setOrderItems();
        setTopItems();
        setAverageSaleAmount();

        return reportModel;
    }




    private void setGroosAmount() {
        double grossSalesAmount = 0d;
        for(SaleModel saleModel : saleModels){
            for(SaleItem saleItem: saleModel.getSaleItems()){
                grossSalesAmount = grossSalesAmount + saleItem.getGrossAmount();
            }
        }
        reportModel.setGrossSalesAmount(grossSalesAmount);
    }

    private void setRefundsAmount() {
        reportModel.setRefundsAmount(0d);
    }

    private void setRefundsByAmount() {
        reportModel.setRefundsByAmount(0d);
    }

    private void setDiscounts() {
        Map<Long, ReportDiscountModel> discountModelMap = new HashMap<>();

        for(SaleModel saleModel : saleModels){

            for(Discount discount : saleModel.getSale().getDiscounts()){

                if(discount.getAmount() > 0d){
                    if(!discountModelMap.containsKey(discount.getId())){
                        ReportDiscountModel reportDiscountModel = new ReportDiscountModel();
                        reportDiscountModel.setDiscountName(discount.getName());
                        reportDiscountModel.setTotalDiscountAmount(discount.getAmount());
                        discountModelMap.put(discount.getId(), reportDiscountModel);
                    }else {
                        ReportDiscountModel reportDiscountModel = discountModelMap.get(discount.getId());
                        reportDiscountModel.setTotalDiscountAmount(reportDiscountModel.getTotalDiscountAmount() + discount.getAmount());
                    }
                }else {

                    List<SaleItemPojo> saleItems = new ArrayList<>();

                    for(SaleItem saleItem : saleModel.getSaleItems()){
                        SaleItemPojo saleItem1 = new SaleItemPojo();

                        List<DiscountPojo> discountPojos = new ArrayList<>();

                        for(Discount discount1 : saleItem.getDiscounts()){
                            discountPojos.add(ConversionHelper.convertDiscountToDiscountPojo(discount1));
                        }

                        saleItem1.setDiscounts(discountPojos);
                        saleItem1.setAmount(saleItem.getAmount() * saleItem.getQuantity());
                        saleItems.add(saleItem1);
                    }

                    for(SaleItemPojo saleItem : saleItems){
                        for(DiscountPojo discount1 : saleItem.getDiscounts()){
                            if(discount.getId() == discount1.getId()){
                                double discountAmount = CommonUtils.round(((saleItem.getAmount() / 100d)  * discount.getRate()), 2);
                                saleItem.setAmount(CommonUtils.round(saleItem.getAmount() - discountAmount, 2));
                                discount1.setAmount(discountAmount);
                                break;
                            }
                        }
                    }

                    if(!discountModelMap.containsKey(discount.getId())){
                        ReportDiscountModel reportDiscountModel = new ReportDiscountModel();
                        reportDiscountModel.setDiscountName(discount.getName());

                        for(SaleItemPojo saleItem:saleItems){

                            for(DiscountPojo discount1: saleItem.getDiscounts()){

                                if(discount1.getId() == discount.getId()){

                                    reportDiscountModel.setTotalDiscountAmount(reportDiscountModel.getTotalDiscountAmount() +
                                            discount1.getAmount());
                                }
                            }
                        }
                        discountModelMap.put(discount.getId(), reportDiscountModel);
                    }else {
                        ReportDiscountModel reportDiscountModel = discountModelMap.get(discount.getId());

                        for(SaleItemPojo saleItem:saleItems){

                            for(DiscountPojo discount1: saleItem.getDiscounts()){

                                if(discount1.getId() == discount.getId()){

                                    reportDiscountModel.setTotalDiscountAmount(reportDiscountModel.getTotalDiscountAmount() +
                                            discount1.getAmount());
                                }
                            }
                        }


                        //reportDiscountModel.setTotalDiscountAmount(reportDiscountModel.getTotalDiscountAmount() + discount.getAmount());
                    }
                }
            }
        }
        reportModel.setDiscounts(discountModelMap);
    }

    private void setTotalDiscountAmount() {

        double totalDiscountAmount = 0d;
        for (Map.Entry<Long, ReportDiscountModel> entry : reportModel.getDiscounts().entrySet()) {

            ReportDiscountModel reportDiscountModel = entry.getValue();
            totalDiscountAmount = totalDiscountAmount + reportDiscountModel.getTotalDiscountAmount();
        }
        reportModel.setDiscountAmount(totalDiscountAmount);
    }

    private void setNetSalesAmount() {
        reportModel.setNetSalesAmount(CommonUtils.round(reportModel.getGrossSalesAmount() - reportModel.getDiscountAmount(), 2));
    }

    private void setTaxAmount() {
        double totalTaxAmount = 0d;
        for(SaleModel saleModel : saleModels){
            for(SaleItem saleItem : saleModel.getSaleItems()){
                totalTaxAmount = CommonUtils.round(totalTaxAmount + saleItem.getTaxAmount(), 2);
            }
        }
        reportModel.setTaxAmount(totalTaxAmount);
    }

    private void setTotalTipAmount(){
        double totalTipAmount = 0d;
        for(SaleModel saleModel : saleModels){
            for(Transaction transaction : saleModel.getTransactions()){
                totalTipAmount = CommonUtils.round(totalTipAmount + transaction.getTipAmount(), 2);
            }
        }
        reportModel.setTipsAmount(totalTipAmount);
    }

    private void setTotalAmount() {
        reportModel.setTotalAmount(CommonUtils.round(reportModel.getNetSalesAmount() +
                reportModel.getTaxAmount() +
                reportModel.getTipsAmount(), 2));
    }

    private void setSaleCount() {
        reportModel.setSaleCount(saleModels.size());
    }

    private void setAmountsByPaymentType() {
        for(SaleModel saleModel : saleModels){
            for(Transaction transaction : saleModel.getTransactions()){
                if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                    reportModel.setCashAmount(CommonUtils.round(reportModel.getCashAmount() + transaction.getTransactionAmount(), 2));
                }else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){
                    reportModel.setCardAmount(CommonUtils.round(reportModel.getCardAmount() +
                            (transaction.getTransactionAmount() + transaction.getTipAmount()), 2));
                }
            }
        }
    }
    private void setFeeAmount(){
        reportModel.setFeeAmount(0d);
    }

    private void setOrderItems(){
        Map<String, List<ReportOrderItem>> reportOrderItems = new HashMap<>();

        for(SaleModel saleModel: saleModels){

            for(SaleItem saleItem : saleModel.getSaleItems()){

                Log.i("Info", "setOrderItems saleItemName:" + saleItem.getName());

                if(!reportOrderItems.containsKey(saleItem.getCategoryName())){

                    ReportOrderItem orderItem = new ReportOrderItem();
                    orderItem.setProductId(saleItem.getProductId());
                    orderItem.setItemName(saleItem.getName());
                    orderItem.setGrossAmount(saleItem.getGrossAmount() * saleItem.getQuantity());
                    orderItem.setSaleCount(saleItem.getQuantity());

                    List<ReportOrderItem> orderItems = new ArrayList<>();
                    orderItems.add(orderItem);

                    reportOrderItems.put(saleItem.getCategoryName(), orderItems);
                }else {
                    List<ReportOrderItem> orderItems = reportOrderItems.get(saleItem.getCategoryName());

                    boolean productExist = false;

                    for(ReportOrderItem reportOrderItem : orderItems){
                        if(saleItem.getProductId() == reportOrderItem.getProductId()){
                            productExist = true;
                            reportOrderItem.setSaleCount(reportOrderItem.getSaleCount() + saleItem.getQuantity());
                            reportOrderItem.setGrossAmount(reportOrderItem.getGrossAmount()
                                    + (saleItem.getGrossAmount() * saleItem.getQuantity()));
                            break;
                        }
                    }

                    if(!productExist){
                        ReportOrderItem orderItem = new ReportOrderItem();
                        orderItem.setProductId(saleItem.getProductId());
                        orderItem.setItemName(saleItem.getName());
                        orderItem.setGrossAmount(saleItem.getGrossAmount() * saleItem.getQuantity());
                        orderItem.setSaleCount(saleItem.getQuantity());
                        orderItems.add(orderItem);
                    }
                }
            }
        }
        reportModel.setReportOrderItems(reportOrderItems);
    }

    private void setTopItems(){

        List<ReportOrderItem> reportOrderItems = new ArrayList<>();

        for (Map.Entry<String, List<ReportOrderItem>> entry : reportModel.getReportOrderItems().entrySet()) {

            List<ReportOrderItem>  reportOrderList = entry.getValue();

            for(ReportOrderItem orderItem : reportOrderList){

                boolean isExist = false;
                for(ReportOrderItem orderItem1 : reportOrderItems){

                    if(orderItem1.getProductId() == orderItem.getProductId()){
                        isExist = true;
                        orderItem.setGrossAmount(orderItem.getGrossAmount() + orderItem1.getGrossAmount());
                        orderItem.setSaleCount(orderItem.getSaleCount() + orderItem1.getSaleCount());
                        break;
                    }
                }

                if(!isExist)
                    reportOrderItems.add(orderItem);
            }
        }
        reportModel.setTopItems(reportOrderItems);
    }

    private void setAverageSaleAmount(){
        reportModel.setAverageSaleAmount(
                CommonUtils.round(reportModel.getSaleCount() == 0 ? 0 : reportModel.getGrossSalesAmount() / reportModel.getSaleCount(), 2)
        );
    }
}
