package com.qpay.android.rechargePrepad

import com.google.gson.annotations.SerializedName

data class RechargePay(
  @SerializedName("amount")
  val amount: String?,
  @SerializedName("billerId")
  val billerId: String?,
  @SerializedName("billerName")
  val billerName: String?,
  @SerializedName("circleRefID")
  val circleRefID: String?,
  @SerializedName("customerMobileNo")
  val customerMobileNo: String?,
  @SerializedName("customerName")
  val customerName: String?,
  @SerializedName("customerEmailId")
  val customerEmailId: String?,
  @SerializedName("fetchRefId")
  val fetchRefId: String?,
  @SerializedName("operatorCode")
  val operatorCode: String?,
  @SerializedName("planId")
  val planId: String?,

)
