package com.qpay.android.rechargePostPaid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.databinding.ActivityElectricityInputBinding
import com.qpay.android.databinding.ActivityPostPaidInputBinding
import com.qpay.android.electricty.ElectricityBillPayActivity
import com.qpay.android.electricty.ElectricityInputActivity
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.ElectricityFetchBillRequest
import com.qpay.android.requestModel.NumberData
import com.qpay.android.requestModel.PostPaidData
import com.qpay.android.requestModel.PostPaidFetchBillRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostPaidInputActivity : AppCompatActivity() {
  private lateinit var binding: ActivityPostPaidInputBinding
  var billerId: String? = ""
  var billerName: String? = ""
  var billerLabel: String? = ""
  var billerLogo: String? = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_post_paid_input)

    billerId = intent.extras?.getString("billerId")
    billerName = intent.extras?.getString("billerName")
    billerLabel = intent.extras?.getString("paramName")
    billerLogo = intent.extras?.getString("logo")

    binding.etVehicleNumber.hint = billerLabel
  }

  override fun onResume() {
    super.onResume()

    binding.tvLabel.text = billerLabel
    binding.tvBillerName.text = "For $billerName"

    var logo = billerLogo
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      if(binding.etVehicleNumber.text.toString().isNullOrEmpty()){
        showSnackBar(this, binding.mainLayout, "Please Enter Mobile Number")
      }
      else{
        var param = PostPaidFetchBillRequest(billerId!!,resources.getString(string.param_postpaid),
          getStringShrd(baseContext, CommonUtils.mobileNumber), PostPaidData(binding.etVehicleNumber.text.toString())
        )
      /*   var param = PostPaidFetchBillRequest(billerId!!,resources.getString(string.param_postpaid),
           "8302693321", PostPaidData(binding.etVehicleNumber.text.toString())
         )*/
        callApi(param, this)
      }
    }
  }

  private fun callApi(param: PostPaidFetchBillRequest, postPaidInputActivity: PostPaidInputActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().getPostPaidBill(
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
        Log.e("response", "postpaidBill: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObj = jsonObject.optJSONObject("data")
          var additionalParams = dataObj.optJSONObject("additionalParams")

          val intent = Intent(postPaidInputActivity, PostPaidBillPayActivity::class.java)
          intent.putExtra("billerId", billerId)
          intent.putExtra("billerName", billerName)
          intent.putExtra("paramName", billerLabel)
          intent.putExtra("customerName", dataObj.optString("accountHolderName"))
          intent.putExtra("refId",dataObj.optString("refId"))
          intent.putExtra("logo",billerLogo)
          intent.putExtra("vehicleNumber",binding.etVehicleNumber.text.toString())
          intent.putExtra("balance",dataObj.optInt("amount").toString())
          intent.putExtra("status",dataObj.optString("status"))
          intent.putExtra("dueDate",dataObj.optString("dueDate"))
          startActivity(intent)
        }
        else if (statusCode == 499){

        }
        else {
          showSnackBar(postPaidInputActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}