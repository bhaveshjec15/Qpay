package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class ElectricityFetchBillRequest(
  @SerializedName("billerId")
  val billerId: String,
  @SerializedName("category")
  val category: String,
  @SerializedName("mobileNo")
  val mobileNo: String,
  @SerializedName("customerParams")
  val customerParams:  Map<String,String>
)

/*
data class NumberData(
  @SerializedName("K Number")
  val number: String
)*/
