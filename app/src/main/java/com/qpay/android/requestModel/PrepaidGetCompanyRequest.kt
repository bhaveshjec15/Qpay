package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class PrepaidGetCompanyRequest(
  @SerializedName("mobileNo")
  val mobileNo: String
)