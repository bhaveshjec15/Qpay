package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityBankTransferBinding
import com.qpay.android.databinding.ActivityRewardBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RewardActivity : AppCompatActivity() {
  private lateinit var binding: ActivityRewardBinding
  var totalPoints: String? = ""
  var totalAmount: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_reward)
    binding.ivBack.setOnClickListener { finish() }
  }

  override fun onResume() {
    super.onResume()

    callApiGetPoints()

    binding.btnRedeem.setOnClickListener {
      val intent = Intent(this, RedeemActivity::class.java)
      intent.putExtra("totalPoints",totalPoints)
      startActivity(intent)
    }

    binding.layoutRefer.setOnClickListener {
      val intent = Intent(this, ReferActivity::class.java)
      startActivity(intent)
    }
  }

  private fun callApiGetPoints() {
    val apiInterface = ApiInterface.create().getReferalPoints(
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
        Log.e("response", "getPoints: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObject = jsonObject.optJSONObject("data")
          try {
            if(dataObject !=null){
              var totalPoints1 = dataObject.optJSONObject("totalRwdPts").toString()
              var totalAmount1 = dataObject.optJSONObject("balanceRwdPts").toString()
              totalPoints = totalPoints1.replace("{", "").replace("}", "").replace("\"", "").split(":")[1]
              totalAmount = totalAmount1.replace("{", "").replace("}", "").replace("\"", "").split(":")[1]
              binding.tvPoints.text = totalPoints + " Points"
            }else{
              binding.tvPoints.text = "0" + " Points"
            }
          }catch (e: Exception){
            binding.tvPoints.text = "0" + " Points"
          }
        } else {
          showSnackBar(baseContext, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }
}