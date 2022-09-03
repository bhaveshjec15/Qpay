package com.qpay.android.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cashfree.pg.CFPaymentService
import com.qpay.android.R
import com.qpay.android.databinding.ActivityAddMoneyBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.AddMoneyRequest
import com.qpay.android.requestModel.TokenRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMoneyActivity : AppCompatActivity() {
  private lateinit var binding: ActivityAddMoneyBinding
  val userBalance: MutableLiveData<Int?> = MutableLiveData(0)
  var transStatus: String = ""
  var transReferenceId: String = ""
  var name: String=""
  var email: String =""
  var phone: String =""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_add_money)

    binding.ivBack.setOnClickListener {
      finish()
    }

    callApiGetBalance()
    binding.tvOne.setOnClickListener {
      binding.etAmount.setText(binding.tvOne.text.toString().replace("+", ""))
    }
    binding.tvTwo.setOnClickListener {
      binding.etAmount.setText(binding.tvTwo.text.toString().replace("+", ""))
    }
    binding.tvThree.setOnClickListener {
      binding.etAmount.setText(binding.tvThree.text.toString().replace("+", ""))
    }
    binding.btnSubmit.setOnClickListener {
      hideKeyboardFrom(baseContext, binding.root.rootView)

      if (binding.etAmount.text.isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter valid amount")
      } else {
        // var transId = System.currentTimeMillis() / 1000
        /* var param = AddMoneyRequest(
           "CREDIT",
           binding.etAmount.text.toString().toInt(),
           "Testing",
           transId.toString()
         )
         callApiForAddMoney(param, this)*/
        var params = TokenRequest(binding.etAmount.text.toString().toInt())
        callApiGetToken(params, this)
      }

    }

    userBalance.observe(this, Observer {
      binding.currentBalance.text = it.toString()
    })
  }

  override fun onResume() {
    super.onResume()


  }

  private fun callApiGetBalance() {
    val apiInterface = ApiInterface.create().getProfile(
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
        Log.e("response", "getProfile: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObject = jsonObject.optJSONObject("data")
          var amount = dataObject.optInt("walletAmount")
          name = dataObject.optString("first_name")
          email = dataObject.optString("email")
          phone = dataObject.optString("mobile_number")
          userBalance.value = amount

        } else {
          //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }

  private fun callApiGetToken(params: TokenRequest, addMoneyActivity: AddMoneyActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().getToken(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      ), params
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
        Log.e("response", "getToken: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObject = jsonObject.optJSONObject("data")
          var token = dataObject.optString("token")
          var orderId = dataObject.optString("orderId")
          /* val intent = Intent(addMoneyActivity, DropCheckoutActivity::class.java)
           intent.putExtra("token", token)
           intent.putExtra("orderId", orderId)
           startActivity(intent)*/

          initialPayment(token, orderId)
        } else {
          showSnackBar(addMoneyActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

  private fun initialPayment(token: String, orderId: String) {

    if(name.equals("")) name = "Testing" else name
    if(email.equals("")) email =  "t1@gmail.com" else email
    if(phone.equals("")) phone = "9988776655" else phone

    var params: HashMap<String, String> = HashMap()
   /* params.put(CFPaymentService.PARAM_APP_ID, "16086004d480e1cdbeeab54d19068061")*/
    params.put(CFPaymentService.PARAM_APP_ID, "209459b30a11a72f9914a28a4c954902")
    params.put(CFPaymentService.PARAM_ORDER_ID, orderId)
    params.put(CFPaymentService.PARAM_ORDER_AMOUNT, binding.etAmount.text.toString())
    params.put(CFPaymentService.PARAM_CUSTOMER_NAME, name)
    params.put(CFPaymentService.PARAM_CUSTOMER_EMAIL, email)
    params.put(CFPaymentService.PARAM_CUSTOMER_PHONE, phone)

    /*CFPaymentService.getCFPaymentServiceInstance().doPayment(this, params, token, "TEST")*/
    CFPaymentService.getCFPaymentServiceInstance().doPayment(this, params, token, "PROD")

  }

  private fun callApiForAddMoney(param: AddMoneyRequest, addMoneyActivity: AddMoneyActivity) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().addMoney(
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
        Log.e("response", "addmoney: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          callApiGetBalance()
          var msg = baseContext.resources.getString(R.string.Rs)+" "+binding.etAmount.text.toString()+" "+"Added Successfully into your wallet"
          val intent = Intent(baseContext, StatusActivity::class.java)
          intent.putExtra("type","addMoney")
          intent.putExtra("message",msg)
          startActivity(intent)
         // showSnackBar(addMoneyActivity, binding.mainLayout, message)

        } else {
          showSnackBar(addMoneyActivity, binding.mainLayout, message)
        }


      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    //Same request code for all payment APIs.
    Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE)
    Log.d(TAG, "API Response : ")
    //Prints all extras. Replace with app logic.
    if (requestCode == CFPaymentService.REQ_CODE && data != null)
      if (data != null) {
        val bundle = data.extras
        if (bundle != null) for (key in bundle.keySet()) {
          if (bundle.getString(key) != null) {
            Log.d(TAG, key + " : " + bundle.getString(key))
            if(key.equals("referenceId")){
              transReferenceId = bundle.getString(key)!!
            }else if (key.equals("txStatus")){
              transStatus = bundle.getString(key)!!
            }

            if(transStatus.equals("SUCCESS") && transReferenceId !=null){
              var param = AddMoneyRequest(
                "CREDIT",
                binding.etAmount.text.toString().toInt(),
                "Testing",
                transReferenceId
              )
              callApiForAddMoney(param, this)
            }


          }
        }
      }
  }
}