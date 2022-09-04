package com.qpay.android.fasTag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityFasTagBinding
import com.qpay.android.databinding.ActivityFasTagDetailsBinding
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.FasTagBillerRequest
import com.qpay.android.requestModel.FasTagFetchBillRequest
import com.qpay.android.requestModel.LoginRequest
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
import java.util.Objects

class FasTagDetailsActivity : AppCompatActivity() {
  private lateinit var binding: ActivityFasTagDetailsBinding
  var billerId: String? = ""
  var billerName: String? = ""
  var billerLabel: String? = ""
  var billerLogo: String? = ""

  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_fas_tag_details)
    billerId = intent.extras?.getString("billerId")
    billerName = intent.extras?.getString("billerName")
    billerLabel = intent.extras?.getString("paramName")
    billerLogo = intent.extras?.getString("logo")

    sliderDataArrayList = ArrayList()
    callApiGetBanner()
    if (billerLabel != null) {
      binding.tvLabel.text = billerLabel
      binding.etVehicleNumber.hint = billerLabel
    }

    binding.tvBillerName.text = "For $billerName"

    var logo = billerLogo
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      if (binding.etVehicleNumber.text.toString().isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Please Enter Valid Vehicle Number")
      } else {
        /* var param = FasTagFetchBillRequest(billerId!!,resources.getString(R.string.param_fastag),
           "9828520222", VehiclData(binding.etVehicleNumber.text.toString())
         )*/
        var paramsMain: HashMap<String, String> = HashMap()
        paramsMain.put(billerLabel!!, binding.etVehicleNumber.text.toString())

        var param = FasTagFetchBillRequest(
          billerId!!, resources.getString(R.string.param_fastag),
          getStringShrd(baseContext, CommonUtils.mobileNumber), paramsMain
        )

        callApi(param, this)
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
          val intent = Intent(detailsActivity, FasTagBillPayActivity::class.java)
          intent.putExtra("billerId", billerId)
          intent.putExtra("billerName", billerName)
          intent.putExtra("paramName", billerLabel)
          intent.putExtra("customerName", dataObj.optString("accountHolderName"))
          intent.putExtra("refId", dataObj.optString("refId"))
          intent.putExtra("logo", billerLogo)
          intent.putExtra("vehicleNumber", binding.etVehicleNumber.text.toString())
          intent.putExtra("balance", availableBalance)
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