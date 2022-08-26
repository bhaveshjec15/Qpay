package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.qpay.android.R
import com.qpay.android.databinding.ActivityPinBinding
import com.qpay.android.databinding.ActivityPinSetupBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.saveStringShrd
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinActivity : AppCompatActivity() {
  private lateinit var binding: ActivityPinBinding
  val pinData: MutableLiveData<String?> = MutableLiveData("")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_pin)
  }

  override fun onResume() {
    super.onResume()

    callApiGetPin()

    binding.createPinView.setOnFinishListener {
     if(it.toString().isNotEmpty()){
       val intent = Intent(this, MainActivity::class.java)
       startActivity(intent)
       finish()
     }
    }
  }

  private fun callApiGetPin() {
    val apiInterface = ApiInterface.create().getPin(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      )
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        var response1: String? = null
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        Log.e("response", "getPin: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObject = jsonObject.optJSONObject("data")
          var pin = dataObject.optString("pin")
          pinData.value = pin
        } else {
          //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }
}