package com.qpay.android.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityAddBankBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.AddBankRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBankActivity : AppCompatActivity() {
  private lateinit var binding: ActivityAddBankBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bank)
  }

  override fun onResume() {
    super.onResume()



    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      if (binding.etName.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Name")
      }
      else if (binding.etEmail.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Email Address")
      }
      else if (binding.etPhone.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Mobile Number")
      }
      else if (binding.etAccountNumber.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Bank Account Number")
      }
      else if (binding.etifsc.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter IFSC Code")
      }
      else if (binding.etAddress.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Address")
      }
      else if (binding.etCity.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter City Name")
      }
      else if (binding.etState.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter State Name")
      }
      else if (binding.etPinode.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter PinCode")
      }
      else {
        var param = AddBankRequest(binding.etName.text.toString(),binding.etEmail.text.toString(),binding.etPhone.text.toString(),binding.etAccountNumber.text.toString(),binding.etifsc.text.toString(),binding.etAddress.text.toString()
        ,binding.etCity.text.toString(),binding.etState.text.toString(),binding.etPinode.text.toString())
        callApi(param, this)
      }
    }
  }

  private fun callApi(param: AddBankRequest, addBankActivity: AddBankActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().addBank(
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
          showSnackBar(addBankActivity, binding.mainLayout, message)
          Handler().postDelayed(Runnable { finish() }, 2000)

        } else {
          showSnackBar(addBankActivity, binding.mainLayout, message)
        }
        Log.e("response", "pinsetup: "+response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}