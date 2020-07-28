package com.paypad.vuk507.menu.reports.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.FinancialReportsEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.PrintSaleReportModel;
import com.paypad.vuk507.model.pojo.SaleItemPojo;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
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
import java.util.UUID;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class PrintSalesReportManager {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    private int sunmiPrinter = CheckSunmiPrinter;
    private SunmiPrinterService sunmiPrinterService;
    private static SunmiPrintHelper helper;
    private Context mContext;
    private FinancialReportsEnum financialReportsType;
    private List<SaleModel> saleModels;
    private String userName;
    private PrintSaleReportModel printSaleReportModel;
    private InnerResultCallbcak callback;

    private Date startDate;
    private Date endDate;

    public void setCallback(InnerResultCallbcak callback) {
        this.callback = callback;
    }

    enum FileTypeEnum{
        OKC_RECEIPTS("OKC REC.", "ÖKC FİŞLERİ"),
        INVOICE("INVOICE", "FATURA"),
        SMM("SMM", "SMM"),
        MM("MM", "MM"),
        TICKET("TICKET", "BİLET"),
        EXPENSES("NOTE OF EXPEN.", "GİDER PUS.");

        private final String labelTr;
        private final String labelEn;

        FileTypeEnum(String labelEn, String labelTr) {
            this.labelTr = labelTr;
            this.labelEn = labelEn;
        }

        public String getLabelTr() {
            return labelTr;
        }

        public String getLabelEn() {
            return labelEn;
        }
    }

    enum InformationReceiptEnum{
        INVOICE("INVOICE", "FATURA"),
        MEAL_CARD("MEAL CARD", "YEMEK KARTI"),
        EARNEST("EARNEST", "AVANS"),
        CAR_PARK_RECEIPT("CAR PARK REC.", "OTO PARK F."),
        INVOICE_COLLECTION("INVOICE COLL.", "FATURA TAH."),
        CURRENT_ACCOUNT("CURRENT ACC.", "CARİ HESAP"),
        OTHER_INFO_RECEIPT("OTHER INFO R.", "DİĞER BİL. F.");

        private final String labelTr;
        private final String labelEn;

        InformationReceiptEnum(String labelEn, String labelTr) {
            this.labelTr = labelTr;
            this.labelEn = labelEn;
        }

        public String getLabelTr() {
            return labelTr;
        }

        public String getLabelEn() {
            return labelEn;
        }
    }

    public PrintSalesReportManager(Context context, FinancialReportsEnum financialReportsType, List<SaleModel> saleModels, String userName,
                                   Date startDate, Date endDate) {
        this.helper = SunmiPrintHelper.getInstance();
        this.sunmiPrinterService = helper.getSunmiPrinterService();
        this.sunmiPrinter = SunmiPrintHelper.getLostSunmiPrinter();
        this.mContext = context;
        this.financialReportsType = financialReportsType;
        this.saleModels = saleModels;
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
        fillReportModel();
    }

    private void fillReportModel() {
        printSaleReportModel = new PrintSaleReportModel();
        printSaleReportModel.setFileTypeModels(new ArrayList<>());
        printSaleReportModel.setReportTaxModels(new ArrayList<>());
        printSaleReportModel.setInformationReceiptModels(new ArrayList<>());

        printSaleReportModel.setMerchantName("BODRUM BELEDIYE TUR.");                              //TODO
        printSaleReportModel.setFirmName("INS GIDA ENERJI SAN. VE TIC. A.S.");                     //TODO
        printSaleReportModel.setAddress("TORBA MAH. M.KEMAL ATATURK CD. No: 153 BODRUM MUGLA");    //TODO
        printSaleReportModel.setPhoneNumber("0 252 317 37 37");                                    //TODO
        printSaleReportModel.setTaxOffice("BODRUM V.D. 1800028646");

        printSaleReportModel.setReportId(UUID.randomUUID().toString());
        printSaleReportModel.setfDate(new Date());
        printSaleReportModel.setReportTitle(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? financialReportsType.getLabelTr().toUpperCase()
                : financialReportsType.getLabelEn().toUpperCase());
        printSaleReportModel.setReportNum(1);                                                       //TODO

        setReportDate();
        setReportTaxModels();
        setSaleInformation();
        setCumulatives();
        setFileTypeModels();
        setUncompletedItems();
        setPaymentModel();
        setInformationReceiptModels();
        setAdditionalInformation();

        printSaleReportModel.setfNo(222);   //TODO
        printSaleReportModel.setzNo(111);   //TODO
        printSaleReportModel.setMersisNo("05352900403");               //TODO
        printSaleReportModel.setEmail("ugur.gogebakan@garaj2.com");    //TODO
        printSaleReportModel.setEkuNo(1);                              //TODO
        printSaleReportModel.setDeviceRegisterId("JH 20082508");       //TODO
    }

    private void setReportDate(){
        printSaleReportModel.setReportDate(new Date());
    }

    private void setReportTaxModels() {
        List<TaxModel> taxModels = DataUtils.getAllTaxes(userName);

        for(TaxModel taxModel : taxModels){

            PrintSaleReportModel.ReportTaxModel reportTaxModel = new PrintSaleReportModel.ReportTaxModel();
            reportTaxModel.setTaxId(taxModel.getId());
            reportTaxModel.setTaxName("%" + CommonUtils.getDoubleStrValueForView(taxModel.getTaxRate(), TYPE_RATE));

            for(SaleModel saleModel : saleModels){
                for(SaleItem saleItem : saleModel.getSaleItems()){

                    OrderItemTax orderItemTax = saleItem.getOrderItemTaxes().get(0);

                    if(taxModel.getId() == orderItemTax.getTaxId()){
                        reportTaxModel.setTaxAmount(CommonUtils.round(reportTaxModel.getTaxAmount() + saleItem.getTaxAmount(), 2));
                        reportTaxModel.setTotalAmount(CommonUtils.round(reportTaxModel.getTotalAmount() + saleItem.getGrossAmount(), 2));
                    }
                }
            }
            printSaleReportModel.getReportTaxModels().add(reportTaxModel);
        }
    }

    private void setSaleInformation() {
        for(PrintSaleReportModel.ReportTaxModel reportTaxModel : printSaleReportModel.getReportTaxModels()){
            printSaleReportModel.setTotTaxAmount(CommonUtils.round(printSaleReportModel.getTotTaxAmount() + reportTaxModel.getTaxAmount(), 2));
            printSaleReportModel.setTotSaleAmount(CommonUtils.round(printSaleReportModel.getTotSaleAmount() + reportTaxModel.getTotalAmount(), 2));
        }
        printSaleReportModel.setDeclaredTaxAmount(printSaleReportModel.getTotTaxAmount());

        for(SaleModel saleModel : saleModels){
            double discountAmountOfSale = OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems());
            printSaleReportModel.setTotDiscountAmount(CommonUtils.round(discountAmountOfSale + printSaleReportModel.getTotDiscountAmount(), 2));
        }

        printSaleReportModel.setIncreaseAmount(0d);   //TODO
    }

    private void setCumulatives() {
        printSaleReportModel.setCumTaxAmount(0d);           //TODO
        printSaleReportModel.setCumSaleAmount(0d);          //TODO
    }

    private void setFileTypeModels(){

        FileTypeEnum[] fileTypeEnums = FileTypeEnum.values();
        List<PrintSaleReportModel.FileTypeModel> fileTypeModels = new ArrayList<>();

        for(FileTypeEnum fileTypeEnum : fileTypeEnums){
            PrintSaleReportModel.FileTypeModel fileTypeModel = new PrintSaleReportModel.FileTypeModel();
            fileTypeModel.setFileName(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? fileTypeEnum.getLabelTr() : fileTypeEnum.getLabelEn());
            fileTypeModel.setSaleCount(0);              //TODO
            fileTypeModel.setTotSaleAmount(0d);         //TODO
            fileTypeModel.setTotTaxAmount(0d);          //TODO
            fileTypeModels.add(fileTypeModel);
        }
        printSaleReportModel.setFileTypeModels(fileTypeModels);
    }

    private void setUncompletedItems(){
        printSaleReportModel.setCancellationCount(0);                //TODO
        printSaleReportModel.setCancellationAmount(0d);              //TODO
    }

    private void setPaymentModel(){
        PrintSaleReportModel.PaymentModel paymentModel = new PrintSaleReportModel.PaymentModel();

        for(SaleModel saleModel : saleModels){
            for(Transaction transaction : saleModel.getTransactions()){
                if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                    paymentModel.setCashAmount(paymentModel.getCashAmount() + transaction.getTransactionAmount());
                }else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){
                    paymentModel.setCardAmount(paymentModel.getCardAmount() + transaction.getTransactionAmount());
                }
                //TODO
            }
        }

        double totalPaymAmount = CommonUtils.round(paymentModel.getCashAmount() +
                paymentModel.getCardAmount() +
                paymentModel.getMealCardAmount() +
                paymentModel.getVirtualPosAmount() +
                paymentModel.getGiftCardAmount() +
                paymentModel.getEftAmount() +
                paymentModel.geteMoneyAmount() +
                paymentModel.getChequeAmount() +
                paymentModel.getOpenAccountAmount(), 2);

        paymentModel.setTotalPaymentAmount(totalPaymAmount);
        printSaleReportModel.setPaymentModel(paymentModel);
    }

    private void setInformationReceiptModels(){
        List<PrintSaleReportModel.InformationReceiptModel> informationReceiptModels = new ArrayList<>();

        InformationReceiptEnum[] informationReceiptEnums = InformationReceiptEnum.values();

        for(InformationReceiptEnum receiptEnum : informationReceiptEnums){
            PrintSaleReportModel.InformationReceiptModel receiptModel = new PrintSaleReportModel.InformationReceiptModel();
            receiptModel.setInfoName((CommonUtils.getLanguage().equals(LANGUAGE_TR) ? receiptEnum.getLabelTr() : receiptEnum.getLabelEn())
                .concat(" ")
                .concat(mContext.getResources().getString(R.string.count_upper)));
            receiptModel.setAmount(0d);         //TODO
            receiptModel.setCount(0);           //TODO
            informationReceiptModels.add(receiptModel);
        }
        printSaleReportModel.setInformationReceiptModels(informationReceiptModels);
    }

    private void setAdditionalInformation(){
        printSaleReportModel.setInvCollectionAmount(0d);            //TODO
        printSaleReportModel.setMealCardAmount(0d);                 //TODO
        printSaleReportModel.setOtherAmount(0d);                    //TODO
        printSaleReportModel.setOtherNoTaxAmount(0d);               //TODO
    }

    public void printSaleReport(){
        if(sunmiPrinterService == null){
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);

            //PRINT TEST DEVICE
            PrintHelper.printTestDeviceLabel(mContext, sunmiPrinterService);

            //PRINT MERCHANT NAME
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getMerchantName() + "\b" + "\n", null, 25, null);

            //PRINT FIRM NAME
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getFirmName()  + "\n", null, 25, null);

            //PRINT ADDRESS
            List<String> addressList = PrintHelper.getAddressList(paper, printSaleReportModel.getAddress());

            for(String addressLine : addressList){
                sunmiPrinterService.printTextWithFont(addressLine  + "\n", null, 23, null);
            }

            //PRINT TAX OFFICE
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getTaxOffice() + "\n\n", null, 25, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }

            //PRINT DATE AND RECEIPT NO FIELDS
            PrintHelper.printDateAndReceiptNoFields(sunmiPrinterService, mContext, printSaleReportModel.getfDate(), printSaleReportModel.getfNo());

            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }

            sunmiPrinterService.printTextWithFont(" " + "\n", null, 20, null);


            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //PRINT REPORT TITLE
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getReportTitle() + "\b" + "\n", null, 20, null);

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            /***************************************** REPORT DATE AND NO ***************************************************************************/


            String txts[] = new String[2];
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};

            //Print Report Date
            @SuppressLint("SimpleDateFormat") String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(printSaleReportModel.getReportDate());
            txts[0] = mContext.getResources().getString(R.string.report_date_upper);
            txts[1] = dateStr;
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //Print Report Date
            txts[0] = mContext.getResources().getString(R.string.report_no_upper);
            txts[1] = String.valueOf(printSaleReportModel.getReportNum());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /***************************************** KDV BILGILERI ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.tax_information_upper));

            for(PrintSaleReportModel.ReportTaxModel reportTaxModel : printSaleReportModel.getReportTaxModels()){
                txts[0] = mContext.getResources().getString(R.string.tax_upper)
                        .concat(" ")
                        .concat(reportTaxModel.getTaxName());
                txts[1] = "*".concat(CommonUtils.getAmountText(reportTaxModel.getTaxAmount()));
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                txts[0] = mContext.getResources().getString(R.string.total_upper);
                txts[1] = "*".concat(CommonUtils.getAmountText(reportTaxModel.getTotalAmount()));
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            /***************************************** SATIS BILGILERI ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.sale_information_upper));

            txts[0] = mContext.getResources().getString(R.string.total_tax_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getTotTaxAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.total_sales_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getTotSaleAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.announced_tax_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getDeclaredTaxAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.dicount_total_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getTotDiscountAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.increase_total_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getIncreaseAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /***************************************** KUMULATIFLER ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.cumulatives_upper));

            txts[0] = mContext.getResources().getString(R.string.cum_tax_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getCumTaxAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.cum_sale_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getCumSaleAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /***************************************** BELGE TIPLERI ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.file_types_upper));

            for(PrintSaleReportModel.FileTypeModel fileTypeModel : printSaleReportModel.getFileTypeModels()){

                txts[0] = fileTypeModel.getFileName().concat(" ").concat(mContext.getResources().getString(R.string.count_upper));
                txts[1] = String.valueOf(fileTypeModel.getSaleCount());
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                txts[0] = "  -".concat(mContext.getResources().getString(R.string.total_tax_upper));
                txts[1] = "*".concat(CommonUtils.getAmountText(fileTypeModel.getTotTaxAmount()));
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                txts[0] = "  -".concat(mContext.getResources().getString(R.string.total_sales_upper));
                txts[1] = "*".concat(CommonUtils.getAmountText(fileTypeModel.getTotSaleAmount()));
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            /***************************************** TAMAMLANMAMIS ISLEMLER ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.uncompleted_processes_upper));

            txts[0] = mContext.getResources().getString(R.string.cancel_count_upper);
            txts[1] = "*".concat(String.valueOf(printSaleReportModel.getCancellationCount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.cancel_amount_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getCancellationAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /***************************************** ODEME BILGILERI ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.payment_information_upper));

            PrintSaleReportModel.PaymentModel paymentModel = printSaleReportModel.getPaymentModel();

            txts[0] = mContext.getResources().getString(R.string.cash_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getCashAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.card_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getCardAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.other_upper);
            txts[1] = " ";
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.virtual_pos_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getVirtualPosAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.gift_card_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getGiftCardAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.havale_eft_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getEftAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.e_money_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.geteMoneyAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.cheque_bill_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.geteMoneyAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.open_account_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getOpenAccountAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.meal_card_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getMealCardAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.total_payment_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(paymentModel.getTotalPaymentAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /***************************************** BILGI FISLERI ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.information_receipts_upper));

            width = new int[]{2, 1};
            align = new int[]{0, 2};

            long totalInfoCount = 0; double totalInfoAmount = 0d;
            for(PrintSaleReportModel.InformationReceiptModel receiptModel : printSaleReportModel.getInformationReceiptModels()){

                txts[0] = receiptModel.getInfoName();
                txts[1] = String.valueOf(receiptModel.getCount());
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                txts[0] = "  -" + mContext.getResources().getString(R.string.total_price_upper);
                txts[1] = "*".concat(CommonUtils.getAmountText(receiptModel.getAmount()));
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                totalInfoCount = totalInfoCount + receiptModel.getCount();
                totalInfoAmount = CommonUtils.round((totalInfoAmount + receiptModel.getAmount()), 2);
            }

            txts[0] = mContext.getResources().getString(R.string.total_info_receipt_count_upper);
            txts[1] = String.valueOf(totalInfoCount);
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.total_price_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(totalInfoAmount));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /***************************************** ADDITIONAL INFORMATION ***************************************************************************/

            PrintHelper.addLineTextWithValue(sunmiPrinterService, mContext.getResources().getString(R.string.additional_info_upper));

            width = new int[]{2, 1};
            align = new int[]{0, 2};

            txts[0] = mContext.getResources().getString(R.string.financial_receipt_incl_upper);
            txts[1] = " ";
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.invoice_collection_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getInvCollectionAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.meal_card_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getMealCardAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.other_matrahsiz_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getOtherNoTaxAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = mContext.getResources().getString(R.string.payment_information_upper);
            txts[1] = " ";
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            txts[0] = "  -" + mContext.getResources().getString(R.string.other_upper);
            txts[1] = "*".concat(CommonUtils.getAmountText(printSaleReportModel.getOtherAmount()));
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            /*****************************************************************************************************************************************/

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //PRINT REPORT TITLE
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getReportTitle()
                    .concat(" ").concat("SONU")+ "\b" + "\n", null, 20, null);

            //ADD LINE
            PrintHelper.addLineText(sunmiPrinterService);

            //PRINT MERSIS
            sunmiPrinterService.setAlignment(0, null);
            String mersis = mContext.getString(R.string.mersis_upper)
                    .concat(" : ")
                    .concat(printSaleReportModel.getMersisNo());
            sunmiPrinterService.printTextWithFont(mersis + "\n", null, 20, null);

            //PRINT EMAIL
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getEmail() + "\n", null, 20, null);
            sunmiPrinterService.printTextWithFont(" " + "\n", null, 20, null);

            //PRINT EKU AND ZNO
            PrintHelper.printEkuAndZNo(mContext, sunmiPrinterService, printSaleReportModel.getEkuNo(), printSaleReportModel.getzNo());

            //PRINT NO FINANCIAL LABEL
            PrintHelper.printNoFinancialLabel(mContext, sunmiPrinterService);

            //PRINT TEST DEVICE
            PrintHelper.printTestDeviceLabel(mContext, sunmiPrinterService);

            //PRINT REGISTER ID
            sunmiPrinterService.printTextWithFont(printSaleReportModel.getDeviceRegisterId() + "\b" + "\n", null, 25, null);
            sunmiPrinterService.autoOutPaper(null);

            sunmiPrinterService.exitPrinterBufferWithCallback(true, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
