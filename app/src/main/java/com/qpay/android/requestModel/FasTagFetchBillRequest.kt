package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class FasTagFetchBillRequest(
  @SerializedName("billerId")
  val billerId: String,
  @SerializedName("category")
  val category: String,
  @SerializedName("mobileNo")
  val mobileNo: String,
  @SerializedName("customerParams")
  val customerParams: VehiclData
)

data class VehiclData(
  @SerializedName("Vehicle Number")
  val number: String
)