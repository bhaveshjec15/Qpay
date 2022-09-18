package com.qpay.android.rechargePostPaid

import com.google.gson.annotations.SerializedName

data class PostPaidQuickBillPay(
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
  @SerializedName("QuickPay")
  val QuickPay: Boolean?,
  @SerializedName("customerParams")
  val customerParams: Map<String,String>
)
