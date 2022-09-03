package com.qpay.android.rechargePrepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.activity.MainActivity
import com.qpay.android.activity.StatusActivity
import com.qpay.android.databinding.ActivityElectricityBillPayBinding
import com.qpay.android.databinding.ActivityRechargePayBinding
import com.qpay.android.electricty.ElectricityBillPayActivity
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import kotlinx.android.synthetic.main.item_history.des
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RechargePayActivity : AppCompatActivity() {
  private lateinit var binding: ActivityRechargePayBinding

  var amount: String? = ""
  var billerId: String? = ""
  var billerName: String? = ""
  var circleRefId: String? = ""
  var fetchRefId: String? = ""
  var operatorCode: String? = ""
  var planId: String? = ""
  var validity: String? = ""
  var description: String? = ""
  var mobileNumber: String? = ""


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_recharge_pay)

    amount = intent.extras?.getString("amount")
    billerId = intent.extras?.getString("billerId")
    billerName = intent.extras?.getString("billerName")
    circleRefId = intent.extras?.getString("circleRefId")
    fetchRefId = intent.extras?.getString("fetchRefId")
    operatorCode = intent.extras?.getString("operatorCode")
    planId = intent.extras?.getString("planId")
    validity= intent.extras?.getString("validity")
    description = intent.extras?.getString("description")
    mobileNumber = intent.extras?.getString("number")

    binding.tvNumber.text = mobileNumber
    binding.tvPrice.text = baseContext.resources.getString(R.string.Rs)+" "+amount
    binding.tvDescription.text = description
    binding.tvValidity.text = validity

  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      /*var param = ElectricityBillPay(
        billerBalancePay?.toInt(),
        billerIdPay,
        resources.getString(string.param_fastag), "9828520222", billerCustomerNamePay, billerRefPay,
        NewData(billerVehicleNumberPay),
      )*/
      var param = RechargePay(amount,billerId,billerName,circleRefId,mobileNumber, getStringShrd(baseContext,CommonUtils.userName),"",fetchRefId,operatorCode,planId)
      callApi(param, this)
      Log.e("status", "perfect")
    }

  }

  private fun callApi(param: RechargePay, rechargePayActivity: RechargePayActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().rechargePay(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      ), param
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
        Log.e("response", "prepaidRecharge: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(rechargePayActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + amount + " Recharge Successfully "
          val intent = Intent(rechargePayActivity, StatusActivity::class.java)
          intent.putExtra("type", "recharge")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        } else {
          showSnackBar(rechargePayActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}