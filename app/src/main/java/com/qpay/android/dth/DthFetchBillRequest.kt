package com.qpay.android.dth

import com.google.gson.annotations.SerializedName

data class DthFetchBillRequest(
  @SerializedName("billerId")
  val billerId: String,
  @SerializedName("category")
  val category: String,
  @SerializedName("mobileNo")
  val mobileNo: String,
  @SerializedName("customerParams")
  var customerParams: Map<String,String>
)

/*
data class VehiclData(
  @SerializedName("Vehicle Number")
  val number: String
)*/
