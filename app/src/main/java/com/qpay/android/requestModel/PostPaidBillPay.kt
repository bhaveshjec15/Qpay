package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class PostPaidBillPay(
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
  val customerParams: PostPaidDataNew
)

data class PostPaidDataNew(
  @SerializedName("Mobile Number")
  val number: String?
)