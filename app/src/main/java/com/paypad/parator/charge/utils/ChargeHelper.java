package com.paypad.parator.charge.utils;

import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.utils.CommonUtils;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class ChargeHelper {

    public static String getChargeTotalAmountStr(double amount){
        double totalAmount = 0d;
        totalAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getTotalAmount() + amount;
        String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        return amountStr;
    }
}
