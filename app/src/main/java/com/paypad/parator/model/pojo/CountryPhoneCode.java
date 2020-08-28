package com.paypad.parator.model.pojo;

public class CountryPhoneCode {
    private String countryCode;
    private String dialCode;

    public CountryPhoneCode(String countryCode, String dialCode) {
        this.countryCode = countryCode;
        this.dialCode = dialCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }
}
