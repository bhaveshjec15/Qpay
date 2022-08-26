package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityLoginBinding
import com.qpay.android.databinding.ActivityRedeemBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.RedeemRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RedeemActivity : AppCompatActivity() {
  private lateinit var binding: ActivityRedeemBinding
  var totalPoints: String? = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_redeem)
    totalPoints = intent.extras?.getString("totalPoints")
    if(totalPoints !=null){
      binding.currentBalance.text = totalPoints +" Points"
    }
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      if (binding.etPoints.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Please enter amount")
      } else {
        var param = RedeemRequest(binding.etPoints.text.toString().toInt())
        callApi(param, this)
      }
    }

  }

  private fun callApi(param: RedeemRequest, redeemActivity: RedeemActivity) {

    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().redeemPoint( getStringShrd(baseContext,
      CommonUtils.accessToken),param)

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1 = response.body()?.string()
        var jsonObject: JSONObject? = null
        if(response1 == null){
          jsonObject = JSONObject(response.errorBody()?.string())
        }else{
          jsonObject = JSONObject(response1)
        }
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode==200) {
          binding.etPoints.setText("")
          showSnackBar(redeemActivity, binding.mainLayout, message)
        } else {
          showSnackBar(redeemActivity, binding.mainLayout, message)
        }
        Log.e("res_redeem", jsonObject.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

}