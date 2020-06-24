package com.paypad.vuk507.charge.utils;

import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class ChargeHelper {

    public static String getChargeTotalAmountStr(double amount){
        double totalAmount = 0d;
        totalAmount = SaleModelInstance.getInstance().getSaleModel().getSale().getTotalAmount() + amount;
        String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        return amountStr;
    }
}
