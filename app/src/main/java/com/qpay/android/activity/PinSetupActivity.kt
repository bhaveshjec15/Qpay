package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityOtpactivityBinding
import com.qpay.android.databinding.ActivityPinSetupBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.OTPRequest
import com.qpay.android.requestModel.PinSetUpRequest
import com.qpay.android.requestModel.ReferCodeRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinSetupActivity : AppCompatActivity() {
  private lateinit var binding: ActivityPinSetupBinding
  var pinOne: String= ""
  var pinTwo: String= ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_setup)
    hideKeyboardFrom(baseContext,binding.root.rootView)
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener {
      finish()
    }

    binding.createPinView.setOnFinishListener {
     pinOne = it
    }

    binding.confirmPinView.setOnFinishListener {
      pinTwo = it
      checkPinValidation()
    }
  }

  private fun checkPinValidation() {
    if(pinOne.equals(pinTwo)){
     /* val intent = Intent(this, MainActivity::class.java)
      startActivity(intent)*/
      var param = PinSetUpRequest(pinTwo)
      callApi(param, this)
    }
    else{
      showSnackBar(this,binding.mainLayout,"Both pin are not match")
    }
  }

  private fun callApi(param: PinSetUpRequest, pinSetUpActivity: PinSetupActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().pinSetUp(getStringShrd(baseContext,CommonUtils.accessToken),param)

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
          val intent = Intent(pinSetUpActivity, ReferalCodeActivity::class.java)
          startActivity(intent)
        } else {
          showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
        Log.e("response", "pinsetup: "+response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}