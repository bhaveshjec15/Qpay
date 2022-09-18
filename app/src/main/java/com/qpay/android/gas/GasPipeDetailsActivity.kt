package com.qpay.android.gas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityGasCynDetailsBinding
import com.qpay.android.databinding.ActivityGasPipeDetailsBinding
import com.qpay.android.gas_cylinder.GasCynFetchBillRequest
import com.qpay.android.gas_cylinder.customParam.CustomParamAdapter
import com.qpay.android.gas_cylinder.customParam.CustomParamListModel
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import com.smarteist.autoimageslider.SliderAnimations.SIMPLETRANSFORMATION
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GasPipeDetailsActivity : AppCompatActivity() {
  private lateinit var binding: ActivityGasPipeDetailsBinding
  var billerId: String? = ""
  var billerName: String? = ""
  var billerLabel: String? = ""
  var billerLogo: String? = ""
  var fetchOption: String? = ""
  var billerRefPay: String? = ""

  var clickStatus: String = ""
  var list: ArrayList<CustomParamListModel> = ArrayList()
  var adapter: CustomParamAdapter? = null
  var adapterBanner: SliderNewAdapter? = null
  private var sliderDataArrayList: ArrayList<SliderData>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_gas_pipe_details)
    sliderDataArrayList = ArrayList()

    billerId = intent.extras?.getString("billerId")
    billerName = intent.extras?.getString("billerName")
    billerLabel = intent.extras?.getString("paramName")
    billerLogo = intent.extras?.getString("logo")
    fetchOption = intent.extras?.getString("fetchOption")
    Log.e("fetch", fetchOption.toString())
    Log.e("count", billerLabel.toString())

    binding.ivBack.setOnClickListener { finish() }

    if (fetchOption.equals("MANDATORY", true)) {
      binding.layoutAmount.visibility = View.GONE
    } else {
      binding.layoutAmount.visibility = View.VISIBLE
    }

    binding.tvBillerName.text = "For $billerName"

    var logo = billerLogo
    if (logo == null || logo.equals("null")) {
      binding.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(binding.logo)
    }

    createAllView()
    callApiGetBanner()

    binding.btnSubmit.setOnClickListener {

      if (fetchOption.equals("MANDATORY", true)) {

        var paramsMain: HashMap<String, String> = HashMap()

        if(clickStatus.equals("1")){
          paramsMain.put(list[0].paramName, binding.etAmountOne.text.toString())
        }else if (clickStatus.equals("2")){
          paramsMain.put(list[0].paramName, binding.etAmountOne.text.toString())
          paramsMain.put(list[1].paramName, binding.etAmountTwo.text.toString())
        }
        else if (clickStatus.equals("3")){
          paramsMain.put(list[0].paramName, binding.etAmountOne.text.toString())
          paramsMain.put(list[1].paramName, binding.etAmountTwo.text.toString())
          paramsMain.put(list[2].paramName, binding.etAmountThree.text.toString())
        }
        else if (clickStatus.equals("4")){
          paramsMain.put(list[0].paramName, binding.etAmountOne.text.toString())
          paramsMain.put(list[1].paramName, binding.etAmountTwo.text.toString())
          paramsMain.put(list[2].paramName, binding.etAmountThree.text.toString())
          paramsMain.put(list[3].paramName, binding.etAmountFour.text.toString())
        }
        else if (clickStatus.equals("5")){
          paramsMain.put(list[0].paramName, binding.etAmountOne.text.toString())
          paramsMain.put(list[1].paramName, binding.etAmountTwo.text.toString())
          paramsMain.put(list[2].paramName, binding.etAmountThree.text.toString())
          paramsMain.put(list[3].paramName, binding.etAmountFour.text.toString())
          paramsMain.put(list[4].paramName, binding.etAmountFive.text.toString())
        }

        var param = GasCynFetchBillRequest(billerId!!,resources.getString(string.param_gas_cylinder),
          getStringShrd(baseContext, CommonUtils.mobileNumber), paramsMain)

        callApiFetchBill(param, this)
      } else {

      }
    }

  }

  private fun callApiFetchBill(param: GasCynFetchBillRequest, gasPipeDetailsActivity: GasPipeDetailsActivity) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().getGasCynBill(
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
        Log.e("response", "gasCynFetchBill: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObj = jsonObject.optJSONObject("data")
          var additionalParams = dataObj.optJSONObject("additionalParams")

          /*val intent = Intent(electricityInputActivity, ElectricityBillPayActivity::class.java)
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
          startActivity(intent)*/
        }
        else {
          showSnackBar(gasPipeDetailsActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
  private fun createAllView() {
    var jsonArray = JSONArray(billerLabel)

    list.clear()
    if (jsonArray.length() > 0) {
      var ss = jsonArray.length() - 1
      for (i in 0..ss) {
        var model = CustomParamListModel()
        var jsonObject = jsonArray.optJSONObject(i)
        model.paramName = jsonObject.optString("paramName")
        list.add(model)
      }
      clickStatus = list.size.toString()
      if (list.size > 0) {
        if (list.size == 1) {
          binding.layoutOne.visibility = View.VISIBLE
          binding.etAmountOne.hint = list[0].paramName
        } else if (list.size == 2) {
          binding.layoutOne.visibility = View.VISIBLE
          binding.etAmountOne.hint = list[0].paramName

          binding.layoutTwo.visibility = View.VISIBLE
          binding.etAmountTwo.hint = list[1].paramName
        }
        else if (list.size == 3) {
          binding.layoutOne.visibility = View.VISIBLE
          binding.etAmountOne.hint = list[0].paramName

          binding.layoutTwo.visibility = View.VISIBLE
          binding.etAmountTwo.hint = list[1].paramName

          binding.layoutThree.visibility = View.VISIBLE
          binding.etAmountThree.hint = list[2].paramName
        }
        else if (list.size == 4) {
          binding.layoutOne.visibility = View.VISIBLE
          binding.etAmountOne.hint = list[0].paramName

          binding.layoutTwo.visibility = View.VISIBLE
          binding.etAmountTwo.hint = list[1].paramName

          binding.layoutThree.visibility = View.VISIBLE
          binding.etAmountThree.hint = list[2].paramName

          binding.layoutFour.visibility = View.VISIBLE
          binding.etAmountFour.hint = list[3].paramName

        }
        else if (list.size == 5) {
          binding.layoutOne.visibility = View.VISIBLE
          binding.etAmountOne.hint = list[0].paramName

          binding.layoutTwo.visibility = View.VISIBLE
          binding.etAmountTwo.hint = list[1].paramName

          binding.layoutThree.visibility = View.VISIBLE
          binding.etAmountThree.hint = list[2].paramName

          binding.layoutFour.visibility = View.VISIBLE
          binding.etAmountFour.hint = list[3].paramName

          binding.layoutFive.visibility = View.VISIBLE
          binding.etAmountFive.hint = list[4].paramName
        }
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
}