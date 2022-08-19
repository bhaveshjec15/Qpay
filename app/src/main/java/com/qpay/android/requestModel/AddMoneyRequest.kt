package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class AddMoneyRequest(
  @SerializedName("type")
  val type: String,
  @SerializedName("amount")
  val amount: Int,
  @SerializedName("remark")
  val remark: String,
  @SerializedName("transaction_id")
  val transaction_id: String,
)