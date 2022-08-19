package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class PinSetUpRequest(
  @SerializedName("pin")
  val otpCode: String,
)