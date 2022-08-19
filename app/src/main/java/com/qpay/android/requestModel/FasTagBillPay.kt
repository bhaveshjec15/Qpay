package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class FasTagBillPay(
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
  @SerializedName("customerParams")
  val customerParams: VehicleData
)

data class VehicleData(
  @SerializedName("Vehicle Number")
  val number: String?
)