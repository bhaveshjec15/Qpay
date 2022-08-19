package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.qpay.android.R
import com.qpay.android.databinding.ActivitySendAmountBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.SendAmountRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendAmountActivity : AppCompatActivity() {
  private lateinit var binding: ActivitySendAmountBinding
  val userBalance: MutableLiveData<Int?> = MutableLiveData(0)
  var selectedUserName: String? = ""
  var selectedUserNumber: String? = ""
  var type: String? = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_send_amount)
    type = intent.extras?.getString("type")

    if (type.equals("phone", true)) {
      binding.tvName.visibility = View.GONE
      binding.etPhone.visibility = View.VISIBLE
    } else if (type.equals("scan", true)) {
      binding.tvName.visibility = View.GONE
      binding.etPhone.visibility = View.VISIBLE
      binding.etPhone.setText(intent.extras?.getString("number"))
    } else {
      binding.tvName.visibility = View.VISIBLE
      binding.etPhone.visibility = View.GONE
      selectedUserName = intent.extras?.getString("user_name")
      selectedUserNumber = intent.extras?.getString("user_number")
    }

    binding.tvName.text = selectedUserName
  }

  override fun onResume() {
    super.onResume()
    callApiGetBalance()

    binding.ivBack.setOnClickListener {
      finish()
    }
    binding.btnSubmit.setOnClickListener {
      if (type.equals("Phone", true)) {
        if (binding.etPhone.text.isNullOrEmpty() || binding.etPhone.text.length < 10 || binding.etPhone.text.length > 10) {
          showSnackBar(this, binding.mainLayout, "Enter valid phone number")
        } else if (binding.etAmount.text.isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else if (userBalance.value?.toInt()!! < (binding.etAmount.text.toString().toInt())) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else {
          var param = SendAmountRequest(
            binding.etPhone.text.toString(),
            binding.etAmount.text.toString().toInt(),
            binding.etRemark.text.toString()
          )
          // Log.e("aamm",selectedUserNumber.toString()?.substring(3))
          callApiSendAmount(param, this)
        }
      } else if (type.equals("scan", true)) {
        if (binding.etPhone.text.isNullOrEmpty() || binding.etPhone.text.length < 10 || binding.etPhone.text.length > 10) {
          showSnackBar(this, binding.mainLayout, "Enter valid phone number")
        } else if (binding.etAmount.text.isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else if (userBalance.value?.toInt()!! < (binding.etAmount.text.toString().toInt())) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else {
          var param = SendAmountRequest(
            binding.etPhone.text.toString(),
            binding.etAmount.text.toString().toInt(),
            binding.etRemark.text.toString()
          )
          // Log.e("aamm",selectedUserNumber.toString()?.substring(3))
          callApiSendAmount(param, this)
        }
      } else {
        if (binding.etAmount.text.isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else if (userBalance.value?.toInt()!! < (binding.etAmount.text.toString().toInt())) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else {
          var param = SendAmountRequest(
            selectedUserNumber.toString()?.substring(3),
            binding.etAmount.text.toString().toInt(),
            binding.etRemark.text.toString()
          )
          // Log.e("aamm",selectedUserNumber.toString()?.substring(3))
          callApiSendAmount(param, this)
        }

      }
    }

    userBalance.observe(this, Observer {
      binding.currentBalance.text = it.toString()
    })
  }

  private fun callApiGetBalance() {
    val apiInterface = ApiInterface.create().getProfile(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      )
    )
    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        var response1: String? = null
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        Log.e("response", "getProfile: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObject = jsonObject.optJSONObject("data")
          var amount = dataObject.optInt("walletAmount")
          userBalance.value = amount

        } else {
          //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }

  private fun callApiSendAmount(
    param: SendAmountRequest,
    sendAmountActivity: SendAmountActivity
  ) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().amountSend(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      ), param
    )
    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        var response1: String? = null
        binding.progress.visibility = View.GONE
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        Log.e("response", "sendAmount: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(sendAmountActivity, binding.mainLayout, message)
          callApiGetBalance()
          var msg =""

          if(type.equals("phone",true) || type.equals("scan",true)){
            msg =  baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " " +
                "Sent to"+binding.etPhone.text.toString()
          }else{
            msg =  baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " " +
                "Sent to "+ selectedUserNumber.toString()?.substring(3)
          }
          val intent = Intent(baseContext, StatusActivity::class.java)
          intent.putExtra("type", "sendMoney")
          intent.putExtra("message", msg)
          startActivity(intent)
        } else {
          showSnackBar(sendAmountActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}