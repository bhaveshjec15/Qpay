package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName
import com.qpay.android.utils.getStringShrd

data class ElectricityBillPay(
  @SerializedName("amount")
  val amount: Int?,
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
  val customerParams: Map<String,String>
)

/*
data class NewData(
  @SerializedName(getStringShrd())
  val number: String?
)*/
