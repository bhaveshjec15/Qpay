package com.qpay.android.rechargePrepad;

import android.graphics.Bitmap;

public class CompanyNameModel {
    String operatorName,operatorId, circleList;

    public String getCircleList() {
        return circleList;
    }

    public void setCircleList(String circleList) {
        this.circleList = circleList;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
}
