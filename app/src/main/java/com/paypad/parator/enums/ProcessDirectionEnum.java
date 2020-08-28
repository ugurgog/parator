package com.paypad.parator.enums;

public enum ProcessDirectionEnum {
    DIRECTION_FAST_MENU,                //Odeme tipi adapter e fast menu den gelir
    DIRECTION_PAYMENT_SELECT,           //Odeme tipi adapter e payment selection den gelir
    PAYMENT_FULLY_COMPLETED,            //Odeme tum splitler tamamlandi ise
    PAYMENT_PARTIALLY_COMPLETED;        //Odemesi tamamlanmamis transactionlar var ise
}