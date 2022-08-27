package com.qpay.android.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.activity.RedeemActivity
import com.qpay.android.activity.ReferActivity
import com.qpay.android.databinding.FragmentHomeBinding
import com.qpay.android.databinding.FragmentReqardBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.saveStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReqardFragment : Fragment() {
  private lateinit var binding: FragmentReqardBinding
  var totalPoints: String? = ""
  var totalAmount: String = ""

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_reqard, container, false
    )
    // binding.viewmodel = vm//attach your viewModel to xml
    return binding.root
  }

  override fun onResume() {
    super.onResume()

    callApiGetPoints()
    binding.btnRedeem.setOnClickListener {
      val intent = Intent(requireActivity(), RedeemActivity::class.java)
      intent.putExtra("totalPoints",totalPoints)
      startActivity(intent)
    }

    binding.layoutRefer.setOnClickListener {
      val intent = Intent(requireActivity(), ReferActivity::class.java)
      startActivity(intent)
    }
  }

  private fun callApiGetPoints() {
    val apiInterface = ApiInterface.create().getReferalPoints(
      getStringShrd(
        requireActivity(),
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
          showSnackBar(requireActivity(), binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }

}