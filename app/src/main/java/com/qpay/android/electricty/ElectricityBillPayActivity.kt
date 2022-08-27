package com.qpay.android.electricty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.cashfree.pg.CFPaymentService
import com.google.android.gms.common.internal.service.Common
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.activity.MainActivity
import com.qpay.android.activity.StatusActivity
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityElectricityBillPayBinding
import com.qpay.android.fasTag.FasTagBillPayActivity
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.ElectricityBillPay
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

class ElectricityBillPayActivity : AppCompatActivity() {
  private lateinit var binding: ActivityElectricityBillPayBinding

  var billerIdPay: String? = ""
  var billerNamePay: String? = ""
  var billerRefPay: String? = ""
  var billerCustomerNamePay: String? = ""
  var billerLabelPay: String? = ""
  var billerLogoPay: String? = ""
  var billerVehicleNumberPay: String? = ""
  var billerBalancePay: String? = ""
  var billerDueDatePay: String? = ""
  var billerStatusPay: String? = ""

  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_electricity_bill_pay)

    billerIdPay = intent.extras?.getString("billerId")
    billerNamePay = intent.extras?.getString("billerName")
    billerLabelPay = intent.extras?.getString("paramName")
    billerCustomerNamePay = intent.extras?.getString("customerName")
    billerRefPay = intent.extras?.getString("refId")
    billerLogoPay = intent.extras?.getString("logo")
    billerVehicleNumberPay = intent.extras?.getString("vehicleNumber")
    billerBalancePay = intent.extras?.getString("balance")
    billerDueDatePay = intent.extras?.getString("dueDate")
    billerStatusPay = intent.extras?.getString("status")


    binding.billerName.text = billerNamePay
    binding.vehicleNumber.text = billerVehicleNumberPay
    binding.tvCustomerName.text = billerCustomerNamePay
    binding.tvAmount.text = billerBalancePay
    binding.tvDueDate.text = billerDueDatePay
    binding.tvStatus.text = billerStatusPay

    var logo = billerLogoPay
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }

   /* if (billerStatusPay.equals("SUCCESS", true)) {
      binding.btnSubmit.visibility = View.GONE
    } else {
      binding.btnSubmit.visibility = View.VISIBLE
    }*/
  }

  override fun onResume() {
    super.onResume()
    sliderDataArrayList = ArrayList()
    callApiGetBanner()

    binding.ivBack.setOnClickListener { finish() }

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
      /*var param = ElectricityBillPay(
        billerBalancePay?.toInt(),
        billerIdPay,
        resources.getString(string.param_fastag), "9828520222", billerCustomerNamePay, billerRefPay,
        NewData(billerVehicleNumberPay),
      )*/
      /*  var param = ElectricityBillPay(
          billerBalancePay?.toInt(),
          billerIdPay,
          resources.getString(string.param_electricity), getStringShrd(baseContext,CommonUtils.mobileNumber), billerCustomerNamePay, billerRefPay,
          NewData(billerVehicleNumberPay),
        )*/

      var params: HashMap<String, String> = HashMap()

      params.put(getStringShrd(baseContext, CommonUtils.paramInputKey), billerVehicleNumberPay!!)
      var param = ElectricityBillPay(
        billerBalancePay?.toInt(),
        billerIdPay,
        resources.getString(string.param_electricity),
        getStringShrd(baseContext, CommonUtils.mobileNumber),
        billerCustomerNamePay,
        billerRefPay,
        params,
      )
      callApi(param, this)
      Log.e("status", "perfect")
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
  private fun callApi(param: ElectricityBillPay, electricityBillPay: ElectricityBillPayActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().electricityBillPay(
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
        Log.e("response", "electricityBillPay: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(electricityBillPay, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " Bill paid for Electricity"
          val intent = Intent(electricityBillPay, StatusActivity::class.java)
          intent.putExtra("type", "electricity")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
         if (message.equals("__Success__", true)) {
           showSnackBar(electricityBillPay, binding.mainLayout, message)

           var msg =
             baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " Bill paid for Electricity"
           val intent = Intent(electricityBillPay, StatusActivity::class.java)
           intent.putExtra("type", "electricity")
           intent.putExtra("message", msg)
           startActivity(intent)
           finish()
        }
        else {
          showSnackBar(electricityBillPay, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}