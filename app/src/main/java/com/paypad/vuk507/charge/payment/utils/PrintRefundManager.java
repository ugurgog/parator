package com.paypad.vuk507.charge.payment.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.PrintReceiptModel;
import com.paypad.vuk507.model.pojo.PrintRefundCancelModel;
import com.paypad.vuk507.model.pojo.SaleItemPojo;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.sunmiutils.ESCUtil;
import com.paypad.vuk507.utils.sunmiutils.PrintHelper;
import com.paypad.vuk507.utils.sunmiutils.SunmiPrintHelper;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class PrintRefundManager {


    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    private int sunmiPrinter = CheckSunmiPrinter;
    private SunmiPrinterService sunmiPrinterService;
    private static SunmiPrintHelper helper;
    private InnerResultCallbcak callback;
    private SaleModel saleModel;
    private PrintRefundCancelModel refundCancelModel;
    private Refund refund;
    private Context mContext;
    private boolean isFinancialReceipt;
    private int refundCancellationStatus;
    private Transaction transaction;


    public PrintRefundManager(Context context, boolean isFinancialReceipt,
                             Refund refund, int refundCancellationStatus, SaleModel saleModel, Transaction transaction) {
        this.mContext = context;
        this.helper = SunmiPrintHelper.getInstance();
        this.sunmiPrinterService = helper.getSunmiPrinterService();
        this.sunmiPrinter = helper.getLostSunmiPrinter();
        this.isFinancialReceipt = isFinancialReceipt;
        this.refundCancellationStatus = refundCancellationStatus;
        this.refund = refund;
        this.saleModel = saleModel;
        this.transaction = transaction;

        fillRefundCancelModel();
    }

    public void setCallback(InnerResultCallbcak callback) {
        this.callback = callback;
    }


    private void fillRefundCancelModel(){
        refundCancelModel = new PrintRefundCancelModel();
        refundCancelModel.setMerchantName("BODRUM BELEDIYE TUR.");                              //TODO
        refundCancelModel.setFirmName("INS GIDA ENERJI SAN. VE TIC. A.S.");                     //TODO
        refundCancelModel.setAddress("TORBA MAH. M.KEMAL ATATURK CD. No: 153 BODRUM MUGLA");    //TODO
        refundCancelModel.setPhoneNumber("0 252 317 37 37");                                    //TODO
        refundCancelModel.setTaxOffice("BODRUM V.D. 1800028646");                               //TODO

        refundCancelModel.setOrderNum(saleModel.getSale().getOrderNum());
        refundCancelModel.setfDate(new Date());
        refundCancelModel.setReceiptDate(new Date());

        if(refundCancellationStatus == TYPE_REFUND){
            refundCancelModel.setRefundAmount(refund.getRefundAmount());
            refundCancelModel.setzNum(refund.getzNum());
            refundCancelModel.setfNum(refund.getfNum());
        }else if(refundCancellationStatus == TYPE_CANCEL){
            refundCancelModel.setRefundAmount(transaction.getTotalAmount());
            refundCancelModel.setzNum(transaction.getzNum());
            refundCancelModel.setfNum(transaction.getfNum());
        }

        refundCancelModel.setChequeNo(7867);                        //TODO
        refundCancelModel.setTableNo(0);                            //TODO
        refundCancelModel.setMersisNo("05352900403");               //TODO
        refundCancelModel.setEmail("ugur.gogebakan@garaj2.com");    //TODO
        //printReceiptModel.setEkuNo(1);                            //TODO
        refundCancelModel.setMerchantNum("123456789");              //TODO
        refundCancelModel.setTerminalNum("1234567");                //TODO
        refundCancelModel.setApproveCode(1234);                     //TODO
        refundCancelModel.setStanNum(1);                            //TODO
        refundCancelModel.setBatchNum(1);                           //TODO
        refundCancelModel.setDeviceRegisterId("JH 20082508");       //TODO
    }

    @SuppressLint("DefaultLocale")
    public void printRefundOrCancelReceipt(){
        if(sunmiPrinterService == null){
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);

            //PRINT TEST DEVICE
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.test_device) + "\b" + "\n", null, 25, null);
            sunmiPrinterService.printTextWithFont(" " + "\n", null, 25, null);

            //PRINT MERCHANT NAME
            sunmiPrinterService.printTextWithFont(refundCancelModel.getMerchantName() + "\b" + "\n", null, 25, null);

            //PRINT FIRM NAME
            sunmiPrinterService.printTextWithFont(refundCancelModel.getFirmName()  + "\n", null, 25, null);

            //PRINT ADDRESS
            List<String> addressList = PrintHelper.getAddressList(paper, refundCancelModel.getAddress());

            for(String addressLine : addressList){
                sunmiPrinterService.printTextWithFont(addressLine  + "\n", null, 23, null);
            }

            //PRINT TAX OFFICE
            sunmiPrinterService.printTextWithFont(refundCancelModel.getTaxOffice() + "\n\n", null, 25, null);


            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }


            //PRINT DATE AND RECEIPT NO FIELDS
            PrintHelper.printReceiptDateFields(sunmiPrinterService, mContext, refundCancelModel.getfDate());

            //PRINT ORDER NUMBER
            PrintHelper.printOrderNum(mContext, sunmiPrinterService, refundCancelModel.getOrderNum());

            //PRINT Z NUM
            PrintHelper.printZNum(mContext, sunmiPrinterService, refundCancelModel.getzNum());

            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }

            sunmiPrinterService.printTextWithFont(" " + "\n", null, 20, null);


            String txts[] = new String[2];
            int width[] = new int[]{2, 1};
            int align[] = new int[]{0, 2};


            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }

            //TOPKDV
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            sunmiPrinterService.setFontSize(20, null);

            //REFUND/CANCEL AMOUNT
            String paymentType = "("
                    .concat(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId() ? mContext.getResources().getString(R.string.cash_upper)
                            : mContext.getResources().getString(R.string.card_upper))
                    .concat(")");

            if(refundCancellationStatus == TYPE_REFUND)
                txts[0] = mContext.getResources().getString(R.string.refund_amount_upper).concat(paymentType) + "\b";
            else if(refundCancellationStatus == TYPE_CANCEL)
                txts[0] = mContext.getResources().getString(R.string.cancel_amount_upper).concat(paymentType) + "\b";

            txts[1] = "*" + CommonUtils.getAmountText(refundCancelModel.getRefundAmount()) + "\b";
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //PRINT BARCODE
            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);
            sunmiPrinterService.printBarCode("{C1234567890123456",  8, 70, 3, 2, null);

            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);

            //PRINT CHEQUE NO AND TABLE NO
            PrintHelper.printChequeAndTableVars(mContext, sunmiPrinterService, refundCancelModel.getChequeNo(), refundCancelModel.getTableNo());

            //PRINT MERSIS
            String mersis = mContext.getString(R.string.mersis_upper)
                    .concat(" : ")
                    .concat(refundCancelModel.getMersisNo());
            sunmiPrinterService.printTextWithFont(mersis + "\n", null, 20, null);

            //PRINT EMAIL
            sunmiPrinterService.printTextWithFont(refundCancelModel.getEmail() + "\n", null, 20, null);
            sunmiPrinterService.printTextWithFont(" " + "\n", null, 20, null);

            //MERCHANT AND TERMINAL NO
            txts[0] = mContext.getResources().getString(R.string.merchant_no_upper)
                    .concat(":")
                    .concat(String.valueOf(refundCancelModel.getMerchantNum()));
            txts[1] = mContext.getResources().getString(R.string.terminal_upper)
                    .concat(":")
                    .concat(String.valueOf(refundCancelModel.getTerminalNum()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //APPCODE, BATCH, STAN
            txts[0] = mContext.getResources().getString(R.string.approve_code_upper)
                    .concat(": ")
                    .concat(String.valueOf(refundCancelModel.getApproveCode()));
            txts[1] = mContext.getResources().getString(R.string.batch_upper)
                    .concat(":")
                    .concat(String.valueOf(refundCancelModel.getBatchNum()))
                    .concat("/")
                    .concat(mContext.getResources().getString(R.string.stan_upper))
                    .concat(":")
                    .concat(String.valueOf(refundCancelModel.getStanNum()));

            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //REFUND DATE
            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);
            @SuppressLint("SimpleDateFormat") String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(refundCancelModel.getReceiptDate());
            String printSaleDateStr = dateStr
                    .concat(" ")
                    .concat(refundCancellationStatus == TYPE_REFUND ? mContext.getString(R.string.refund)
                            : mContext.getString(R.string.cancel));
            try {
                sunmiPrinterService.setAlignment(0, null);
                sunmiPrinterService.printTextWithFont(printSaleDateStr + "\n", null, 20, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


            PrintHelper.setBold(sunmiPrinterService, WoyouConsts.ENABLE);

            //PRINT GOOD AND SERVICES
            PrintHelper.printGoodServicesInfo(mContext, sunmiPrinterService);

            PrintHelper.setBold(sunmiPrinterService, WoyouConsts.DISABLE);

            sunmiPrinterService.setAlignment(0, null);

            sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.belongs_to_card_holder)
                    .concat(" ")
                    .concat("(")
                    .concat(mContext.getResources().getString(R.string.first_copy))
                    .concat(")")+ "\n\n", null, 20, null);

            sunmiPrinterService.printTextWithFont(" " + "\n", null, 25, null);

            //PRINT TEST DEVICE
            PrintHelper.printTestDeviceLabel(mContext, sunmiPrinterService);

            sunmiPrinterService.printTextWithFont(refundCancelModel.getDeviceRegisterId() + "\b" + "\n", null, 25, null);

            sunmiPrinterService.autoOutPaper(null);

            sunmiPrinterService.exitPrinterBufferWithCallback(true, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
