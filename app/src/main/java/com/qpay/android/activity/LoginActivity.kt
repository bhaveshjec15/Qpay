package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityLoginBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.saveBooleanShrd
import com.qpay.android.utils.saveStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
  private lateinit var binding: ActivityLoginBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
  }

  override fun onResume() {
    super.onResume()
    binding.btnSubmit.setOnClickListener {
      if (binding.edtPhoneNo.text.toString().length < 10) {
        showSnackBar(this, binding.mainLayout, "Please Enter Valid Mobile Number")
      } else {
        var param = LoginRequest(binding.edtPhoneNo.text.toString())
        callApi(param, this)
        /* val intent = Intent(this, OTPActivity::class.java)
         intent.putExtra("phoneNo",binding.edtPhoneNo.text.toString())
         startActivity(intent)*/
      }
    }
  }

  private fun callApi(param: LoginRequest, loginActivity: LoginActivity) {

    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().login(param)

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
          val intent = Intent(loginActivity, OTPActivity::class.java)
          intent.putExtra("phoneNo",binding.edtPhoneNo.text.toString())
          startActivity(intent)
        } else {
          showSnackBar(loginActivity, binding.mainLayout, message)
        }
        Log.e("res", response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}