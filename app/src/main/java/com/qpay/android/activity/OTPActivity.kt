package com.qpay.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityOtpactivityBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.OTPRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.saveBooleanShrd
import com.qpay.android.utils.saveStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPActivity : AppCompatActivity() {
  private lateinit var binding: ActivityOtpactivityBinding
  var phoneNo: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_otpactivity)
  }

  override fun onResume() {
    super.onResume()
    startCountDown()
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    phoneNo = intent.extras?.getString("phoneNo").toString()
    binding.tvPhoneNo.text = "OTP sent to " + phoneNo

    binding.ivBack.setOnClickListener {
      finish()
    }

    binding.pinview.setOnFinishListener {
      goToNext(it)
    }


    binding.tvResendOtp.setOnClickListener {
      hideKeyboardFrom(baseContext, binding.root.rootView)
      var param = LoginRequest(phoneNo)
      callApiResendOtp(param, this)
    }

  }

  private fun startCountDown() {

    object : CountDownTimer(30000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        binding.tvRemaningSeconds.setText("00 : " + millisUntilFinished / 1000)
        // logic to set the EditText could go here
      }

      override fun onFinish() {
        binding.tvRemaningSeconds.visibility = View.INVISIBLE
        binding.tvResendOtp.visibility = View.VISIBLE

      }
    }.start()
  }

  private fun goToNext(pin: String) {
    /*if(pin.equals("1234")){
      val intent = Intent(this, PinSetupActivity::class.java)
      startActivity(intent)
    }
    else{
      showSnackBar(this,binding.mainLayout,"Enter valid OTP")
    }*/
    if (pin.isNullOrEmpty()) {
      showSnackBar(this, binding.mainLayout, "Enter valid OTP")
    } else {
      var param = OTPRequest(phoneNo, pin, "Android", "android_$phoneNo")
      callApiVerify(param, this)
    }

  }

  private fun callApiVerify(param: OTPRequest, otpActivity: OTPActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().verifyOtp(param)

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
        Log.e("response", "otpVerify: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        var dataObject = jsonObject.optJSONObject("data")
        if (statusCode == 200) {
          saveBooleanShrd(otpActivity, CommonUtils.isLogin, true)
          saveStringShrd(otpActivity, CommonUtils.accessToken, dataObject.optString("api_token"))
          var pinSetup = dataObject.optBoolean("is_pin_setup")
          var useReferal = dataObject.optBoolean("is_use_referral")
          if (pinSetup && useReferal == false) {
            val intent = Intent(otpActivity, ReferalCodeActivity::class.java)
            startActivity(intent)
            finish()
          } else if (pinSetup == false) {
            val intent = Intent(otpActivity, PinSetupActivity::class.java)
            startActivity(intent)
            finish()
          }else{
            val intent = Intent(otpActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
          }

        } else {
          showSnackBar(otpActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

  private fun callApiResendOtp(param: LoginRequest, otpActivity: OTPActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().reSendOtp(param)

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1 = response.body()?.string()
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(otpActivity, binding.mainLayout, message)
          binding.tvResendOtp.visibility = View.INVISIBLE
          binding.tvRemaningSeconds.visibility = View.VISIBLE
        } else {
          showSnackBar(otpActivity, binding.mainLayout, message)
        }
        Log.e("response", "otpResend: " + response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}