package com.paypad.vuk507.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.paypad.vuk507.model.AutoIncrement;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Order;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.ReportDiscountModel;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.model.pojo.ReportOrderItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class LogUtil {

    public static void logTransactions(List<Transaction> transactions){

        try{
            for(Transaction transaction : transactions){
                Log.i("Info", "::logTransactions Transactions +++++++++++++++++++++++++");
                Log.i("Info", "::logTransactions Transaction: Orderid          :" + transaction.getOrderId() );
                Log.i("Info", "::logTransactions Transaction: Id               :" + transaction.getId() );
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

    public static void logSale(Order order){
        try{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

            Log.i("Info", "::logSale Transactions +++++++++++++++++++++++++");
            Log.i("Info", "::logSale CustomerId:" + order.getCustomerId() +
                    ", Discounts:" + order.getDiscounts().toString() +
                    ", DiscountedAmount:" + order.getDiscountedAmount() +
                    ", TotalAmount:" + order.getTotalAmount() +
                    ", SaleCount:" + order.getTotalItemCount() +
                    ", Id:" + order.getId() +
                    ", RemainAmount:" + order.getRemainAmount() +
                    ", UserId:" + order.getUserId() +
                    ", CreateDate:" + simpleDateFormat.format(order.getCreateDate()) +
                    ", PaymentCompleted:" + order.isPaymentCompleted() +
                    ", DeviceId:" + order.getDeviceId() +
                    ", OrderNum:" + order.getOrderNum() +
                    ", Longitude:" + order.getLongitude() +
                    ", Latitude:" + order.getLatitude() );
        }catch (Exception e){

        }
    }

    public static void logTransaction(String methodName, Transaction transaction){
        try{

            Log.i("Info", methodName + "+++++++++++++++++++++++++");
            Log.i("Info", methodName + " OrderId:" + transaction.getOrderId() +
                    ", Id:" + transaction.getId() +
                    ", SeqNumber:" + transaction.getSeqNumber() +
                    ", TransactionAmount:" + transaction.getTransactionAmount() +
                    ", TotalAmount:" + transaction.getTotalAmount() +
                    ", TipAmount:" + transaction.getTipAmount() +
                    ", PaymentTypeId:" + transaction.getPaymentTypeId() +
                    ", CreateDate:" + transaction.getCreateDate() +
                    ", ChangeAmount:" + transaction.getChangeAmount() +
                    ", CashAmount:" + transaction.getCashAmount() +
                    ", UserId:" + transaction.getUserId() +
                    ", isMailSend:" + transaction.isMailSend() +
                    ", mailAddress:" + transaction.getMailAdress() +
                    ", transactionType:" + transaction.getTransactionType() +
                    ", zNum:" + transaction.getzNum() +
                    ", fNum:" + transaction.getfNum());
        }catch (Exception e){

        }
    }

    public static void logRefund(Refund refund){
        try{

            Log.i("Info", "logRefund +++++++++++++++++++++++++");
            Log.i("Info", "logRefund Id:" + refund.getId() +
                    ", TransactionId:" + refund.getTransactionId() +
                    ", OrderId:" + refund.getOrderId() +
                    ", RefundGroupId:" + refund.getRefundGroupId() +
                    ", RefundAmount:" + refund.getRefundAmount() +
                    ", RefundReason:" + refund.getRefundReason() +
                    ", isRefundByAmount:" + refund.isRefundByAmount() +
                    ", isSuccessful:" + refund.isSuccessful() +
                    ", ZNum:" + refund.getzNum() +
                    ", FNum:" + refund.getfNum() +
                    ", Latitude:" + refund.getLatitude() +
                    ", Longitude:" + refund.getLongitude() +
                    ", isTransferred:" + refund.isTransferred() +
                    ", isEODProcessed:" + refund.isEODProcessed() +
                    ", EodDate:" + refund.getEodDate());
        }catch (Exception e){

        }
    }

    public static void logOrderRefundItem(OrderRefundItem orderRefundItem){
        try{

            Log.i("Info", "logOrderRefundItem +++++++++++++++++++++++++");
            Log.i("Info", "logOrderRefundItem Id:" + orderRefundItem.getId() +
                    ", OrderItemId:" + orderRefundItem.getOrderItemId() +
                    ", OrderId:" + orderRefundItem.getOrderId() +
                    ", RefundGroupId:" + orderRefundItem.getRefundGroupId() +
                    ", Name:" + orderRefundItem.getName() +
                    ", Amount:" + orderRefundItem.getAmount() +
                    ", GrossAmount:" + orderRefundItem.getGrossAmount() +
                    ", Note:" + orderRefundItem.getNote() +
                    ", ProductId:" + orderRefundItem.getProductId() +
                    ", isDynamicAmount:" + orderRefundItem.isDynamicAmount() +
                    ", OrderItemTax:" + orderRefundItem.getOrderItemTax() +
                    ", ColorId:" + orderRefundItem.getColorId() +
                    ", CategoryName:" + orderRefundItem.getCategoryName() +
                    ", isTransferred:" + orderRefundItem.isTransferred());
        }catch (Exception e){

        }
    }

    public static void logCategory(String methodName, Category category){
        try{

            Log.i("Info", methodName + " logCategory +++++++++++++++++++++++++");
            Log.i("Info", methodName + " logCategory Id:" + category.getId() +
                    ", Name:" + category.getName() +
                    ", ColorId:" + category.getColorId() +
                    ", CreateDate:" + category.getCreateDate() +
                    ", UserId:" + category.getUserId() +
                    ", UpdateDate:" + category.getUpdateDate() +
                    ", UpdateUserId:" + category.getUpdateUserId() +
                    ", isDeleted:" + category.isDeleted());
        }catch (Exception e){

        }
    }

    public static void logDynamicBoxList(List<DynamicBoxModel> dynamicBoxModelList){
        try{
            for(DynamicBoxModel dynamicBoxModel : dynamicBoxModelList){
                Log.i("Info", "::logDynamicBoxList +++++++++++++++++++++++++");
                Log.i("Info", "::logDynamicBoxList Id:" + dynamicBoxModel.getId() +
                        ", itemId:" +  dynamicBoxModel.getItemId() +
                        ", StructId:" +  dynamicBoxModel.getStructId() +
                        ", UserId:" +  dynamicBoxModel.getUserId()+
                        ", createDate:" + dynamicBoxModel.getCreateDate() +
                        ", UpdateUserId:" +  dynamicBoxModel.getUpdateUserId()+
                        ", UpdateDate:" +  dynamicBoxModel.getUpdateDate()+
                        ", isDeleted:" +  dynamicBoxModel.isDeleted());
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
            Log.i("Info", "::logReportModel cancelAmount      :" + reportModel.getCancelAmount());
            Log.i("Info", "::logReportModel totalAmount       :" + reportModel.getTotalAmount());
            Log.i("Info", "::logReportModel saleCount         :" + reportModel.getSaleCount());
            Log.i("Info", "::logReportModel cashAmount        :" + reportModel.getCashAmount());
            Log.i("Info", "::logReportModel cardAmount        :" + reportModel.getCardAmount());


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

    public static void logAutoIncrement(AutoIncrement autoIncrement) {
        try {
            Log.i("Info", "::AutoIncrement +++++++++++++++++++++++++");
            Log.i("Info", "::logAutpIncrement UserId:" + autoIncrement.getUserId() +
                    ", zNum:" + autoIncrement.getzNum() +
                    ", fNum:" + autoIncrement.getfNum() +
                    ", orderNum:" + autoIncrement.getOrderNumCounter());

        } catch (Exception e) {

        }
    }
}
