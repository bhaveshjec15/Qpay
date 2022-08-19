package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class OTPRequest(
  @SerializedName("mobile_number")
  val mobile_number: String,
  @SerializedName("otp")
  val otp: String,
  @SerializedName("device_type")
  val device_type: String,
  @SerializedName("device_token")
  val device_token: String,
)