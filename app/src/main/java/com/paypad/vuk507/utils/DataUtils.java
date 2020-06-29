package com.paypad.vuk507.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Product;

import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class DataUtils {

    public static String getProductShortenName(Product product){
        if(product.getName().length() > 2)
            return product.getName().substring(0, 2);
        else
            return product.getName();
    }

    public static String getProductNameShortenName(String name){
        if(name.length() > 2)
            return name.substring(0, 2);
        else
            return name;
    }

    public static String getCustomerShortName(String name, String surname) {
        String shortName = "";

        if (name != null && !name.trim().isEmpty() && surname != null && !surname.trim().isEmpty()) {
            shortName = name.substring(0,1).toUpperCase().concat(surname.substring(0,1).toUpperCase());
        }else if (name != null && !name.trim().isEmpty()){
            if(name.length() > 1)
                shortName = name.substring(0,2).toUpperCase();
            else
                shortName = name.substring(0,1).toUpperCase();
        }

        return shortName;
    }

    public static double getDoubleValueFromFormattedString(String rateOrAmountStr){
        return Double.valueOf(rateOrAmountStr.replaceAll(",", ""));
    }

    public static String getContactShortName(String name) {
        StringBuilder returnValue = new StringBuilder();
        if (name != null && !name.trim().isEmpty()) {
            String[] seperatedName = name.trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 3)
                    returnValue.append(word.substring(0, 1).toUpperCase());
            }
        }

        return returnValue.toString();
    }

    public static void callCustomer(Context mContext, String phoneNumber) {
        try {
            mContext.startActivity(new Intent(Intent.ACTION_DIAL,
                    Uri.fromParts("tel", phoneNumber, null)));

        } catch (Exception e) {
            CommonUtils.showToastShort(mContext, "Error while calling customer!");
            e.printStackTrace();
        }
    }

    public static void sendEmailToCustomer(Context mContext, String toEmail) {
        String[] TO = {toEmail};
        //String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            CommonUtils.showToastShort(mContext, "There is no email client installed.");
            ex.printStackTrace();
        }
    }

    public static String getTotalAmount(double totalAmount, int number){
        if(totalAmount == 0){
            totalAmount = (totalAmount  + number) / 100;
        }else {
            totalAmount = (totalAmount * 10) + (number / 100.00d);
        }

        if(totalAmount > MAX_PRICE_VALUE){
            totalAmount = MAX_PRICE_VALUE;
        }

        return CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
    }
}
