package com.qpay.android.fasTag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityFasTagBinding
import com.qpay.android.databinding.ActivityFasTagDetailsBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.FasTagBillerRequest
import com.qpay.android.requestModel.FasTagFetchBillRequest
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.VehiclData
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

class FasTagDetailsActivity : AppCompatActivity() {
  private lateinit var binding: ActivityFasTagDetailsBinding
  var billerId: String? = ""
  var billerName: String? = ""
  var billerLabel: String? = ""
  var billerLogo: String? = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_fas_tag_details)
    billerId = intent.extras?.getString("billerId")
    billerName = intent.extras?.getString("billerName")
    billerLabel = intent.extras?.getString("paramName")
    billerLogo = intent.extras?.getString("logo")
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
        showSnackBar(this, binding.mainLayout, "Please Enter Valid Vehicle Number")
      }
      else{
        var param = FasTagFetchBillRequest(billerId!!,resources.getString(R.string.param_fastag),
         getStringShrd(baseContext,CommonUtils.mobileNumber), VehiclData(binding.etVehicleNumber.text.toString())
        )
       /* var param = FasTagFetchBillRequest(billerId!!,resources.getString(R.string.param_fastag),
          "9828520222", VehiclData(binding.etVehicleNumber.text.toString())
        )*/
        callApi(param, this)
      }
    }


  }

  private fun callApi(param: FasTagFetchBillRequest, detailsActivity: FasTagDetailsActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().getFasTagBill(
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
        Log.e("response", "fasTagDetail: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObj = jsonObject.optJSONObject("data")
          var additionalParams = dataObj.optJSONObject("additionalParams")

          val intent = Intent(detailsActivity, FasTagBillPayActivity::class.java)
          intent.putExtra("billerId", billerId)
          intent.putExtra("billerName", billerName)
          intent.putExtra("paramName", billerLabel)
          intent.putExtra("customerName", dataObj.optString("accountHolderName"))
          intent.putExtra("refId",dataObj.optString("refId"))
        /*  intent.putExtra("min",additionalParams.optString("Minimum Amount for Top-up"))
          intent.putExtra("max",additionalParams.optString("Maximum Permissible Recharge Amount"))*/
          intent.putExtra("logo",billerLogo)
          intent.putExtra("vehicleNumber",binding.etVehicleNumber.text.toString())
          intent.putExtra("balance",additionalParams.optString("Available Balance"))
          startActivity(intent)
        } else {
          showSnackBar(detailsActivity, binding.mainLayout, message)
        }


      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}