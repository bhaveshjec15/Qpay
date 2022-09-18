package com.qpay.android.dth

import com.google.gson.annotations.SerializedName

data class dthBillPay(
  @SerializedName("amount")
  val amount: String,
  @SerializedName("billerId")
  val billerId: String?,
  @SerializedName("category")
  val category: String,
  @SerializedName("customerMobile")
  val customerMobile: String,
  @SerializedName("customerName")
  val customerName: String?,
  @SerializedName("merchantTrxnRefId")
  val merchantTrxnRefId: String?,
  @SerializedName("QuickPay")
  val QuickPay: Boolean?,
  @SerializedName("customerParams")
  val customerParams: Map<String,String>
)

data class VehicleData(
  @SerializedName("Vehicle Number")
  val number: String?
)