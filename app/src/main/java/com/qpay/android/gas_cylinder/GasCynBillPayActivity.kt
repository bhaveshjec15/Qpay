package com.qpay.android.gas_cylinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.activity.StatusActivity
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityElectricityBillPayBinding
import com.qpay.android.databinding.ActivityGasCynBillPayBinding
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.ElectricityBillPay
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

class GasCynBillPayActivity : AppCompatActivity() {
  private lateinit var binding: ActivityGasCynBillPayBinding

  var billerIdPay: String? = ""
  var billerNamePay: String? = ""
  var billerRefPay: String? = ""
  var billerCustomerNamePay: String? = ""
  var billerLabelPay: String? = ""
  var billerLogoPay: String? = ""
  var billerNumberPay: String? = ""
  var billerBalancePay: String? = ""
  var billerDueDatePay: String? = ""
  var billerStatusPay: String? = ""
  var billerRegisteredNumber: String? = ""

  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_gas_cyn_bill_pay)
    sliderDataArrayList = ArrayList()
    billerIdPay = intent.extras?.getString("billerId")
    billerNamePay = intent.extras?.getString("billerName")
    billerLabelPay = intent.extras?.getString("paramName")
    billerCustomerNamePay = intent.extras?.getString("customerName")
    billerRefPay = intent.extras?.getString("refId")
    billerLogoPay = intent.extras?.getString("logo")
    billerNumberPay = intent.extras?.getString("number")
    billerBalancePay = intent.extras?.getString("balance")
    billerDueDatePay = intent.extras?.getString("dueDate")
    billerStatusPay = intent.extras?.getString("status")
    billerRegisteredNumber = intent.extras?.getString("mobile_number")

    binding.billerName.text = billerNamePay
    binding.vehicleNumber.text = billerNumberPay
    binding.tvCustomerName.text = billerCustomerNamePay
    binding.tvAmount.text = billerBalancePay
    binding.tvDueDate.text = billerDueDatePay
    binding.tvStatus.text = billerStatusPay

    binding.ivBack.setOnClickListener { finish() }

    if(billerDueDatePay.equals("")){
      binding.layoutDueDate.visibility = View.GONE
    }

    var logo = billerLogoPay
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }

    callApiGetBanner()

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

      params.put("Mobile Number",billerRegisteredNumber!!)
      var param = ElectricityBillPay(
        billerBalancePay?.toInt(),
        billerIdPay,
        resources.getString(string.param_gas_cylinder),
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
  private fun callApi(param: ElectricityBillPay, gasCynBillPayActivity: GasCynBillPayActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().lpgGasBillPay(
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
        Log.e("response", "gasCynBillPay: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(gasCynBillPayActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.tvAmount.text.toString() + " Bill paid for Electricity"
          val intent = Intent(gasCynBillPayActivity, StatusActivity::class.java)
          intent.putExtra("type", "lpg_gas")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
        if (message.equals("__Success__", true)) {
          showSnackBar(gasCynBillPayActivity, binding.mainLayout, message)

          var msg =
            baseContext.resources.getString(R.string.Rs) + " " + binding.tvAmount.text.toString() + " Bill paid for Electricity"
          val intent = Intent(gasCynBillPayActivity, StatusActivity::class.java)
          intent.putExtra("type", "lpg_gas")
          intent.putExtra("message", msg)
          startActivity(intent)
          finish()
        }
        else {
          showSnackBar(gasCynBillPayActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}