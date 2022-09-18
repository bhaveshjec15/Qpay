package com.qpay.android.rechargePostPaid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.databinding.ActivityElectrictyBillerListBinding
import com.qpay.android.databinding.ActivityPostpaidBillerListBinding
import com.qpay.android.electricty.ElectricityListAdapter
import com.qpay.android.electricty.ElectrictyBillerList
import com.qpay.android.electricty.ElectrictyBillerListModel
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.FasTagBillerRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostpaidBillerList : AppCompatActivity() {

  private lateinit var binding: ActivityPostpaidBillerListBinding
  var list: ArrayList<PostPaidBillerListModel> = ArrayList()
  var listTemp: ArrayList<PostPaidBillerListModel> = ArrayList()
  var adapter: PostPaidListAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_postpaid_biller_list)
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    var param = FasTagBillerRequest(resources.getString(string.param_postpaid), 0, 0)
    callApi(param, this)

    binding.etSearch.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //yourFunction();
      }

      override fun afterTextChanged(s: Editable) {
        listTemp.clear()
        if(list.size>0){
          for (i in list.indices){
            var typeName = s.toString()
            var findName = list[i].billerName
            if(findName.startsWith(typeName, true)){
              listTemp.add(list[i])
            }
          }
          if(listTemp.size>0){
            adapter = PostPaidListAdapter(baseContext, listTemp)
            binding.rvBillerList.adapter = adapter
            adapter?.notifyDataSetChanged()
          }
        }
      }
    })

  }

  private fun callApi(param: FasTagBillerRequest, postpaidBillerList: PostpaidBillerList) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().getFasTagList(
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
        Log.e("response", "fesTagList: " + response1.toString())
        list.clear()
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataArray = jsonObject.optJSONArray("data")
          var ss = dataArray.length()-1
          for (i in 0..ss) {
            var model = PostPaidBillerListModel()
            var obj = dataArray.getJSONObject(i)

            model.billerId = obj.optString("billerId")
            model.billerName = obj.optString("billerName")
            model.logoUrl = obj.optString("billerLogoURL")
            model.fetchOption = obj.optString("fetchOption")
            var customerParamsArray = obj.optJSONArray("customerParams")
            model.paramName = customerParamsArray.getJSONObject(0).optString("paramName")
            list.add(model)
          }
          if(list.size>0){
            adapter = PostPaidListAdapter(postpaidBillerList, list)
            binding.rvBillerList.adapter = adapter
            adapter?.notifyDataSetChanged()
          }
        } else {
          showSnackBar(postpaidBillerList, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}