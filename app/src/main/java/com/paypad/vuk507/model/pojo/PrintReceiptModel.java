package com.paypad.vuk507.model.pojo;

import com.paypad.vuk507.model.SaleItem;

import java.util.Date;
import java.util.List;

public class PrintReceiptModel {

    private String merchantName;
    private String firmName;
    private String address;
    private String phoneNumber;
    private String taxOffice;
    private String taxNumber;

    private String receiptId;
    private Date receiptDate;
    private double totalAmount;
    private double totalTipAmount;
    private double totalTaxAmount;
    private int fNo;
    private int zNo;

    private int chequeNo;
    private int tableNo;
    private String mersisNo;
    private String email;
    private int ekuNo;

    private String merchantNum;
    private String terminalNum;
    private int approveCode;
    private int batchNum;
    private int stanNum;
    private String deviceRegisterId;
    private Date fDate;

    private List<SaleItemPojo> saleItems;
    private List<ReceiptDiscountModel> discounts;
    private List<ReceiptTaxModel> receiptTaxModels;
    //private List<ReceiptPaymentModel> receiptPaymentModels;
    private ReceiptPaymentModel receiptPaymentModel;

    public Date getfDate() {
        return fDate;
    }

    public void setfDate(Date fDate) {
        this.fDate = fDate;
    }

    public int getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(int chequeNo) {
        this.chequeNo = chequeNo;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
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

    public String getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(String merchantNum) {
        this.merchantNum = merchantNum;
    }

    public String getTerminalNum() {
        return terminalNum;
    }

    public void setTerminalNum(String terminalNum) {
        this.terminalNum = terminalNum;
    }

    public int getApproveCode() {
        return approveCode;
    }

    public void setApproveCode(int approveCode) {
        this.approveCode = approveCode;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    public int getStanNum() {
        return stanNum;
    }

    public void setStanNum(int stanNum) {
        this.stanNum = stanNum;
    }

    public String getDeviceRegisterId() {
        return deviceRegisterId;
    }

    public void setDeviceRegisterId(String deviceRegisterId) {
        this.deviceRegisterId = deviceRegisterId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public List<ReceiptTaxModel> getReceiptTaxModels() {
        return receiptTaxModels;
    }

    public void setReceiptTaxModels(List<ReceiptTaxModel> receiptTaxModels) {
        this.receiptTaxModels = receiptTaxModels;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public List<SaleItemPojo> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItemPojo> saleItems) {
        this.saleItems = saleItems;
    }

    public List<ReceiptDiscountModel> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<ReceiptDiscountModel> discounts) {
        this.discounts = discounts;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalTipAmount() {
        return totalTipAmount;
    }

    public void setTotalTipAmount(double totalTipAmount) {
        this.totalTipAmount = totalTipAmount;
    }

    /*public List<ReceiptPaymentModel> getReceiptPaymentModels() {
        return receiptPaymentModels;
    }

    public void setReceiptPaymentModels(List<ReceiptPaymentModel> receiptPaymentModels) {
        this.receiptPaymentModels = receiptPaymentModels;
    }*/

    public ReceiptPaymentModel getReceiptPaymentModel() {
        return receiptPaymentModel;
    }

    public void setReceiptPaymentModel(ReceiptPaymentModel receiptPaymentModel) {
        this.receiptPaymentModel = receiptPaymentModel;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public int getfNo() {
        return fNo;
    }

    public void setfNo(int fNo) {
        this.fNo = fNo;
    }

    public int getzNo() {
        return zNo;
    }

    public void setzNo(int zNo) {
        this.zNo = zNo;
    }


    public static class ReceiptDiscountModel{
        private int discountId;
        private String discountName;
        private double totalDiscountAmount;

        public int getDiscountId() {
            return discountId;
        }

        public void setDiscountId(int discountId) {
            this.discountId = discountId;
        }

        public String getDiscountName() {
            return discountName;
        }

        public void setDiscountName(String discountName) {
            this.discountName = discountName;
        }

        public double getTotalDiscountAmount() {
            return totalDiscountAmount;
        }

        public void setTotalDiscountAmount(double totalDiscountAmount) {
            this.totalDiscountAmount = totalDiscountAmount;
        }
    }

    public static class ReceiptPaymentModel  {
        private double amount;
        private long paymentType;
        private String cardNumber;
        private String cardName;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public long getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(long paymentType) {
            this.paymentType = paymentType;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }
    }

    public static class ReceiptTaxModel  {
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
}


