package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class SendAmountRequest(
  @SerializedName("mobile_number")
  val mobile_number: String,
  @SerializedName("amount")
  val amount: Int,
  @SerializedName("remark")
  val remark: String,
)