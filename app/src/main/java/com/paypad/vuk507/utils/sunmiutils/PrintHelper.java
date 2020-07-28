package com.paypad.vuk507.utils.sunmiutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.pojo.PrintReceiptModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintHelper {

    private static final int MAX_DATE_LEN = 12;
    private static final int DATE_FIELDS_FONT_SIZE = 25;

    @SuppressLint("DefaultLocale")
    public static void printDateAndReceiptNoFields(SunmiPrinterService sunmiPrinterService, Context mContext, Date date, int receiptNo){
        try {
            sunmiPrinterService.setAlignment(0, null);
            String dateString = CommonUtils.padRight(mContext.getResources().getString(R.string.date_upper), MAX_DATE_LEN) + ": ";

            @SuppressLint("SimpleDateFormat") String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
            sunmiPrinterService.printTextWithFont(dateString + dateStr + "\n", null, DATE_FIELDS_FONT_SIZE, null);

            //Hour format
            String hourString = CommonUtils.padRight(mContext.getResources().getString(R.string.hour_upper), MAX_DATE_LEN) + ": ";

            @SuppressLint("SimpleDateFormat") String hourStr = new SimpleDateFormat("HH:mm:ss").format(date);
            sunmiPrinterService.printTextWithFont(hourString + hourStr + "\n", null, DATE_FIELDS_FONT_SIZE, null);

            //Receipt no
            printReceiptNo(mContext, sunmiPrinterService, receiptNo);
            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    public static void printReceiptNo(Context mContext, SunmiPrinterService sunmiPrinterService, int receiptNo){
        String rNoString = CommonUtils.padRight(mContext.getResources().getString(R.string.receipt_no_upper), MAX_DATE_LEN) + ": ";
        try {
            sunmiPrinterService.printTextWithFont(rNoString + String.format("%05d", receiptNo) + "\n", null, DATE_FIELDS_FONT_SIZE, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void addLineText(SunmiPrinterService sunmiPrinterService){
        int paper;
        try {
            paper = sunmiPrinterService.getPrinterPaper();
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void addLineTextWithValue(SunmiPrinterService sunmiPrinterService, String text){
        int paper;
        try {
            String fullLine = "------------------------------------------------";
            paper = sunmiPrinterService.getPrinterPaper();

            int dataLen = text.trim().length();
            int maxLen = paper == 1 ? 32 : 48;

            if(dataLen > maxLen){
                text = text.substring(0, maxLen - 6).concat("..");
                dataLen = text.trim().length();
            }

            int lineLen = (maxLen - dataLen) / 2;

            String mainText = fullLine.substring(0, lineLen).concat(text).concat(fullLine);
            mainText = mainText.substring(0, maxLen);

            sunmiPrinterService.printText(mainText + "\n", null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAddressList(int paper, String address){
        List<String> addressList = new ArrayList<>();

        int maxLen = paper == 1 ? 32 : 48;

        String[] parts = address.split(" ");

        String aLine = "";

        for(String part : parts){
            if(aLine.trim().length() < maxLen){

                if((aLine + " " + part).trim().length() > maxLen){
                    addressList.add(aLine);
                    aLine = "";
                }else {
                    aLine = aLine + " " + part;
                }
            }
        }

        if(aLine.trim().length() > 0)
            addressList.add(aLine);

        return addressList;
    }

    public static void printChequeAndTableVars(Context mContext, SunmiPrinterService sunmiPrinterService, int chequeNo, int tableNo){
        String chequeAndTable = mContext.getResources().getString(R.string.cheque_upper)
                .concat(":")
                .concat(String.valueOf(chequeNo))
                .concat("  ")
                .concat(mContext.getResources().getString(R.string.table_no_upper))
                .concat(":")
                .concat(String.valueOf(tableNo));
        try {
            sunmiPrinterService.printTextWithFont(chequeAndTable + "\n\n", null, 20, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static String getMaskedCardNumber(String cardNum){

        if(cardNum == null || cardNum.isEmpty())
            return null;

        int maskedCardNumLen = cardNum.trim().length();
        String maskedCardNum = "************************".substring(0, maskedCardNumLen - 4) + cardNum.substring(maskedCardNumLen - 4, maskedCardNumLen);
        return maskedCardNum;
    }

    public static void printMerchantAndTerminalRow(Context mContext, SunmiPrinterService sunmiPrinterService, String merchantNum, String terminalNum){
        String txts[] = new String[2];
        int width[] = new int[]{1, 1};
        int align[] = new int[]{0, 2};

        txts[0] = mContext.getResources().getString(R.string.merchant_no_upper)
                .concat(":")
                .concat(merchantNum);
        txts[1] = mContext.getResources().getString(R.string.terminal_upper)
                .concat(":")
                .concat(terminalNum);
        try {
            sunmiPrinterService.setFontSize(20, null);
            sunmiPrinterService.printColumnsString(txts, width, align, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printAppCodeBatchAndStan(Context mContext, SunmiPrinterService sunmiPrinterService, int approveCode, int batchNum, int stanNum){
        String txts[] = new String[2];
        int width[] = new int[]{1, 1};
        int align[] = new int[]{0, 2};

        txts[0] = mContext.getResources().getString(R.string.approve_code_upper)
                .concat(": ")
                .concat(String.valueOf(approveCode));
        txts[1] = mContext.getResources().getString(R.string.batch_upper)
                .concat(":")
                .concat(String.valueOf(batchNum))
                .concat("/")
                .concat(mContext.getResources().getString(R.string.stan_upper))
                .concat(":")
                .concat(String.valueOf(stanNum));

        try {
            sunmiPrinterService.printColumnsString(txts, width, align, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printSaleDateRow(Context context, SunmiPrinterService sunmiPrinterService, Date date){
        @SuppressLint("SimpleDateFormat") String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
        String printSaleDateStr = dateStr
                .concat(" ")
                .concat(context.getString(R.string.sale_upper));
        try {
            sunmiPrinterService.setAlignment(0, null);
            sunmiPrinterService.printTextWithFont(printSaleDateStr + "\n", null, 20, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printEkuAndZNo(Context mContext, SunmiPrinterService sunmiPrinterService, int EkuNo, int zNo){
        String txts[] = new String[2];
        int width[] = new int[]{1, 1};
        int align[] = new int[]{0, 2};
        txts[0] = mContext.getResources().getString(R.string.eku_no_upper)
                .concat(": ")
                .concat(String.valueOf(EkuNo));
        txts[1] = mContext.getResources().getString(R.string.z_no_upper)
                .concat(":")
                .concat(String.valueOf(zNo));
        try {
            sunmiPrinterService.printColumnsString(txts, width, align, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printCardTypeAndAmountRows(Context context, SunmiPrinterService sunmiPrinterService, PrintReceiptModel printReceiptModel){
        try {
            sunmiPrinterService.setAlignment(1, null);

            for(PrintReceiptModel.ReceiptPaymentModel receiptPaymentModel : printReceiptModel.getReceiptPaymentModels()){

                if(receiptPaymentModel.getPaymentType() == PaymentTypeEnum.CREDIT_CARD.getId()){
                    String maskedCardNumber = PrintHelper.getMaskedCardNumber(receiptPaymentModel.getCardNumber());

                    if(maskedCardNumber != null && !maskedCardNumber.isEmpty()){
                        sunmiPrinterService.printTextWithFont(maskedCardNumber + "\b" + "\n", null, 25, null);

                        String amountStr = context.getResources().getString(R.string.price)
                                .concat(":")
                                .concat(CommonUtils.getAmountText(receiptPaymentModel.getAmount()));
                        sunmiPrinterService.printTextWithFont(amountStr + "\b" + "\n\n", null, 25, null);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printGoodServicesInfo(Context context, SunmiPrinterService sunmiPrinterService){
        try {
            sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.goods_and_services_received) + "\n", null, 20, null);
            sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.keep_this_receipt) + "\n", null, 20, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printBelongsToCardHolder(Context context, SunmiPrinterService sunmiPrinterService){
        try {
            sunmiPrinterService.setAlignment(0, null);
            sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.belongs_to_card_holder)
                    .concat(" ")
                    .concat("(")
                    .concat(context.getResources().getString(R.string.first_copy))
                    .concat(")")+ "\n\n", null, 20, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printBelongsToMerchant(Context context, SunmiPrinterService sunmiPrinterService){
        try {
            sunmiPrinterService.setAlignment(0, null);
            sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.belongs_to_contracted_merchant)
                    .concat(" ")
                    .concat("(")
                    .concat(context.getResources().getString(R.string.second_copy))
                    .concat(")")+ "\n\n", null, 18, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void setBold(SunmiPrinterService sunmiPrinterService, int boldValue){
        try {
            sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, boldValue);
        } catch (RemoteException e) {
            try {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void printTestDeviceLabel(Context mContext, SunmiPrinterService sunmiPrinterService){
        try {
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.test_device) + "\b" + "\n", null, 25, null);
            sunmiPrinterService.printTextWithFont(" " + "\n", null, 25, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printNoFinancialLabel(Context mContext, SunmiPrinterService sunmiPrinterService){
        try {
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.no_financial_receipt) + "\b" + "\n", null, 25, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
