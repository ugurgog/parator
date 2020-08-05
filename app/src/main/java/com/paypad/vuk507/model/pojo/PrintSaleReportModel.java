package com.paypad.vuk507.model.pojo;

import java.util.Date;
import java.util.List;

public class PrintSaleReportModel {

    private String merchantName;
    private String firmName;
    private String address;
    private String phoneNumber;
    private String taxOffice;

    private String reportId;
    private Date fDate;
    private String reportTitle;
    private long receiptNum;
    private long batchNum;

    private long reportNum;
    private Date reportDate;

    //KDV Bilgileri
    private List<ReportTaxModel> reportTaxModels;

    //Satis Bilgileri
    private double totTaxAmount;
    private double totSaleAmount;
    private double declaredTaxAmount;
    private double totDiscountAmount;
    private double increaseAmount;

    //Kumulatifler
    private double cumTaxAmount;
    private double cumSaleAmount;

    //Belge Tipleri
    private List<FileTypeModel> fileTypeModels;

    //Tamamlanmamis Islemler
    private int cancellationCount;
    private double cancellationAmount;

    //Odeme Bilgileri
    private PaymentModel paymentModel;

    //Bilgi Fisleri
    private List<InformationReceiptModel> informationReceiptModels;

    //Ek Bilgiler
    private double invCollectionAmount;
    private double mealCardAmount;
    private double otherNoTaxAmount;
    private double otherAmount;

    private String mersisNo;
    private String email;
    private int ekuNo;
    private String deviceRegisterId;

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public Date getfDate() {
        return fDate;
    }

