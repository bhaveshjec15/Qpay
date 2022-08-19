package com.qpay.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityStaticPageBinding
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import com.smarteist.autoimageslider.SliderAnimations.SIMPLETRANSFORMATION
import com.smarteist.autoimageslider.SliderView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaticPageActivity : AppCompatActivity() {

  private lateinit var binding: ActivityStaticPageBinding
  var pageType: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_static_page)
  }

  override fun onResume() {
    super.onResume()
    pageType = intent.extras?.getString("type").toString()
    binding.ivBack.setOnClickListener { finish() }

    if(pageType.equals("about")){
      binding.title.text = "About Us"
      callApiGetAboutUs(this)
    }
    else if( pageType.equals("privacy")){
      binding.title.text = "Privacy policy"
      callApiGetAboutUs(this)
    }
    else if(pageType.equals("terms")){
      binding.title.text = "Terms &Conditions"
      callApiGetTerms(this)
    }


  }

  private fun callApiGetAboutUs(staticPageActivity: StaticPageActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().getAboutUs(
      getStringShrd(
        staticPageActivity,
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
        binding.progress.visibility = View.GONE
        if (statusCode == 200) {
          var jsonData = jsonObject.optJSONObject("data")

          var content  = jsonData.optString("content")
          binding.tvContent.setText(Html.fromHtml(content))
        }else{
          showSnackBar(staticPageActivity, binding.mainLayout, message)
        }
        Log.e("res", response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

      }
    })
  }

  private fun callApiGetTerms(staticPageActivity: StaticPageActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().getTerms(
      getStringShrd(
        staticPageActivity,
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
        binding.progress.visibility = View.GONE
        if (statusCode == 200) {
          var jsonData = jsonObject.optJSONObject("data")

          var content  = jsonData.optString("content")
          binding.tvContent.setText(Html.fromHtml(content))
        }else{
          showSnackBar(staticPageActivity, binding.mainLayout, message)
        }
        Log.e("res", response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

      }
    })
  }
}