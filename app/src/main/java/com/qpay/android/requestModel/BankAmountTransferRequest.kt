package com.qpay.android.requestModel

import com.google.gson.annotations.SerializedName

data class BankAmountTransferRequest(
  @SerializedName("amount")
  val amount: Int,
)