package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class TokenRequest(
  @SerializedName("amount")
  val amount: Int,
)