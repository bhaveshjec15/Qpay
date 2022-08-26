package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class RedeemRequest(
  @SerializedName("amount")
  val amount: Int,
)