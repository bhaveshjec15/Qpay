package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class AddBankRequest(
  @SerializedName("name")
  val name: String,
  @SerializedName("email")
  val email: String,
  @SerializedName("phone")
  val phone: String,
  @SerializedName("bankAccount")
  val bankAccount: String,
  @SerializedName("ifsc")
  val ifsc: String,
  @SerializedName("address1")
  val address1: String,
  @SerializedName("city")
  val city: String,
  @SerializedName("state")
  val state: String,
  @SerializedName("pincode")
  val pincode: String,
)