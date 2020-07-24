package com.paypad.vuk507.utils.sunmiutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.utils.CommonUtils;
import com.sunmi.peripheral.printer.SunmiPrinterService;

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
            String rNoString = CommonUtils.padRight(mContext.getResources().getString(R.string.receipt_no_upper), MAX_DATE_LEN) + ": ";
            sunmiPrinterService.printTextWithFont(rNoString + String.format("%05d", receiptNo) + "\n\n", null, DATE_FIELDS_FONT_SIZE, null);
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
        int maskedCardNumLen = cardNum.trim().length();
        String maskedCardNum = "************************".substring(0, maskedCardNumLen - 4) + cardNum.substring(maskedCardNumLen - 4, maskedCardNumLen);
        return maskedCardNum;
    }
}
