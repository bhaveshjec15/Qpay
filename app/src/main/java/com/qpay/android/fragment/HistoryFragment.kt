package com.qpay.android.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.adapter.ContactListAdapter
import com.qpay.android.adapter.HistoryListAdapter
import com.qpay.android.databinding.FragmentHistoryBinding
import com.qpay.android.databinding.FragmentHomeBinding
import com.qpay.android.model.HistoryModel
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.ContactsModel
import com.qpay.android.utils.getStringShrd
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

  private lateinit var binding: FragmentHistoryBinding
  var list: ArrayList<HistoryModel> = ArrayList()
  var adapter: HistoryListAdapter? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_history, container, false
    )
    // binding.viewmodel = vm//attach your viewModel to xml
    return binding.root
  }

  companion object {
    fun newInstance() = HistoryFragment()
  }

  override fun onResume() {
    super.onResume()
    callApiGetHistory()

  }

  private fun callApiGetHistory() {
    binding.progress.visibility = View.VISIBLE
    list.clear()
    /*val apiInterface = ApiInterface.create().getHistory(
      getStringShrd(
        requireActivity(),
        CommonUtils.accessToken
      ), getStringShrd(requireActivity(),CommonUtils.userId)
    )*/
    val apiInterface = ApiInterface.create().getHistory(
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
        binding.progress.visibility = View.GONE
        Log.e("response", "getHistory: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObject = jsonObject.optJSONObject("data")
          var dataArray = dataObject.optJSONArray("data")
          var ss = dataArray.length()-1
          for (i in 0..ss) {
            var model = HistoryModel()
            var obj = dataArray.getJSONObject(i)
            model.created_at = obj.optString("created_at")
            model.category = obj.optString("category")
            model.remark = obj.optString("remark")
            model.transaction_id = obj.optString("transaction_id")
            model.amount = obj.optInt("amount")
            list.add(model)
          }
          if(list.size>0){
            if(isAdded){
              adapter = HistoryListAdapter(requireActivity(), list)
              binding.rvHistoryList.adapter = adapter
              adapter?.notifyDataSetChanged()
            }

          }
        } else {
          //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

}