package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityLoginBinding
import com.qpay.android.databinding.ActivityReferalCodeBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.PinSetUpRequest
import com.qpay.android.requestModel.ReferCodeRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.saveBooleanShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReferalCodeActivity : AppCompatActivity() {
  private lateinit var binding: ActivityReferalCodeBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_referal_code)

  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }
    binding.tvSkip.setOnClickListener {
      val intent = Intent(this, MainActivity::class.java)
      startActivity(intent)
      finish()

    }

    binding.btnSubmit.setOnClickListener {
      if (binding.edtReferCode.text.isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Please Enter Valid Refer Code")
      } else {
        var param = ReferCodeRequest(binding.edtReferCode.text.toString())
        callApi(param, this)
      }
    }
  }

  private fun callApi(param: ReferCodeRequest, pinSetUpActivity: ReferalCodeActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().referCode(
      getStringShrd(baseContext, CommonUtils.accessToken), param
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1: String? = null
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }

        Log.e("response", "referCode: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          saveBooleanShrd(pinSetUpActivity, CommonUtils.isLogin, true)
          val intent = Intent(pinSetUpActivity, MainActivity::class.java)
          startActivity(intent)
          finish()
        } else {
          showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}