package com.qpay.android.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.droidbyme.dialoglib.DroidDialog
import com.qpay.android.R
import com.qpay.android.activity.AddBankActivity
import com.qpay.android.activity.BankAmountTransferActivity
import com.qpay.android.activity.BankTransferActivity
import com.qpay.android.databinding.FragmentBankBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankFragment : Fragment() {
  private lateinit var binding: FragmentBankBinding
  var name: String = ""
  var email: String = ""
  var phone: String = ""
  var bankAccount: String = ""
  var ifsc: String = ""
  var address: String = ""
  var city: String = ""
  var state: String = ""
  var pinCode: String = ""


 var mContext: Context? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater,
      R.layout.fragment_bank, container, false)
    // binding.viewmodel = vm//attach your viewModel to xml
    mContext = requireContext()
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    callApiGetBank()

    binding.layoutBankItem.setOnClickListener {
      val intent = Intent(mContext, BankAmountTransferActivity::class.java)
      startActivity(intent)
    }

    binding.btnSubmit.setOnClickListener {
      val intent = Intent(mContext, AddBankActivity::class.java)
      startActivity(intent)

    }

    binding.btnDelete.setOnClickListener { deleteBank() }

  }

  private fun callApiGetBank() {
    val apiInterface = ApiInterface.create().getSavedBank(
      getStringShrd(
        mContext!!,
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
        Log.e("response", "getSavedBank: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          binding.layoutAddBank.visibility = View.GONE
          binding.layoutBankItem.visibility = View.VISIBLE
          binding.btnDelete.visibility = View.VISIBLE
          var dataObject = jsonObject.optJSONObject("data")

          binding.tvBankName.text = dataObject.optString("bankAccount")
          binding.tvCode.text = dataObject.optString("ifsc")
          binding.tvCity.text = dataObject.optString("city")
          binding.tvState.text = dataObject.optString("state")

          name=dataObject.optString("name")
          email=dataObject.optString("email")
          phone=dataObject.optString("phone")
          bankAccount=dataObject.optString("bankAccount")
          ifsc=dataObject.optString("ifsc")
          address=dataObject.optString("address1")
          city=dataObject.optString("city")
          state=dataObject.optString("state")
          pinCode=dataObject.optString("pincode")

        }
        else {
          binding.layoutAddBank.visibility = View.VISIBLE
          binding.layoutBankItem.visibility = View.GONE
          binding.btnDelete.visibility = View.GONE
          // showSnackBar(bankTransferActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }

  private fun deleteBank() {

    DroidDialog.Builder(mContext)
      .title("Delete Bank!")
      .content("Are you sure you want to delete?")
      .cancelable(true, true)
      .positiveButton(
        "Yes"
      ) {
        it.dismiss()
        callApiDelete()
      }
      .negativeButton("No", DroidDialog.onNegativeListener {
        it.cancel()
      })

      .show()
  }

  private fun callApiDelete() {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().deleteBank(
      getStringShrd(mContext!!,
        CommonUtils.accessToken)
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1: String? = null
        if(response.body()== null){
          response1 = response.errorBody()?.string()
        }else{
          response1 = response.body()?.string()
        }
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode==200) {
          showSnackBar(mContext!!, binding.mainLayout, message)
          callApiGetBank()

        } else {
          showSnackBar(mContext!!, binding.mainLayout, message)
        }
        Log.e("response", "pinsetup: "+response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

}