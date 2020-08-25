package com.paypad.vuk507.charge.payment.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderItemTax;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintOrderManager {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    private int sunmiPrinter = CheckSunmiPrinter;
    private SunmiPrinterService sunmiPrinterService;
    private static SunmiPrintHelper helper;
    private InnerResultCallbcak callback;
    private SaleModel saleModel;
    private PrintReceiptModel printReceiptModel;
    private PrintRefundCancelModel refundCancelModel;
    private Context mContext;
    private boolean isFinancialReceipt;

    public PrintOrderManager(Context context, SaleModel saleModel, boolean isFinancialReceipt) {
        this.mContext = context;
        this.helper = SunmiPrintHelper.getInstance();
        this.sunmiPrinterService = helper.getSunmiPrinterService();
        this.sunmiPrinter = helper.getLostSunmiPrinter();
        this.saleModel = saleModel;
        printReceiptModel = new PrintReceiptModel();
        this.isFinancialReceipt = isFinancialReceipt;
        fillReceiptModel();
    }

    public void setCallback(InnerResultCallbcak callback) {
        this.callback = callback;
    }

    private void fillReceiptModel(){
        printReceiptModel = new PrintReceiptModel();

        printReceiptModel.setMerchantName("BODRUM BELEDIYE TUR.");                              //TODO
        printReceiptModel.setFirmName("INS GIDA ENERJI SAN. VE TIC. A.S.");                     //TODO
        printReceiptModel.setAddress("TORBA MAH. M.KEMAL ATATURK CD. No: 153 BODRUM MUGLA");    //TODO
        printReceiptModel.setPhoneNumber("0 252 317 37 37");                                    //TODO
        printReceiptModel.setTaxOffice("BODRUM V.D. 1800028646");                               //TODO

        printReceiptModel.setDiscounts(new ArrayList<>());
        printReceiptModel.setSaleItems(new ArrayList<>());
        printReceiptModel.setReceiptTaxModels(new ArrayList<>());
        printReceiptModel.setReceiptPaymentModels(new ArrayList<>());


        addDiscountItems();
        addSaleItems();
        addTaxModels();
        addPaymentTypes();
        addTotalTipAmount();
        //setBatchAndReceiptNum();

        printReceiptModel.setzNum(saleModel.getOrder().getzNum());
        printReceiptModel.setOrderNum(saleModel.getOrder().getOrderNum());
        printReceiptModel.setfDate(new Date());
        printReceiptModel.setReceiptDate(saleModel.getOrder().getCreateDate());
        printReceiptModel.setTotalAmount(saleModel.getOrder().getDiscountedAmount());


        printReceiptModel.setChequeNo(7867);                        //TODO
        printReceiptModel.setTableNo(0);                            //TODO
        printReceiptModel.setMersisNo("05352900403");               //TODO
        printReceiptModel.setEmail("ugur.gogebakan@garaj2.com");    //TODO
        //printReceiptModel.setEkuNo(1);                            //TODO
        printReceiptModel.setMerchantNum("123456789");              //TODO
        printReceiptModel.setTerminalNum("1234567");                //TODO
        printReceiptModel.setApproveCode(1234);                     //TODO
        printReceiptModel.setStanNum(1);                            //TODO
        printReceiptModel.setBatchNum(1);                           //TODO
        printReceiptModel.setDeviceRegisterId("JH 20082508");       //TODO
    }

    /**
     * Finansal olmayan makbuzlar orjinal satis batch ve makbuz no ile print edilir
     */
    /*private void setBatchAndReceiptNum(){

        if(isFinancialReceipt){
            AutoIncrement autoIncrement = AutoIncrementDBHelper.getAutoIncrement(saleModel.getOrder().getUserUuid());

            printReceiptModel.setReceiptNum(AutoIncrementDBHelper.getCurrentReceiptNum(saleModel.getOrder().getUserUuid()));                                //TODO
            printReceiptModel.setBatchNum((int)autoIncrement.getBatchNum());                              //TODO
        }else {
            printReceiptModel.setReceiptNum((int)saleModel.getOrder().getReceiptNum());
            printReceiptModel.setBatchNum((int)saleModel.getOrder().getBatchNum());
        }
    }*/

    private void addPaymentTypes() {
        for(Transaction transaction : saleModel.getTransactions()){
            if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){
                PrintReceiptModel.ReceiptPaymentModel receiptPaymentModel = new PrintReceiptModel.ReceiptPaymentModel();
                receiptPaymentModel.setPaymentType(transaction.getPaymentTypeId());
                receiptPaymentModel.setAmount(transaction.getTransactionAmount());
                receiptPaymentModel.setCardName("OFFLINE XXX"); //TODO
                receiptPaymentModel.setCardNumber("1111 2222 3333 4444");//TODO

                receiptPaymentModel.setzNum(transaction.getzNum());
                receiptPaymentModel.setfNum(transaction.getfNum());

                printReceiptModel.getReceiptPaymentModels().add(receiptPaymentModel);
                printReceiptModel.setTotalTipAmount(CommonUtils.round(
                        printReceiptModel.getTotalTipAmount() + transaction.getTipAmount(), 2
                ));
            }else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){

                PrintReceiptModel.ReceiptPaymentModel receiptPaymentModel = new PrintReceiptModel.ReceiptPaymentModel();
                receiptPaymentModel.setPaymentType(transaction.getPaymentTypeId());
                receiptPaymentModel.setAmount(transaction.getTransactionAmount());

                receiptPaymentModel.setzNum(transaction.getzNum());
                receiptPaymentModel.setfNum(transaction.getfNum());

                printReceiptModel.getReceiptPaymentModels().add(receiptPaymentModel);


                /*boolean isExist = false;
                for(PrintReceiptModel.ReceiptPaymentModel receiptPaymentModel : printReceiptModel.getReceiptPaymentModels()){
                    if(receiptPaymentModel.getPaymentType() == transaction.getPaymentTypeId()){
                        receiptPaymentModel.setAmount(CommonUtils.round(receiptPaymentModel.getAmount() + transaction.getTransactionAmount(), 2));
                        isExist = true;
                        break;
                    }
                }
                if(!isExist){
                    PrintReceiptModel.ReceiptPaymentModel receiptPaymentModel = new PrintReceiptModel.ReceiptPaymentModel();
                    receiptPaymentModel.setPaymentType(transaction.getPaymentTypeId());
                    receiptPaymentModel.setAmount(transaction.getTransactionAmount());
                    printReceiptModel.getReceiptPaymentModels().add(receiptPaymentModel);
                }*/
            }
        }
    }

    private void addTotalTipAmount(){
        for(Transaction transaction : saleModel.getTransactions()){
            if(transaction.getTipAmount() > 0d){
                printReceiptModel.setTotalTipAmount(CommonUtils.round(printReceiptModel.getTotalTipAmount() + transaction.getTipAmount(), 2));
            }
        }
    }

    private void addTaxModels() {
        for(OrderItem orderItem : saleModel.getOrderItems()){

            boolean isExist = false;

            //OrderItemTax orderItemTax = orderItem.getOrderItemTaxes().get(0);

            OrderItemTax taxModel = orderItem.getTaxModel();

            for(PrintReceiptModel.ReceiptTaxModel receiptTaxModel : printReceiptModel.getReceiptTaxModels()){

                if(receiptTaxModel.getTaxId() == taxModel.getId()){
                    receiptTaxModel.setTaxAmount(CommonUtils.round(receiptTaxModel.getTaxAmount() + orderItem.getTaxAmount(), 2));
                    receiptTaxModel.setTotalAmount(CommonUtils.round(receiptTaxModel.getTotalAmount() + orderItem.getGrossAmount(), 2));
                    isExist = true;
                    break;
                }
            }

            if(!isExist){
                PrintReceiptModel.ReceiptTaxModel receiptTaxModel = new PrintReceiptModel.ReceiptTaxModel();
                receiptTaxModel.setTaxId(taxModel.getId());
                receiptTaxModel.setTaxName(taxModel.getName());
                receiptTaxModel.setTaxAmount(orderItem.getTaxAmount());
                receiptTaxModel.setTotalAmount(orderItem.getGrossAmount());
                printReceiptModel.getReceiptTaxModels().add(receiptTaxModel);
            }
        }
    }

    private void addSaleItems() {
        for(OrderItem orderItem : saleModel.getOrderItems()){

            SaleItemPojo saleItemPojo = new SaleItemPojo();
            saleItemPojo.setName(orderItem.getName());
            saleItemPojo.setQuantity(orderItem.getQuantity());
            saleItemPojo.setAmount(orderItem.getAmount());
            saleItemPojo.setTaxModel(orderItem.getTaxModel());
            //saleItemPojo.setOrderItemTaxes(orderItem.getOrderItemTaxes());
            printReceiptModel.getSaleItems().add(saleItemPojo);

            printReceiptModel.setTotalTaxAmount(CommonUtils.round(printReceiptModel.getTotalTaxAmount() + (orderItem.getTaxAmount() * orderItem.getQuantity()), 2));
        }

        printReceiptModel.setTotalTaxAmount(CommonUtils.round(printReceiptModel.getTotalTaxAmount() / saleModel.getTransactions().size(), 2));
    }

    private void addDiscountItems(){
        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItem orderItem : saleModel.getOrderItems()){
            OrderItem orderItem1 = new OrderItem();
            orderItem1.setDiscounts(orderItem.getDiscounts());
            orderItem1.setAmount(orderItem.getAmount() * orderItem.getQuantity());
            orderItems.add(orderItem1);
        }

        for(OrderItemDiscount discount : saleModel.getOrder().getDiscounts()){

            PrintReceiptModel.ReceiptDiscountModel reportDiscountModel = new PrintReceiptModel.ReceiptDiscountModel();

            reportDiscountModel.setDiscountName(discount.getName());

            if(discount.getAmount() > 0d){
                reportDiscountModel.setTotalDiscountAmount(discount.getAmount());
            }else if(discount.getRate() > 0d){
                double discountAmount = 0d;
                double totalDiscAmount = 0d;

                for(OrderItem orderItem : orderItems){
                    if(orderItem.getDiscounts() != null && orderItem.getDiscounts().size() > 0){
                        for(OrderItemDiscount discount1 : orderItem.getDiscounts()){

                            if(discount.getId() == discount1.getId()){
                                discountAmount = discountAmount + ((orderItem.getAmount() / 100d)  * discount.getRate());
                                orderItem.setAmount(CommonUtils.round(orderItem.getAmount() - discountAmount, 2));
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

    @SuppressLint("DefaultLocale")
    public void printCustomerReceipt(){
        if(sunmiPrinterService == null){
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);

            if(!isFinancialReceipt){
                sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.no_financial_receipt) + "\b" + "\n", null, 25, null);
                sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.copy_of_sale_receipt) + "\b" + "\n\n", null, 25, null);
            }

            //PRINT TEST DEVICE
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.test_device) + "\b" + "\n", null, 25, null);
            sunmiPrinterService.printTextWithFont(" " + "\n", null, 25, null);

            //PRINT MERCHANT NAME
            sunmiPrinterService.printTextWithFont(printReceiptModel.getMerchantName() + "\b" + "\n", null, 25, null);

            //PRINT FIRM NAME
            sunmiPrinterService.printTextWithFont(printReceiptModel.getFirmName()  + "\n", null, 25, null);

            //PRINT ADDRESS
            List<String> addressList = PrintHelper.getAddressList(paper, printReceiptModel.getAddress());

            for(String addressLine : addressList){
                sunmiPrinterService.printTextWithFont(addressLine  + "\n", null, 23, null);
            }

            //PRINT TAX OFFICE
            sunmiPrinterService.printTextWithFont(printReceiptModel.getTaxOffice() + "\n\n", null, 25, null);


            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }


            //PRINT DATE AND RECEIPT NO FIELDS
            PrintHelper.printReceiptDateFields(sunmiPrinterService, mContext, printReceiptModel.getfDate());

            //PRINT ORDER NUMBER
            PrintHelper.printOrderNum(mContext, sunmiPrinterService, printReceiptModel.getOrderNum());

            //PRINT Z NUM
            PrintHelper.printZNum(mContext, sunmiPrinterService, printReceiptModel.getzNum());

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


            //PRINT SALE ITEMS
            for(SaleItemPojo saleItem : printReceiptModel.getSaleItems()){

                String txts1[] = new String[3];
                int width1[] = new int[]{3, 1, 1};
                int align1[] = new int[]{0, 1, 2};

                txts1[0] = saleItem.getName();
                //txts1[1] = "%" + saleItem.getOrderItemTaxes().get(0).getTaxRate();
                txts1[1] = "%" + saleItem.getTaxModel().getTaxRate();
                txts1[2] = "*" + CommonUtils.getAmountText(CommonUtils.round(saleItem.getAmount() * saleItem.getQuantity(), 2));
                sunmiPrinterService.printColumnsString(txts1, width1, align1, null);

                if(saleItem.getQuantity() > 1){
                    String quantityStr = String.valueOf(saleItem.getQuantity())
                            .concat(mContext.getResources().getString(R.string.per))
                            .concat(" X ")
                            .concat(CommonUtils.getAmountTextWithCurrency(saleItem.getAmount()));
                    sunmiPrinterService.printTextWithFont(quantityStr + "\n", null, 20, null);
                }
            }

            //DISCOUNTS
            if(printReceiptModel.getDiscounts().size() > 0){

                String discTitle = "--------" + mContext.getResources().getString(R.string.discounts);

                if(paper == 1){
                    discTitle = discTitle + "----------------------".substring(0, 32 - discTitle.length());
                    sunmiPrinterService.printText(discTitle + "\n", null);
                }else{
                    discTitle = discTitle + "----------------------".substring(0, 48 - discTitle.length());
                    sunmiPrinterService.printText(discTitle + "\n", null);
                }

                for(PrintReceiptModel.ReceiptDiscountModel reportDiscountModel : printReceiptModel.getDiscounts()){
                    txts[0] = reportDiscountModel.getDiscountName();
                    txts[1] = CommonUtils.getAmountTextWithCurrency(reportDiscountModel.getTotalDiscountAmount());
                    sunmiPrinterService.printColumnsString(txts, width, align, null);
                }
            }

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
            txts[0] = mContext.getResources().getString(R.string.toptax_upper) + "\b";
            txts[1] = "*" + CommonUtils.getAmountText(printReceiptModel.getTotalTaxAmount()) + "\b";
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //TOTAL
            txts[0] = mContext.getResources().getString(R.string.total_upper) + "\b";
            txts[1] = "*" + CommonUtils.getAmountText(printReceiptModel.getTotalAmount()) + "\b";
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //PRINT PAYMENT TYPES AND CARD INFOS
            for(PrintReceiptModel.ReceiptPaymentModel receiptPaymentModel : printReceiptModel.getReceiptPaymentModels()){

                if(receiptPaymentModel.getPaymentType() == PaymentTypeEnum.CASH.getId()){
                    txts[0] = mContext.getResources().getString(R.string.cash_upper);
                    txts[1] = "*" + CommonUtils.getAmountText(receiptPaymentModel.getAmount());

                    sunmiPrinterService.printColumnsString(txts, width, align, null);
                }else if(receiptPaymentModel.getPaymentType() == PaymentTypeEnum.CREDIT_CARD.getId()){
                    txts[0] = mContext.getResources().getString(R.string.card_upper);
                    txts[1] = "*" + CommonUtils.getAmountText(receiptPaymentModel.getAmount());

                    sunmiPrinterService.printColumnsString(txts, width, align, null);

                    sunmiPrinterService.printTextWithFont("  " + receiptPaymentModel.getCardName() + "\n", null, 20, null);

                    String maskedCardNum = PrintHelper.getMaskedCardNumber(receiptPaymentModel.getCardNumber());
                    sunmiPrinterService.printTextWithFont("  " + maskedCardNum + "\n", null, 20, null);
                }

                sunmiPrinterService.setAlignment(0, null);
                String retrefNumStr = mContext.getResources().getString(R.string.sequence_number)
                        .concat(" : ")
                        .concat(String.format("%06d", receiptPaymentModel.getfNum()));
                sunmiPrinterService.printTextWithFont("  " + retrefNumStr + "\n", null, 20, null);

                //ADD LINE
                PrintHelper.addLineText2(sunmiPrinterService);
            }

            //PRINT BARCODE
            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);
            sunmiPrinterService.printBarCode("{C1234567890123456",  8, 70, 3, 2, null);

            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);

            //PRINT CHEQUE NO AND TABLE NO
            PrintHelper.printChequeAndTableVars(mContext, sunmiPrinterService, printReceiptModel.getChequeNo(), printReceiptModel.getTableNo());

            //PRINT MERSIS
            String mersis = mContext.getString(R.string.mersis_upper)
                    .concat(" : ")
                    .concat(printReceiptModel.getMersisNo());
            sunmiPrinterService.printTextWithFont(mersis + "\n", null, 20, null);

            //PRINT EMAIL
            sunmiPrinterService.printTextWithFont(printReceiptModel.getEmail() + "\n", null, 20, null);
            sunmiPrinterService.printTextWithFont(" " + "\n", null, 20, null);

            //PRINT EKU AND ZNO
            /*txts[0] = mContext.getResources().getString(R.string.eku_no_upper)
                    .concat(":")
                    .concat(String.valueOf(printReceiptModel.getEkuNo()));
            txts[1] = mContext.getResources().getString(R.string.z_no_upper)
                    .concat(":")
                    .concat(String.valueOf(printReceiptModel.getBatchNum()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            sunmiPrinterService.printTextWithFont(" " + "\n\n\n", null, 20, null);*/

            //MERCHANT AND TERMINAL NO
            txts[0] = mContext.getResources().getString(R.string.merchant_no_upper)
                    .concat(":")
                    .concat(String.valueOf(printReceiptModel.getMerchantNum()));
            txts[1] = mContext.getResources().getString(R.string.terminal_upper)
                    .concat(":")
                    .concat(String.valueOf(printReceiptModel.getTerminalNum()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //APPCODE, BATCH, STAN
            txts[0] = mContext.getResources().getString(R.string.approve_code_upper)
                    .concat(": ")
                    .concat(String.valueOf(printReceiptModel.getApproveCode()));
            txts[1] = mContext.getResources().getString(R.string.batch_upper)
                    .concat(":")
                    .concat(String.valueOf(printReceiptModel.getBatchNum()))
                    .concat("/")
                    .concat(mContext.getResources().getString(R.string.stan_upper))
                    .concat(":")
                    .concat(String.valueOf(printReceiptModel.getStanNum()));

            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //SALE DATE
            sunmiPrinterService.printTextWithFont(" " + "\n\n", null, 20, null);
            PrintHelper.printSaleDateRow(mContext, sunmiPrinterService, printReceiptModel.getReceiptDate());

            //PRINT CARD INFOS
            PrintHelper.printCardTypeAndAmountRows(mContext, sunmiPrinterService, printReceiptModel);

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


            //PRINT EKU AND ZNO
            //PrintHelper.printEkuAndZNo(mContext, sunmiPrinterService, printReceiptModel.getEkuNo(), printReceiptModel.getBatchNum());

            //PRINT TEST DEVICE
            PrintHelper.printTestDeviceLabel(mContext, sunmiPrinterService);

            sunmiPrinterService.printTextWithFont(printReceiptModel.getDeviceRegisterId() + "\b" + "\n", null, 25, null);


            //Total
            /*txts[0] = mContext.getResources().getString(R.string.total);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //Amount
            txts[0] = mContext.getResources().getString(R.string.amount);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getSubTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //Tip
            txts[0] = mContext.getResources().getString(R.string.tip);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getTipAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //Payment Type
            txts[0] = mContext.getResources().getString(R.string.payment_type);
            txts[1] = CommonUtils.getLanguage().equals(LANGUAGE_TR) ? printReceiptModel.getPaymentType().getLabelTr() : printReceiptModel.getPaymentType().getLabelEn();
            sunmiPrinterService.printColumnsString(txts, width, align, null);*/


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


    public void printMerchantReceipt(){
        if(sunmiPrinterService == null){
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);

            //PRINT NO FINANCIAL LABEL
            PrintHelper.printNoFinancialLabel(mContext, sunmiPrinterService);

            //PRINT MERCHANT AND TERMINAL
            PrintHelper.printMerchantAndTerminalRow(mContext, sunmiPrinterService, printReceiptModel.getMerchantNum(), printReceiptModel.getTerminalNum());

            //APPCODE, BATCH, STAN
            PrintHelper.printAppCodeBatchAndStan(mContext, sunmiPrinterService, printReceiptModel.getApproveCode(), printReceiptModel.getBatchNum(), printReceiptModel.getStanNum());

            //SALE DATE
            PrintHelper.printSaleDateRow(mContext, sunmiPrinterService, printReceiptModel.getReceiptDate());

            //PRINT CARD INFOS
            PrintHelper.printCardTypeAndAmountRows(mContext, sunmiPrinterService, printReceiptModel);

            //PRINT GOOD AND SERVICES
            PrintHelper.printGoodServicesInfo(mContext, sunmiPrinterService);

            //PRINT BELONGS TO INFO
            PrintHelper.printBelongsToMerchant(mContext, sunmiPrinterService);

            //PRINT RECEIPT NO
            PrintHelper.printOrderNum(mContext, sunmiPrinterService, printReceiptModel.getOrderNum());

            //PRINT EKU AND ZNO
            //PrintHelper.printEkuAndZNo(mContext, sunmiPrinterService, printReceiptModel.getEkuNo(), printReceiptModel.getBatchNum());

            //PRINT NO FINANCIAL LABEL
            PrintHelper.printNoFinancialLabel(mContext, sunmiPrinterService);

            sunmiPrinterService.printTextWithFont(printReceiptModel.getDeviceRegisterId() + "\b" + "\n", null, 25, null);
            sunmiPrinterService.autoOutPaper(null);

            sunmiPrinterService.exitPrinterBufferWithCallback(true, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*public void printReceiptOld(){
        if(sunmiPrinterService == null){
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            sunmiPrinterService.enterPrinterBuffer(true);
            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);

            sunmiPrinterService.printTextWithFont(mContext.getResources().getString(R.string.app_name).concat(" ")
                    .concat(mContext.getResources().getString(R.string.receipt))+ "\b\n", null, 40, null);

            sunmiPrinterService.printTextWithFont(CommonUtils.getAmountTextWithCurrency(printReceiptModel.getReceiptAmount()) + "\n\n", null, 30, null);

            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_pos_bg_white_64dp);
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
            int width[] = new int[]{2, 1};
            int align[] = new int[]{0, 2};

            for(SaleItemPojo saleItemPojo : printReceiptModel.getSaleItemPojos()){
                txts[0] = saleItemPojo.getName();
                txts[1] = CommonUtils.getAmountTextWithCurrency(saleItemPojo.getAmount());
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if(printReceiptModel.getDiscounts().size() > 0){

                String discTitle = "--------" + mContext.getResources().getString(R.string.discounts);

                if(paper == 1){
                    discTitle = discTitle + "----------------------".substring(0, 32 - discTitle.length());
                    sunmiPrinterService.printText(discTitle + "\n", null);
                }else{
                    discTitle = discTitle + "----------------------".substring(0, 48 - discTitle.length());
                    sunmiPrinterService.printText(discTitle + "\n", null);
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
            txts[0] = mContext.getResources().getString(R.string.subtotal);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getPurchaseSubTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //Tax Amount

            double taxAmount = printReceiptModel.getTaxAmount() / saleModel.getTransactions().size();
            String salesTaxStr = mContext.getResources().getString(R.string.sales_tax).concat(" - ")
                    .concat(mContext.getResources().getString(R.string.included)).concat(", ")
                    .concat(CommonUtils.getAmountTextWithCurrency(taxAmount));
            sunmiPrinterService.printTextWithFont(salesTaxStr + "\n", null, 17, null);

            //Total
            txts[0] = mContext.getResources().getString(R.string.total);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Amount
            txts[0] = mContext.getResources().getString(R.string.amount);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getSubTotalAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //Tip
            txts[0] = mContext.getResources().getString(R.string.tip);
            txts[1] = CommonUtils.getAmountTextWithCurrency(printReceiptModel.getTipAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Payment Type
            txts[0] = mContext.getResources().getString(R.string.payment_type);
            txts[1] = CommonUtils.getLanguage().equals(LANGUAGE_TR) ? printReceiptModel.getPaymentType().getLabelTr() : printReceiptModel.getPaymentType().getLabelEn();
            sunmiPrinterService.printColumnsString(txts, width, align, null);


            //sunmiPrinterService.printTextWithFont("Toplam Tutar:          59.00 TL\b", null, 40, null);
            //sunmiPrinterService.setAlignment(1, null);
            //sunmiPrinterService.printQRCode("ugur.gogebakan.tutar.59.00tl", 10, 0, null);
            //sunmiPrinterService.setFontSize(36, null);
            //sunmiPrinterService.printText("Bu barkoddur", null);
            sunmiPrinterService.autoOutPaper(null);

            sunmiPrinterService.exitPrinterBufferWithCallback(true, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }*/
}
