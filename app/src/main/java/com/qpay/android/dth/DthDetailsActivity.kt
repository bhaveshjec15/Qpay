package com.qpay.android.dth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonArray
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.activity.StatusActivity
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityDthDetailsBinding
import com.qpay.android.databinding.ActivityFasTagDetailsBinding
import com.qpay.android.fasTag.FasTagBillPayActivity
import com.qpay.android.fasTag.FasTagDetailsActivity
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.FasTagBillPay
import com.qpay.android.requestModel.FasTagFetchBillRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DthDetailsActivity : AppCompatActivity() {
  private lateinit var binding: ActivityDthDetailsBinding
  var billerId: String? = ""
  var billerName: String? = ""
  var billerLabel: String? = ""
  var billerLogo: String? = ""
  var fetchOption: String? = ""
  var billerRefPay: String? = ""

  var clickStatus: String = ""

  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_dth_details)

    billerId = intent.extras?.getString("billerId")
    billerName = intent.extras?.getString("billerName")
    billerLabel = intent.extras?.getString("paramName")
    billerLogo = intent.extras?.getString("logo")
    fetchOption = intent.extras?.getString("fetchOption")

    binding.ivBack.setOnClickListener { finish() }

    binding.tvBillerName.text = "For $billerName"

    var logo = billerLogo
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }

    Log.e("fetchOption_DthDetails",fetchOption!!)

    createAllView()

    if (billerName.equals("Sun Direct TV", true)) {
      binding.layoutAmount.visibility = View.GONE
    } else {
      if (fetchOption.equals("MANDATORY", true)) {

      } else {
        binding.layoutAmount.visibility = View.VISIBLE
      }
    }


    binding.btnSubmit.setOnClickListener {
      if (clickStatus.equals("one")) {
        if (binding.etOne.text.toString().isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid data")
        } else {
          clickSubmit()
        }
      } else if (clickStatus.equals("two")) {
        if (binding.etOne.text.toString().isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid data")
        } else if (binding.etTwo.text.toString().isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid data")
        } else {
          clickSubmit()
        }
      } else if (clickStatus.equals("three")) {
        if (binding.etOne.text.toString().isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid data")
        } else if (binding.etTwo.text.toString().isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid data")
        } else if (binding.etThree.text.toString().isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid data")
        } else {
          clickSubmit()
        }

      }
    }

  }

  private fun clickSubmit() {
    if (fetchOption.equals("MANDATORY", true)) {
      var jsonArray = JSONArray(billerLabel)
      var paramsMain: HashMap<String, String> = HashMap()

      if (clickStatus.equals("one"))
        paramsMain.put(
          jsonArray.optJSONObject(0).optString("paramName"),
          binding.etOne.text.toString()
        )
      else if (clickStatus.equals("two")){
        paramsMain.put(
          jsonArray.optJSONObject(0).optString("paramName"),
          binding.etOne.text.toString()
        )
        paramsMain.put(
          jsonArray.optJSONObject(1).optString("paramName"),
          binding.etTwo.text.toString()
        )
      }
      else if (clickStatus.equals("three")){
        paramsMain.put(
          jsonArray.optJSONObject(0).optString("paramName"),
          binding.etOne.text.toString()
        )
        paramsMain.put(
          jsonArray.optJSONObject(1).optString("paramName"),
          binding.etTwo.text.toString()
        )
        paramsMain.put(
          jsonArray.optJSONObject(2).optString("paramName"),
          binding.etTwo.text.toString()
        )
      }

      var param = DthFetchBillRequest(
        billerId!!, resources.getString(string.param_dth),
        getStringShrd(baseContext, CommonUtils.mobileNumber), paramsMain
      )

      callApiFetch(param, this)
    } else {
      var jsonArray = JSONArray(billerLabel)

      var paramsMain: HashMap<String, String> = HashMap()
      if (clickStatus.equals("one"))
        paramsMain.put(
          jsonArray.optJSONObject(0).optString("paramName"),
          binding.etOne.text.toString()
        )
      else if (clickStatus.equals("two")){
        paramsMain.put(
          jsonArray.optJSONObject(0).optString("paramName"),
          binding.etOne.text.toString()
        )
        paramsMain.put(
          jsonArray.optJSONObject(1).optString("paramName"),
          binding.etTwo.text.toString()
        )
      }
      else if (clickStatus.equals("three")){
        paramsMain.put(
          jsonArray.optJSONObject(0).optString("paramName"),
          binding.etOne.text.toString()
        )
        paramsMain.put(
          jsonArray.optJSONObject(1).optString("paramName"),
          binding.etTwo.text.toString()
        )
        paramsMain.put(
          jsonArray.optJSONObject(2).optString("paramName"),
          binding.etTwo.text.toString()
        )
      }



      billerRefPay = ""

      lateinit var param: dthBillPay
      if(billerName.equals("Sun Direct TV")){
         param = dthBillPay(
          binding.etTwo.text.toString(),
          billerId,
          resources.getString(string.param_dth),
          getStringShrd(baseContext, CommonUtils.mobileNumber),
          getStringShrd(baseContext, CommonUtils.userName) ?: "Testing",
          billerRefPay, true,
          paramsMain,
        )
      }
      else{
         param = dthBillPay(
          binding.etAmount.text.toString(),
          billerId,
          resources.getString(string.param_dth),
          getStringShrd(baseContext, CommonUtils.mobileNumber),
          getStringShrd(baseContext, CommonUtils.userName) ?: "Testing",
          billerRefPay, true,
          paramsMain,
        )
      }

      callApiBillPay(param, this)
    }

  }

  private fun createAllView() {
    var jsonArray = JSONArray(billerLabel)
    if (jsonArray != null) {
      var totalViews = jsonArray.length()

      if (totalViews == 1) {
        binding.layoutOne.visibility = View.VISIBLE
        clickStatus = "one"

        var jsonObject = jsonArray.optJSONObject(0)
        binding.tvOne.setText(jsonObject.optString("paramName"))
        binding.etOne.setHint(jsonObject.optString("paramName"))

      } else if (totalViews == 2) {
        binding.layoutOne.visibility = View.VISIBLE
        binding.layoutTwo.visibility = View.VISIBLE
        clickStatus = "two"

        var jsonObjectOne = jsonArray.optJSONObject(0)
        binding.tvOne.setText(jsonObjectOne.optString("paramName"))
        binding.etOne.setHint(jsonObjectOne.optString("paramName"))

        var jsonObjectTwo = jsonArray.optJSONObject(1)
        binding.tvTwo.setText(jsonObjectTwo.optString("paramName"))
        binding.etTwo.setHint(jsonObjectTwo.optString("paramName"))

      } else if (totalViews == 3) {
        binding.layoutOne.visibility = View.VISIBLE
        binding.layoutTwo.visibility = View.VISIBLE
        binding.layoutThree.visibility = View.VISIBLE
        clickStatus = "three"

        var jsonObjectOne = jsonArray.optJSONObject(0)
        binding.tvOne.setText(jsonObjectOne.optString("paramName"))
        binding.etOne.setHint(jsonObjectOne.optString("paramName"))

        var jsonObjectTwo = jsonArray.optJSONObject(1)
        binding.tvTwo.setText(jsonObjectTwo.optString("paramName"))
        binding.etTwo.setHint(jsonObjectTwo.optString("paramName"))

        var jsonObjectThree = jsonArray.optJSONObject(2)
        binding.tvThree.setText(jsonObjectThree.optString("paramName"))
        binding.etThree.setHint(jsonObjectThree.optString("paramName"))
      }

    }

  }

  private fun callApiBillPay(param: dthBillPay, dthDetailsActivity: DthDetailsActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().dthBillPay(
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
        Log.e("response", "dthBillPay: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(dthDetailsActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " Recharge done on DTH"
          val intent = Intent(dthDetailsActivity, StatusActivity::class.java)
          intent.putExtra("type", "dth")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
        else if (message.equals("__Success__", true)) {
          showSnackBar(dthDetailsActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " Recharge done on FasTag"
          val intent = Intent(dthDetailsActivity, StatusActivity::class.java)
          intent.putExtra("type", "dth")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
        else {
          showSnackBar(dthDetailsActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

  private fun callApiFetch(param: DthFetchBillRequest, dthDetailsActivity: DthDetailsActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().getDthBill(
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
        Log.e("response", "dthBillDetail: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObj = jsonObject.optJSONObject("data")
          var additionalParams = dataObj.optJSONObject("additionalParams")
          var availableBalance: String? = ""
          if (additionalParams != null) {

            var iterator: Iterator<String> = additionalParams.keys();
            while (iterator.hasNext()) {
              var key = iterator.next()
              if (key.toString().contains("Balance")) {
                Log.i("TAG", "key:" + key + "--Value::" + additionalParams.optString(key))
                availableBalance = additionalParams.optString(key)
                break
              }
            }
          }
          val intent = Intent(dthDetailsActivity, DthPayActivity::class.java)
          intent.putExtra("billerId", billerId)
          intent.putExtra("billerName", billerName)
          intent.putExtra("paramName", billerLabel)
          intent.putExtra("customerName", dataObj.optString("accountHolderName"))
          intent.putExtra("refId",dataObj.optString("refId"))
          intent.putExtra("logo",billerLogo)
          intent.putExtra("vehicleNumber","")
          intent.putExtra("balance",dataObj.optInt("amount").toString())
          intent.putExtra("status",dataObj.optString("status"))
          intent.putExtra("dueDate",dataObj.optString("dueDate"))
          startActivity(intent)
        } else {
          showSnackBar(dthDetailsActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}