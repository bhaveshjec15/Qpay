package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class ReferCodeRequest(
  @SerializedName("referral_code")
  val referral_code: String,
)