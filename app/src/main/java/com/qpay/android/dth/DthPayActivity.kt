package com.qpay.android.dth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityDthPayBinding
import com.qpay.android.databinding.ActivityFasTagBillPayBinding
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import com.smarteist.autoimageslider.SliderAnimations.SIMPLETRANSFORMATION
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DthPayActivity : AppCompatActivity() {
  private lateinit var binding: ActivityDthPayBinding
  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null
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


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_dth_pay)

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

    sliderDataArrayList = ArrayList()
    callApiGetBanner()

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      showSnackBar(this,binding.mainLayout,"Under Process")
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
}