    public void setfDate(Date fDate) {
        this.fDate = fDate;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public long getReportNum() {
        return reportNum;
    }

    public void setReportNum(long reportNum) {
        this.reportNum = reportNum;
    }

    public List<ReportTaxModel> getReportTaxModels() {
        return reportTaxModels;
    }

    public void setReportTaxModels(List<ReportTaxModel> reportTaxModels) {
        this.reportTaxModels = reportTaxModels;
    }

    public double getTotTaxAmount() {
        return totTaxAmount;
    }

    public void setTotTaxAmount(double totTaxAmount) {
        this.totTaxAmount = totTaxAmount;
    }

    public double getTotSaleAmount() {
        return totSaleAmount;
    }

    public void setTotSaleAmount(double totSaleAmount) {
        this.totSaleAmount = totSaleAmount;
    }

    public double getDeclaredTaxAmount() {
        return declaredTaxAmount;
    }

    public void setDeclaredTaxAmount(double declaredTaxAmount) {
        this.declaredTaxAmount = declaredTaxAmount;
    }

    public double getTotDiscountAmount() {
        return totDiscountAmount;
    }

    public void setTotDiscountAmount(double totDiscountAmount) {
        this.totDiscountAmount = totDiscountAmount;
    }

    public double getIncreaseAmount() {
        return increaseAmount;
    }

    public void setIncreaseAmount(double increaseAmount) {
        this.increaseAmount = increaseAmount;
    }

    public double getCumTaxAmount() {
        return cumTaxAmount;
    }

    public void setCumTaxAmount(double cumTaxAmount) {
        this.cumTaxAmount = cumTaxAmount;
    }

    public double getCumSaleAmount() {
        return cumSaleAmount;
    }

    public void setCumSaleAmount(double cumSaleAmount) {
        this.cumSaleAmount = cumSaleAmount;
    }

    public List<FileTypeModel> getFileTypeModels() {
        return fileTypeModels;
    }

    public void setFileTypeModels(List<FileTypeModel> fileTypeModels) {
        this.fileTypeModels = fileTypeModels;
    }

    public int getCancellationCount() {
        return cancellationCount;
    }

    public void setCancellationCount(int cancellationCount) {
        this.cancellationCount = cancellationCount;
    }

    public double getCancellationAmount() {
        return cancellationAmount;
    }

    public void setCancellationAmount(double cancellationAmount) {
        this.cancellationAmount = cancellationAmount;
    }

    public PaymentModel getPaymentModel() {
        return paymentModel;
    }

    public void setPaymentModel(PaymentModel paymentModel) {
        this.paymentModel = paymentModel;
    }

    public List<InformationReceiptModel> getInformationReceiptModels() {
        return informationReceiptModels;
    }

    public void setInformationReceiptModels(List<InformationReceiptModel> informationReceiptModels) {
        this.informationReceiptModels = informationReceiptModels;
    }

    public double getInvCollectionAmount() {
        return invCollectionAmount;
    }

    public void setInvCollectionAmount(double invCollectionAmount) {
        this.invCollectionAmount = invCollectionAmount;
    }

    public double getMealCardAmount() {
        return mealCardAmount;
    }

    public void setMealCardAmount(double mealCardAmount) {
        this.mealCardAmount = mealCardAmount;
    }

    public double getOtherNoTaxAmount() {
        return otherNoTaxAmount;
    }

    public void setOtherNoTaxAmount(double otherNoTaxAmount) {
        this.otherNoTaxAmount = otherNoTaxAmount;
    }

    public double getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(double otherAmount) {
        this.otherAmount = otherAmount;
    }

    public String getMersisNo() {
        return mersisNo;
    }

    public void setMersisNo(String mersisNo) {
        this.mersisNo = mersisNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEkuNo() {
        return ekuNo;
    }

    public void setEkuNo(int ekuNo) {
        this.ekuNo = ekuNo;
    }

    public long getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(long receiptNum) {
        this.receiptNum = receiptNum;
    }

    public long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(long batchNum) {
        this.batchNum = batchNum;
    }

    public String getDeviceRegisterId() {
        return deviceRegisterId;
    }

    public void setDeviceRegisterId(String deviceRegisterId) {
        this.deviceRegisterId = deviceRegisterId;
    }

    public static class ReportTaxModel  {
        private long taxId;
        private String taxName;
        private double totalAmount;
        private double taxAmount;

        public long getTaxId() {
            return taxId;
        }

        public void setTaxId(long taxId) {
            this.taxId = taxId;
        }

        public String getTaxName() {
            return taxName;
        }

        public void setTaxName(String taxName) {
            this.taxName = taxName;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public double getTaxAmount() {
            return taxAmount;
        }

        public void setTaxAmount(double taxAmount) {
            this.taxAmount = taxAmount;
        }
    }

    public static class FileTypeModel{
        private String fileName;
        private double totTaxAmount;
        private double totSaleAmount;
        private long saleCount;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public double getTotTaxAmount() {
            return totTaxAmount;
        }

        public void setTotTaxAmount(double totTaxAmount) {
            this.totTaxAmount = totTaxAmount;
        }

        public double getTotSaleAmount() {
            return totSaleAmount;
        }

        public void setTotSaleAmount(double totSaleAmount) {
            this.totSaleAmount = totSaleAmount;
        }

        public long getSaleCount() {
            return saleCount;
        }

        public void setSaleCount(long saleCount) {
            this.saleCount = saleCount;
        }
    }

    public static class InformationReceiptModel{
        private String infoName;
        private long count;
        private double amount;

        public String getInfoName() {
            return infoName;
        }

        public void setInfoName(String infoName) {
            this.infoName = infoName;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }

    public static class PaymentModel{
        private double cashAmount;
        private double cardAmount;
        private double mealCardAmount;
        private double virtualPosAmount;
        private double giftCardAmount;
        private double eftAmount;
        private double eMoneyAmount;
        private double chequeAmount;
        private double openAccountAmount;
        private double totalPaymentAmount;

        public double getTotalPaymentAmount() {
            return totalPaymentAmount;
        }

        public void setTotalPaymentAmount(double totalPaymentAmount) {
            this.totalPaymentAmount = totalPaymentAmount;
        }

        public double getCashAmount() {
            return cashAmount;
        }

        public void setCashAmount(double cashAmount) {
            this.cashAmount = cashAmount;
        }

        public double getCardAmount() {
            return cardAmount;
        }

        public void setCardAmount(double cardAmount) {
            this.cardAmount = cardAmount;
        }

        public double getMealCardAmount() {
            return mealCardAmount;
        }

        public void setMealCardAmount(double mealCardAmount) {
            this.mealCardAmount = mealCardAmount;
        }

        public double getVirtualPosAmount() {
            return virtualPosAmount;
        }

        public void setVirtualPosAmount(double virtualPosAmount) {
            this.virtualPosAmount = virtualPosAmount;
        }

        public double getGiftCardAmount() {
            return giftCardAmount;
        }

        public void setGiftCardAmount(double giftCardAmount) {
            this.giftCardAmount = giftCardAmount;
        }

        public double getEftAmount() {
            return eftAmount;
        }

        public void setEftAmount(double eftAmount) {
            this.eftAmount = eftAmount;
        }

        public double geteMoneyAmount() {
            return eMoneyAmount;
        }

        public void seteMoneyAmount(double eMoneyAmount) {
            this.eMoneyAmount = eMoneyAmount;
        }

        public double getChequeAmount() {
            return chequeAmount;
        }

        public void setChequeAmount(double chequeAmount) {
            this.chequeAmount = chequeAmount;
        }

        public double getOpenAccountAmount() {
            return openAccountAmount;
        }

        public void setOpenAccountAmount(double openAccountAmount) {
            this.openAccountAmount = openAccountAmount;
        }
    }
}
