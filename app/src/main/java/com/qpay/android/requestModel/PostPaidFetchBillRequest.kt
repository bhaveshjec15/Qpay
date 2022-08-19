package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class PostPaidFetchBillRequest(
  @SerializedName("billerId")
  val billerId: String,
  @SerializedName("category")
  val category: String,
  @SerializedName("mobileNo")
  val mobileNo: String,
  @SerializedName("customerParams")
  val customerParams: PostPaidData
)

data class PostPaidData(
  @SerializedName("Mobile Number")
  val number: String
)