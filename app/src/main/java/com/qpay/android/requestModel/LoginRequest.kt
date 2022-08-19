package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class LoginRequest(
  @SerializedName("mobile_number")
  val mobile_number: String,
)