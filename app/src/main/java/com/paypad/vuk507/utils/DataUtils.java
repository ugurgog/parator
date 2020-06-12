package com.paypad.vuk507.utils;

import com.paypad.vuk507.model.Product;

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

    public static double getDoubleValueFromFormattedString(String rateOrAmountStr){
        return Double.valueOf(rateOrAmountStr.replaceAll(",", ""));
    }

    public static String getShortenCustomerName(String name) {
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
}
