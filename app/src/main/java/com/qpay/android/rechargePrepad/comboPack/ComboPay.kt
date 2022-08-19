package com.qpay.android.rechargePrepad.comboPack

import com.google.gson.annotations.SerializedName

data class ElectricityBillPay(
  @SerializedName("amount")
  val amount: Int?,
  @SerializedName("billerId")
  val billerId: String?,
  @SerializedName ("billerName")
  val billerName: String?,
  @SerializedName("circleRefID")
  val circleRefID: String,
  @SerializedName("customerEmailId")
  val customerEmailId: String,
  @SerializedName("customerMobileNo")
  val customerMobileNo: String?,
  @SerializedName("customerName")
  val customerName: String?,
  @SerializedName("operatorCode")
  val operatorCode: String?,
  @SerializedName("planId")
  val planId: String?,
  @SerializedName("fetchRefId")
  val fetchRefId: String?,

)
