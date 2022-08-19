package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class FasTagBillerRequest(
  @SerializedName("category")
  val category: String,
  @SerializedName("pageNo")
  val pageNo: Int,
  @SerializedName("pageSize")
  val pageSize: Int,
)