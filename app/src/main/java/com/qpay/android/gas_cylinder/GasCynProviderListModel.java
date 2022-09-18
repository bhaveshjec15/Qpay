package com.qpay.android.gas_cylinder;

public class GasCynProviderListModel {
    String billerId,billerName, paramName, logoUrl, fetchOption;

    public String getFetchOption() {
        return fetchOption;
    }

    public void setFetchOption(String fetchOption) {
        this.fetchOption = fetchOption;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}
