package com.paypad.vuk507.menu.reports.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;

import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.FinancialReportsEnum;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.PrintReportModel;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.sunmiutils.ESCUtil;
import com.paypad.vuk507.utils.sunmiutils.SunmiPrintHelper;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class FinancialReportManager {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    private int sunmiPrinter = CheckSunmiPrinter;
    private SunmiPrinterService sunmiPrinterService;
    private static SunmiPrintHelper helper;
    private Context context;
    private FinancialReportsEnum financialReportsType;
    private List<SaleModel> saleModels;
    private String userName;
    private PrintReportModel printReportModel;

    public FinancialReportManager(Context context, FinancialReportsEnum financialReportsType, List<SaleModel> saleModels, String userName) {
        this.helper = SunmiPrintHelper.getInstance();
        this.sunmiPrinterService = helper.getSunmiPrinterService();
        this.sunmiPrinter = SunmiPrintHelper.getLostSunmiPrinter();
        this.context = context;
        this.financialReportsType = financialReportsType;
        this.saleModels = saleModels;
        this.userName = userName;
        fillReportModel();
    }

    private void fillReportModel() {
        printReportModel = new PrintReportModel();
        printReportModel.setFileTypeModels(new ArrayList<>());
        printReportModel.setReportTaxModels(new ArrayList<>());
        printReportModel.setInformationReceiptModels(new ArrayList<>());

        printReportModel.setMerchantName("BODRUM BELEDIYE TUR.");                              //TODO
        printReportModel.setFirmName("INS GIDA ENERJI SAN. VE TIC. A.S.");                     //TODO
        printReportModel.setAddress("TORBA MAH. M.KEMAL ATATURK CD. No: 153 BODRUM MUGLA");    //TODO
        printReportModel.setPhoneNumber("0 252 317 37 37");                                    //TODO
        printReportModel.setTaxOffice("BODRUM V.D. 1800028646");

        printReportModel.setReportId(UUID.randomUUID().toString());
        printReportModel.setReportDate(new Date());
        printReportModel.setReportTitle(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? financialReportsType.getLabelTr().toUpperCase()
                : financialReportsType.getLabelEn().toUpperCase());
        printReportModel.setReportNum(1);                                                       //TODO

        setReportTaxModels();
        setSaleInformation();
        setCumulatives();



    }

    private void setReportTaxModels() {
        List<TaxModel> taxModels = DataUtils.getAllTaxes(userName);

        for(TaxModel taxModel : taxModels){

            PrintReportModel.ReportTaxModel reportTaxModel = new PrintReportModel.ReportTaxModel();
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
            printReportModel.getReportTaxModels().add(reportTaxModel);
        }
    }

    private void setSaleInformation() {
        for(PrintReportModel.ReportTaxModel reportTaxModel : printReportModel.getReportTaxModels()){
            printReportModel.setTotTaxAmount(CommonUtils.round(printReportModel.getTotTaxAmount() + reportTaxModel.getTaxAmount(), 2));
            printReportModel.setTotSaleAmount(CommonUtils.round(printReportModel.getTotSaleAmount() + reportTaxModel.getTotalAmount(), 2));
        }
        printReportModel.setDeclaredTaxAmount(printReportModel.getTotTaxAmount());

        for(SaleModel saleModel : saleModels){
            double discountAmountOfSale = OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems());
            printReportModel.setTotDiscountAmount(CommonUtils.round(discountAmountOfSale + printReportModel.getTotDiscountAmount(), 2));
        }

        printReportModel.setIncreaseAmount(0d);   //TODO
    }

    private void setCumulatives() {
        printReportModel.setCumTaxAmount(0d);           //TODO
        printReportModel.setCumSaleAmount(0d);          //TODO
    }

    public void printZReport(Context context, InnerResultCallbcak callbcak){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText("Z Raporu\n", null);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_discount_black_24dp);
            sunmiPrinterService.printBitmap(bitmap, null);
            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }
            sunmiPrinterService.printTextWithFont("Su tarihler arasindaki Z raporu\n",
                    null, 12, null);
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }
            String txts[] = new String[]{"Kola", "17.00 TL"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            txts[0] = "Fanta";
            txts[1] = "20.00 TL";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "Makarna";
            txts[1] = "10.00 TL";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "Susi";
            txts[1] = "11.50 TL";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "Cay";
            txts[1] = "11.00 TL";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "Kek";
            txts[1] = "10.00 TL";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            sunmiPrinterService.printTextWithFont("Toplam Tutar:          59.00 TL\b", null, 40, null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printQRCode("ugur.gogebakan.tutar.59.00tl", 10, 0, null);
            sunmiPrinterService.setFontSize(36, null);
            sunmiPrinterService.printText("Bu barkoddur", null);
            sunmiPrinterService.autoOutPaper(null);

            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
