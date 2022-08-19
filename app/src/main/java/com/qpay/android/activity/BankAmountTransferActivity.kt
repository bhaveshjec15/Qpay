package com.qpay.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityBankAmountTransferBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.BankAmountTransferRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankAmountTransferActivity : AppCompatActivity() {
  private lateinit var binding: ActivityBankAmountTransferBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_bank_amount_transfer)
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      if(binding.etAmount.text.toString().isNullOrEmpty()){
        showSnackBar(this, binding.mainLayout, "Enter Amount")
      }
      else{
        var param = BankAmountTransferRequest(binding.etAmount.text.toString().toInt())
        callApiAmountTransfer(param, this)
      }
    }
  }

  private fun callApiAmountTransfer(
    param: BankAmountTransferRequest,
    bankAmountTransferActivity: BankAmountTransferActivity
  ) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().amountTransfer(
      getStringShrd(baseContext,
        CommonUtils.accessToken),param)

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1: String? = null
        if(response.body()== null){
          response1 = response.errorBody()?.string()
        }else{
          response1 = response.body()?.string()
        }
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode==200) {
          showSnackBar(bankAmountTransferActivity, binding.mainLayout, message)
          Handler().postDelayed(Runnable { finish() }, 2000)

        } else {
          showSnackBar(bankAmountTransferActivity, binding.mainLayout, message)
        }
        Log.e("response", "pinsetup: "+response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}