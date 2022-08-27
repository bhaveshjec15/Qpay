package com.qpay.android.fasTag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.cashfree.pg.CFPaymentService
import com.qpay.android.R
import com.qpay.android.activity.MainActivity
import com.qpay.android.activity.StatusActivity
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityFasTagBillPayBinding
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.FasTagBillPay
import com.qpay.android.requestModel.FasTagFetchBillRequest
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.VehicleData
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import com.smarteist.autoimageslider.SliderAnimations.SIMPLETRANSFORMATION
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FasTagBillPayActivity : AppCompatActivity() {
  private lateinit var binding: ActivityFasTagBillPayBinding
  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null

  var billerIdPay: String? = ""
  var billerNamePay: String? = ""
  var billerRefPay: String? = ""
  var billerMinAmtPay: String? = ""
  var billerMaxAmtPay: String? = ""
  var billerCustomerNamePay: String? = ""
  var billerLabelPay: String? = ""
  var billerLogoPay: String? = ""
  var billerVehicleNumberPay: String? = ""
  var billerBalancePay: String? = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_fas_tag_bill_pay)

    billerIdPay = intent.extras?.getString("billerId")
    billerNamePay = intent.extras?.getString("billerName")
    billerRefPay = intent.extras?.getString("refId")
    /* billerMinAmtPay = intent.extras?.getString("min")
     billerMaxAmtPay = intent.extras?.getString("max")*/
    billerCustomerNamePay = intent.extras?.getString("customerName")
    billerLabelPay = intent.extras?.getString("paramName")
    billerLogoPay = intent.extras?.getString("logo")
    billerVehicleNumberPay = intent.extras?.getString("vehicleNumber")
    billerBalancePay = intent.extras?.getString("balance")



    binding.billerName.text = billerNamePay
    binding.vehicleNumber.text = billerVehicleNumberPay
    binding.tvCustomerName.text = billerCustomerNamePay
    binding.tvBalance.text = billerBalancePay

    var logo = billerLogoPay
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }
    /*  var minAmount = billerMinAmtPay?.toFloat()
      var maxAmount = billerMaxAmtPay?.toFloat()*/

    sliderDataArrayList = ArrayList()
    callApiGetBanner()

    binding.tvOne.setOnClickListener {
      binding.etAmount.setText(binding.tvOne.text.toString().replace("+", ""))
    }
    binding.tvTwo.setOnClickListener {
      binding.etAmount.setText(binding.tvTwo.text.toString().replace("+", ""))
    }
    binding.tvThree.setOnClickListener {
      binding.etAmount.setText(binding.tvThree.text.toString().replace("+", ""))
    }

    binding.btnSubmit.setOnClickListener {
      if (binding.etAmount.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Please Enter Amount")
      } else {
        var enteredAmount = binding.etAmount.text.toString().toFloat()

        var param = FasTagBillPay(
          binding.etAmount.text.toString(),
          billerIdPay,
          resources.getString(R.string.param_fastag),
          getStringShrd(baseContext, CommonUtils.mobileNumber),
          billerCustomerNamePay,
          billerRefPay,
          VehicleData(billerVehicleNumberPay),
        )
        callApi(param, this)
        Log.e("status", "perfect")
      }
    }
  }
  private fun callApiGetBanner() {
    sliderDataArrayList?.clear()
    val apiInterface = ApiInterface.create().getBanner(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      )
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        var response1 = response.body()?.string()
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var jsonData = jsonObject.optJSONArray("data")

          for (item in 0..jsonData.length() - 1) {
            val model = SliderData()
            var jsonImg = jsonData.optJSONObject(item)
            model.imgUrl = jsonImg.optString("image_url")

            sliderDataArrayList!!.add(model)

            // after adding data to our array list we are passing
            // that array list inside our adapter class.
            adapterBanner = SliderNewAdapter(baseContext, sliderDataArrayList)

            // belows line is for setting adapter
            // to our slider view
            binding.slider.setSliderAdapter(adapterBanner!!)

            // below line is for setting animation to our slider.
            binding.slider.setSliderTransformAnimation(SIMPLETRANSFORMATION)

            // below line is for setting auto cycle duration.
            binding.slider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH)

            // below line is for setting
            // scroll time animation
            binding.slider.setScrollTimeInSec(3)

            // below line is for setting auto
            // cycle animation to our slider
            binding.slider.setAutoCycle(true)


            adapterBanner?.notifyDataSetChanged()
          }
        }
        Log.e("res", response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

      }
    })
  }
  private fun callApi(param: FasTagBillPay, fasTagBillPayActivity: FasTagBillPayActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().fasTagBillPay(
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
        Log.e("response", "fasTagBillPay: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(fasTagBillPayActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " Recharge done on FasTag"
          val intent = Intent(fasTagBillPayActivity, StatusActivity::class.java)
          intent.putExtra("type", "fastag")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
        else if (message.equals("__Success__", true)) {
          showSnackBar(fasTagBillPayActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " Recharge done on FasTag"
          val intent = Intent(fasTagBillPayActivity, StatusActivity::class.java)
          intent.putExtra("type", "fastag")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
        else {
          showSnackBar(fasTagBillPayActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}