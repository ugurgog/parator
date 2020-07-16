package com.paypad.vuk507.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.ReportDiscountModel;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.model.pojo.ReportOrderItem;
import com.paypad.vuk507.model.pojo.SaleModelInstance;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class LogUtil {

    public static void logTransactions(List<Transaction> transactions){

        try{
            for(Transaction transaction : transactions){
                Log.i("Info", "::logTransactions Transactions +++++++++++++++++++++++++");
                Log.i("Info", "::logTransactions Transaction: SaleUuid         :" + transaction.getSaleUuid() );
                Log.i("Info", "::logTransactions Transaction: TransactionUuid  :" + transaction.getTransactionUuid() );
                Log.i("Info", "::logTransactions Transaction: SeqNumber        :" + transaction.getSeqNumber() );
                Log.i("Info", "::logTransactions Transaction: TransactionAmount:" + transaction.getTransactionAmount() );
                Log.i("Info", "::logTransactions Transaction: CashAmount       :" + transaction.getCashAmount() );
                Log.i("Info", "::logTransactions Transaction: ChangeAmount     :" + transaction.getChangeAmount() );
                Log.i("Info", "::logTransactions Transaction: CreateDate       :" + transaction.getCreateDate() );
                Log.i("Info", "::logTransactions Transaction: PaymentTypeId    :" + transaction.getPaymentTypeId() );
                Log.i("Info", "::logTransactions Transaction: TipAmount        :" + transaction.getTipAmount() );
                Log.i("Info", "::logTransactions Transaction: TotalAmount      :" + transaction.getTotalAmount() );
                Log.i("Info", "::logTransactions Transaction: PaymentCompleted :" + transaction.isPaymentCompleted() );
            }
        }catch (Exception e){

        }
    }

    public static void logSale(Sale sale){
        try{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

            Log.i("Info", "::logSale Transactions +++++++++++++++++++++++++");
            Log.i("Info", "::logSale CustomerId:" + sale.getCustomerId() +
                    ", Discounts:" + sale.getDiscounts().toString() +
                    ", DiscountedAmount:" + sale.getSubTotalAmount() +
                    ", TotalAmount:" + sale.getTotalAmount() +
                    ", SaleCount:" + sale.getSaleCount() +
                    ", SaleUuid:" + sale.getSaleUuid() +
                    ", RemainAmount:" + sale.getRemainAmount() +
                    ", UserUuid:" + sale.getUserUuid() +
                    ", CreateDate:" + simpleDateFormat.format(sale.getCreateDate()) +
                    ", PaymentCompleted:" + sale.isPaymentCompleted() +
                    ", DeviceId:" + sale.getDeviceId());
        }catch (Exception e){

        }
    }

    public static void logTransaction(Transaction transaction){
        try{

            Log.i("Info", "::logTransaction +++++++++++++++++++++++++");
            Log.i("Info", "::logTransaction SaleUuid:" + transaction.getSaleUuid() +
                    ", TransactionUuid:" + transaction.getTransactionUuid() +
                    ", SeqNumber:" + transaction.getSeqNumber() +
                    ", TransactionAmount:" + transaction.getTransactionAmount() +
                    ", TotalAmount:" + transaction.getTotalAmount() +
                    ", TipAmount:" + transaction.getTipAmount() +
                    ", PaymentTypeId:" + transaction.getPaymentTypeId() +
                    ", CreateDate:" + transaction.getCreateDate() +
                    ", ChangeAmount:" + transaction.getChangeAmount() +
                    ", CashAmount:" + transaction.getCashAmount() +
                    ", UserUuid:" + transaction.getUserUuid() +
                    ", isMailSend:" + transaction.isMailSend() +
                    ", mailAddress:" + transaction.getMailAdress());
        }catch (Exception e){

        }
    }

    public static void logDynamicBoxList(List<DynamicBoxModel> dynamicBoxModelList){
        try{
            for(DynamicBoxModel dynamicBoxModel : dynamicBoxModelList){
                Log.i("Info", "::logDynamicBoxList +++++++++++++++++++++++++");
                Log.i("Info", "::logDynamicBoxList itemId:" + dynamicBoxModel.getItemId() +
                        ", StructId:" +  dynamicBoxModel.getStructId() +
                        ", createUserId:" +  dynamicBoxModel.getCreateUserId()+
                        ", createDate:" + dynamicBoxModel.getCreateDate());
            }

        }catch (Exception e){

        }
    }

    public static void logReportModel(ReportModel reportModel) {
        try {
            Log.i("Info", "::logReportModel  +++++++++++++++++++++++++");
            Log.i("Info", "::logReportModel startDate         :" + reportModel.getStartDate());
            Log.i("Info", "::logReportModel endDate           :" + reportModel.getEndDate());
            Log.i("Info", "::logReportModel grossSalesAmount  :" + reportModel.getGrossSalesAmount());
            Log.i("Info", "::logReportModel refundsAmount     :" + reportModel.getRefundsAmount());
            Log.i("Info", "::logReportModel discountAmount    :" + reportModel.getDiscountAmount());
            Log.i("Info", "::logReportModel netSalesAmount    :" + reportModel.getNetSalesAmount());
            Log.i("Info", "::logReportModel taxAmount         :" + reportModel.getTaxAmount());
            Log.i("Info", "::logReportModel tipsAmount        :" + reportModel.getTipsAmount());
            Log.i("Info", "::logReportModel refundsByAmount   :" + reportModel.getRefundsByAmount());
            Log.i("Info", "::logReportModel totalAmount       :" + reportModel.getTotalAmount());
            Log.i("Info", "::logReportModel saleCount         :" + reportModel.getSaleCount());
            Log.i("Info", "::logReportModel cashAmount        :" + reportModel.getCashAmount());
            Log.i("Info", "::logReportModel cardAmount        :" + reportModel.getCardAmount());
            Log.i("Info", "::logReportModel feeAmount         :" + reportModel.getFeeAmount());


            Log.i("Info", "::logReportModel discounts-------------------");

            for (Map.Entry<Long, ReportDiscountModel> entry : reportModel.getDiscounts().entrySet()) {

                ReportDiscountModel reportDiscountModel = entry.getValue();

                Log.i("Info", "::logReportModel key:" + entry.getKey() +
                        " discountName:" + reportDiscountModel.getDiscountName() +
                        " discountAmount:" + reportDiscountModel.getTotalDiscountAmount());

            }

            Log.i("Info", "::logReportModel orderItems-------------------");

            for (Map.Entry<String, List<ReportOrderItem>> entry : reportModel.getReportOrderItems().entrySet()) {

                List<ReportOrderItem> orderItems = entry.getValue();

                Log.i("Info", "::logReportModel key-----------:" + entry.getKey());

                for(ReportOrderItem reportOrderItem : orderItems){

                    Log.i("Info", "::logReportModel productId  :" + reportOrderItem.getProductId() +
                        " saleCount  :" + reportOrderItem.getSaleCount() +
                        " grossAmount:" + reportOrderItem.getGrossAmount()+
                        " itemName   :" + reportOrderItem.getItemName());

                }

            }

        } catch (Exception e) {

        }
    }
}
