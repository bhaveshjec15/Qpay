package com.qpay.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.qpay.android.R
import com.qpay.android.databinding.ActivitySendAmountBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.requestModel.SendAmountRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SendAmountActivity : AppCompatActivity() {
  private lateinit var binding: ActivitySendAmountBinding
  val userBalance: MutableLiveData<Int?> = MutableLiveData(0)
  var selectedUserName: String? = ""
  var selectedUserNumber: String? = ""
  var type: String? = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_send_amount)
    type = intent.extras?.getString("type")

    if (type.equals("phone", true)) {
      binding.tvName.visibility = View.GONE
      binding.etPhone.visibility = View.VISIBLE
    } else if (type.equals("scan", true)) {
      binding.tvName.visibility = View.GONE
      binding.etPhone.visibility = View.VISIBLE
      binding.etPhone.setText(intent.extras?.getString("number"))
    } else {
      binding.tvName.visibility = View.VISIBLE
      binding.etPhone.visibility = View.GONE
      selectedUserName = intent.extras?.getString("user_name")
      selectedUserNumber = intent.extras?.getString("user_number")
    }

    binding.tvName.text = selectedUserName
  }

  override fun onResume() {
    super.onResume()
    callApiGetBalance()

    binding.ivBack.setOnClickListener {
      finish()
    }
    binding.btnSubmit.setOnClickListener {
      if (type.equals("Phone", true)) {
        if (binding.etPhone.text.isNullOrEmpty() || binding.etPhone.text.length < 10 || binding.etPhone.text.length > 10) {
          showSnackBar(this, binding.mainLayout, "Enter valid phone number")
        } else if (binding.etAmount.text.isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else if (userBalance.value?.toInt()!! < (binding.etAmount.text.toString().toInt())) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else {
          var param = SendAmountRequest(
            binding.etPhone.text.toString(),
            binding.etAmount.text.toString().toInt(),
            binding.etRemark.text.toString()
          )
          // Log.e("aamm",selectedUserNumber.toString()?.substring(3))
          callApiSendAmount(param, this)
        }
      }
      else if (type.equals("scan", true)) {
        if (binding.etPhone.text.isNullOrEmpty() || binding.etPhone.text.length < 10 || binding.etPhone.text.length > 10) {
          showSnackBar(this, binding.mainLayout, "Enter valid phone number")
        } else if (binding.etAmount.text.isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else if (userBalance.value?.toInt()!! < (binding.etAmount.text.toString().toInt())) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else {
          var param = SendAmountRequest(
            binding.etPhone.text.toString(),
            binding.etAmount.text.toString().toInt(),
            binding.etRemark.text.toString()
          )
          // Log.e("aamm",selectedUserNumber.toString()?.substring(3))
          callApiSendAmount(param, this)
        }
      }
      else {
        if (binding.etAmount.text.isNullOrEmpty()) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else if (userBalance.value?.toInt()!! < (binding.etAmount.text.toString().toInt())) {
          showSnackBar(this, binding.mainLayout, "Enter valid amount")
        } else {
          var number = PhoneNumberWithoutCountryCode(selectedUserNumber!!)!!.replace(" ","")
          Log.e("number",number!!)
          var param = SendAmountRequest(
            number!!,
            binding.etAmount.text.toString().toInt(),
            binding.etRemark.text.toString()
          )
          callApiSendAmount(param, this)

        }

      }
    }

    userBalance.observe(this, Observer {
      binding.currentBalance.text = it.toString()
    })
  }
  fun PhoneNumberWithoutCountryCode(phoneNumberWithCountryCode: String): String? { //+91 7698989898
    val compile: Pattern = Pattern.compile(
      "\\+(?:998|996|995|994|993|992|977|976|975|974|973|972|971|970|968|967|966|965|964|963|962|961|960|886|880|856|855|853|852|850|692|691|690|689|688|687|686|685|683|682|681|680|679|678|677|676|675|674|673|672|670|599|598|597|595|593|592|591|590|509|508|507|506|505|504|503|502|501|500|423|421|420|389|387|386|385|383|382|381|380|379|378|377|376|375|374|373|372|371|370|359|358|357|356|355|354|353|352|351|350|299|298|297|291|290|269|268|267|266|265|264|263|262|261|260|258|257|256|255|254|253|252|251|250|249|248|246|245|244|243|242|241|240|239|238|237|236|235|234|233|232|231|230|229|228|227|226|225|224|223|222|221|220|218|216|213|212|211|98|95|94|93|92|91|90|86|84|82|81|66|65|64|63|62|61|60|58|57|56|55|54|53|52|51|49|48|47|46|45|44\\D?1624|44\\D?1534|44\\D?1481|44|43|41|40|39|36|34|33|32|31|30|27|20|7|1\\D?939|1\\D?876|1\\D?869|1\\D?868|1\\D?849|1\\D?829|1\\D?809|1\\D?787|1\\D?784|1\\D?767|1\\D?758|1\\D?721|1\\D?684|1\\D?671|1\\D?670|1\\D?664|1\\D?649|1\\D?473|1\\D?441|1\\D?345|1\\D?340|1\\D?284|1\\D?268|1\\D?264|1\\D?246|1\\D?242|1)\\D?"
    )
    //Log.e(tag, "number::_>" +  number);//OutPut::7698989898
    return phoneNumberWithCountryCode.replace(compile.pattern().toRegex(), "")
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

  private fun callApiSendAmount(
    param: SendAmountRequest,
    sendAmountActivity: SendAmountActivity
  ) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().amountSend(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      ), param
    )
    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        var response1: String? = null
        binding.progress.visibility = View.GONE
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        Log.e("response", "sendAmount: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(sendAmountActivity, binding.mainLayout, message)
          callApiGetBalance()
          var msg =""

          if(type.equals("phone",true) || type.equals("scan",true)){
            msg =  baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " " +
                "Sent to"+binding.etPhone.text.toString()
          }else{
            msg =  baseContext.resources.getString(R.string.Rs) + " " + binding.etAmount.text.toString() + " " +
                "Sent to "+ selectedUserNumber.toString()?.substring(3)
          }
          val intent = Intent(baseContext, StatusActivity::class.java)
          intent.putExtra("type", "sendMoney")
          intent.putExtra("message", msg)
          startActivity(intent)
        } else {
          showSnackBar(sendAmountActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}