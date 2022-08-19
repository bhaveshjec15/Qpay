package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.droidbyme.dialoglib.DroidDialog
import com.qpay.android.R
import com.qpay.android.databinding.ActivityBankTransferBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.AddBankRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankTransferActivity : AppCompatActivity() {

  private lateinit var binding: ActivityBankTransferBinding
  var name: String = ""
  var email: String = ""
  var phone: String = ""
  var bankAccount: String = ""
  var ifsc: String = ""
  var address: String = ""
  var city: String = ""
  var state: String = ""
  var pinCode: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_bank_transfer)


  }

  override fun onResume() {
    super.onResume()
    callApiGetBank(this)

    binding.layoutBankItem.setOnClickListener {
      val intent = Intent(this, BankAmountTransferActivity::class.java)
      startActivity(intent)
    }

    binding.ivBack.setOnClickListener { finish() }

    binding.btnSubmit.setOnClickListener {
      val intent = Intent(this, AddBankActivity::class.java)
      startActivity(intent)

    }

    binding.btnDelete.setOnClickListener { deleteBank() }

  }

  private fun callApiGetBank(bankTransferActivity: BankTransferActivity) {
    val apiInterface = ApiInterface.create().getSavedBank(
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

    DroidDialog.Builder(this)
      .title("Delete Bank!")
      .content("Are you sure you want to delete?")
      .cancelable(true, true)
      .positiveButton(
        "Yes"
      ) {
        it.dismiss()
        callApiDelete(this)
      }
      .negativeButton("No", DroidDialog.onNegativeListener {
        it.cancel()
      })

      .show()
  }

  private fun callApiDelete(bankTransferActivity: BankTransferActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().deleteBank(
      getStringShrd(baseContext,
        CommonUtils.accessToken))

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
          showSnackBar(bankTransferActivity, binding.mainLayout, message)
          callApiGetBank(bankTransferActivity)

        } else {
          showSnackBar(bankTransferActivity, binding.mainLayout, message)
        }
        Log.e("response", "pinsetup: "+response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}