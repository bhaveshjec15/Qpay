package com.qpay.android.rechargePrepad

import com.google.gson.annotations.SerializedName

data class PaymentValidationRequestModel(
  @SerializedName("amount")
  val amount: String?,
  @SerializedName("billerId")
  val billerId: String?,
  @SerializedName("circleRefID")
  val circleRefID: String,
  @SerializedName("customerMobileNo")
  val customerMobileNo: String,
  @SerializedName("customerName")
  val customerName: String?,
  @SerializedName("operatorCode")
  val operatorCode: String?,

)
