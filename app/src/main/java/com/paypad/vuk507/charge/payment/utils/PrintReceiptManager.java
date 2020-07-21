package com.paypad.vuk507.charge.payment.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.PaymentDetailModel;
import com.paypad.vuk507.model.pojo.PrintReceiptModel;
import com.paypad.vuk507.model.pojo.ReportDiscountModel;
import com.paypad.vuk507.model.pojo.SaleItemPojo;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.LogUtil;
import com.paypad.vuk507.utils.sunmiutils.ESCUtil;
import com.paypad.vuk507.utils.sunmiutils.SunmiPrintHelper;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class PrintReceiptManager {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    private int sunmiPrinter = CheckSunmiPrinter;
    private SunmiPrinterService sunmiPrinterService;
    private static SunmiPrintHelper helper;
    private InnerResultCallbcak callback;
    private SaleModel saleModel;
    private Transaction transaction;
    private PrintReceiptModel printReceiptModel;

    public PrintReceiptManager(SaleModel saleModel, Transaction transaction) {
        this.helper = SunmiPrintHelper.getInstance();
        this.sunmiPrinterService = helper.getSunmiPrinterService();
        this.sunmiPrinter = helper.getLostSunmiPrinter();
        this.saleModel = saleModel;
        this.transaction = transaction;
        printReceiptModel = new PrintReceiptModel();

        LogUtil.logTransaction(PrintReceiptManager.class.getName(), transaction);

        fillReceiptModel();
    }

    public void setCallback(InnerResultCallbcak callback) {
        this.callback = callback;
    }

    private void fillReceiptModel(){
        printReceiptModel = new PrintReceiptModel();
        printReceiptModel.setDiscounts(new ArrayList<>());
        printReceiptModel.setSaleItemPojos(new ArrayList<>());

        printReceiptModel.setPaymentType(PaymentTypeEnum.getById(transaction.getPaymentTypeId()));

        printReceiptModel.setTipAmount(transaction.getTipAmount());
        printReceiptModel.setSubTotalAmount(transaction.getTransactionAmount());

        printReceiptModel.setReceiptDate(transaction.getCreateDate());
        printReceiptModel.setTotalAmount(saleModel.getSale().getSubTotalAmount());
        printReceiptModel.setPurchaseSubTotalAmount(saleModel.getSale().getSubTotalAmount());

        addDiscountItems();
        addSaleItems();

        printReceiptModel.setReceiptAmount(transaction.getTotalAmount());
    }

    private void addSaleItems() {
        for(SaleItem saleItem : saleModel.getSaleItems()){
            SaleItemPojo saleItemPojo = new SaleItemPojo();
            saleItemPojo.setName(saleItem.getName());
            saleItemPojo.setAmount(saleItem.getAmount());
            printReceiptModel.getSaleItemPojos().add(saleItemPojo);
            printReceiptModel.setTaxAmount(printReceiptModel.getTaxAmount() + saleItem.getTaxAmount());
        }
    }

    private void addDiscountItems(){
        List<SaleItem> saleItems = new ArrayList<>();

        for(SaleItem saleItem : saleModel.getSaleItems()){
            SaleItem saleItem1 = new SaleItem();
            saleItem1.setDiscounts(saleItem.getDiscounts());
            saleItem1.setAmount(saleItem.getAmount() * saleItem.getQuantity());
            saleItems.add(saleItem1);
        }

        for(Discount discount : saleModel.getSale().getDiscounts()){

            ReportDiscountModel reportDiscountModel = new ReportDiscountModel();

            reportDiscountModel.setDiscountName(discount.getName());

            if(discount.getAmount() > 0d){
                reportDiscountModel.setTotalDiscountAmount(discount.getAmount());
            }else if(discount.getRate() > 0d){
                double discountAmount = 0d;
                double totalDiscAmount = 0d;

                for(SaleItem saleItem : saleItems){
                    if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
                        for(Discount discount1 : saleItem.getDiscounts()){

                            if(discount.getId() == discount1.getId()){
                                discountAmount = discountAmount + ((saleItem.getAmount() / 100d)  * discount.getRate());
                                saleItem.setAmount(CommonUtils.round(saleItem.getAmount() - discountAmount, 2));
                                totalDiscAmount = totalDiscAmount + discountAmount;
                                break;
                            }
                        }
                        discountAmount = 0d;
                    }
                }
                reportDiscountModel.setTotalDiscountAmount(totalDiscAmount);
            }
            printReceiptModel.getDiscounts().add(reportDiscountModel);
        }
    }

    public void printReceipt(Context context){
        if(sunmiPrinterService == null){
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            sunmiPrinterService.enterPrinterBuffer(true);
            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);

            sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.app_name).concat(" ")
                    .concat(context.getResources().getString(R.string.receipt))+ "\b\n", null, 40, null);

            sunmiPrinterService.printTextWithFont(CommonUtils.getAmountTextWithCurrency(printReceiptModel.getReceiptAmount()) + "\n\n", null, 30, null);

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_pos_bg_white_64dp);
            sunmiPrinterService.printBitmap(bitmap, null);

            sunmiPrinterService.printText("\n\n", null);

            @SuppressLint("SimpleDateFormat") String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(printReceiptModel.getReceiptDate());
            sunmiPrinterService.printTextWithFont(dateStr + "\n", null, 20, null);

            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String txts[] = new String[]{"Kola", "17.00 TL"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};

            for(SaleItemPojo saleItemPojo : printReceiptModel.getSaleItemPojos()){
                txts[0] = saleItemPojo.getName();
                txts[1] = CommonUtils.getAmountTextWithCurrency(saleItemPojo.getAmount());
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if(printReceiptModel.getDiscounts().size() > 0){

                String discTitle = "--------" + context.getResources().getString(R.string.discounts);
                discTitle = discTitle + "----------------------".substring(0, 32 - discTitle.length());

                if(paper == 1){
                    sunmiPrinterService.printText(discTitle + "\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                for(ReportDiscountModel reportDiscountModel : printReceiptModel.getDiscounts()){
                    txts[0] = reportDiscountModel.getDiscountName();
                    txts[1] = CommonUtils.getAmountTextWithCurrency(reportDiscountModel.getTotalDiscountAmount());
                    sunmiPrinterService.printColumnsString(txts, width, align, null);
                }
            }

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Purchase Subtotal
            txts[0] = context.getResources().getString(R.string.subtotal);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getPurchaseSubTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //Tax Amount

            double taxAmount = printReceiptModel.getTaxAmount() / saleModel.getTransactions().size();
            String salesTaxStr = context.getResources().getString(R.string.sales_tax).concat(" - ")
                    .concat(context.getResources().getString(R.string.included)).concat(", ")
                    .concat(CommonUtils.getAmountTextWithCurrency(taxAmount));
            sunmiPrinterService.printTextWithFont(salesTaxStr + "\n", null, 17, null);

            //Total
            txts[0] = context.getResources().getString(R.string.total);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Amount
            txts[0] = context.getResources().getString(R.string.amount);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getSubTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //Tip
            txts[0] = context.getResources().getString(R.string.tip);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getTipAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Payment Type
            txts[0] = context.getResources().getString(R.string.payment_type);
            txts[1] = CommonUtils.getLanguage().equals(LANGUAGE_TR) ? printReceiptModel.getPaymentType().getLabelTr() : printReceiptModel.getPaymentType().getLabelEn();
            sunmiPrinterService.printColumnsString(txts, width, align, null);


            /*sunmiPrinterService.printTextWithFont("Toplam Tutar:          59.00 TL\b", null, 40, null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printQRCode("ugur.gogebakan.tutar.59.00tl", 10, 0, null);
            sunmiPrinterService.setFontSize(36, null);
            sunmiPrinterService.printText("Bu barkoddur", null);*/
            sunmiPrinterService.autoOutPaper(null);

            sunmiPrinterService.exitPrinterBufferWithCallback(true, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
