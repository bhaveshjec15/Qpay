package com.qpay.android.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore.Images.Media
import android.util.Log
import android.view.View
import androidmads.library.qrgenearator.QRGContents.Type
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.droidbyme.dialoglib.DroidDialog
import com.google.zxing.WriterException
import com.qpay.android.R
import com.qpay.android.databinding.ActivityMyAccountBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.saveBooleanShrd
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMyAccountBinding
  val userBalance: MutableLiveData<Int?> = MutableLiveData(0)
  val userImage: MutableLiveData<String?> = MutableLiveData("")
  val userPhone: MutableLiveData<String?> = MutableLiveData("")
  val kycStatus: MutableLiveData<String?> = MutableLiveData("")
  var ivQrCode: Bitmap? = null
  private var mLastClickTime: Long = 0
  var qrgEncoder: QRGEncoder? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_my_account)


    binding.ivBack.setOnClickListener {
      finish()
    }
  }

  override fun onResume() {
    super.onResume()
    generateQRCode()

    callApiGetProfile()
    callApiGetBalance()

    kycStatus.observe(this, Observer {
      if (it.toString().isNotEmpty()) {
        if (kycStatus.value.toString().equals("ACCEPTED")) {
          binding.ivKycStatus.visibility = View.VISIBLE
        } else {
          binding.ivKycStatus.visibility = View.GONE
        }
      }
    })
    userBalance.observe(this, Observer {
      binding.currentBalance.text = it.toString()
    })

    binding.layoutKyc.setOnClickListener {
      if (kycStatus.value.equals("ACCEPTED")) {

      } else {
        val intent = Intent(this, kycActivity::class.java)
        startActivity(intent)
      }

    }
    binding.layoutLogout.setOnClickListener {
      logout()
    }

    binding.layoutUpdate.setOnClickListener {
      val intent = Intent(this, EditProfileActivity::class.java)
      startActivity(intent)
    }

    userPhone.observe(this, Observer {
      generateQRCode()
    })

    binding.layoutAddMoney.setOnClickListener {
      val intent = Intent(this, AddMoneyActivity::class.java)
      startActivity(intent)
    }

    binding.layoutAbout.setOnClickListener {
      val intent = Intent(this, StaticPageActivity::class.java)
      intent.putExtra("type","about")
      startActivity(intent)
    }

    binding.ivShare.setOnClickListener {

      if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        return@setOnClickListener
      }
      mLastClickTime = SystemClock.elapsedRealtime();
      val path: String = Media.insertImage(contentResolver, ivQrCode, "QR Code", null)
      val uri = Uri.parse(path)

      val intent = Intent(Intent.ACTION_SEND)
      intent.type = "image/jpeg"
      intent.putExtra(Intent.EXTRA_STREAM, uri)
      startActivity(Intent.createChooser(intent, "Share Image"))
    }
  }

  private fun logout() {

    DroidDialog.Builder(this)
      .title("Logout!")
      .content("Are you sure you want to logout?")
      .cancelable(true, true)
      .positiveButton(
        "Yes"
      ) {
        saveBooleanShrd(baseContext, CommonUtils.isLogin, false)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
      }
      .negativeButton("No", DroidDialog.onNegativeListener {
        it.cancel()
      })
      /*.neutralButton(
        "DISMISS"
      ) { Toast.makeText(context, "DISMISS", Toast.LENGTH_SHORT).show() }*/
      .show()
  }

  private fun generateQRCode() {
    val qrgEncoder = QRGEncoder(userPhone.value, null, Type.TEXT, 200)
    try {
      // Getting QR-Code as Bitmap
      var bitmap = qrgEncoder.encodeAsBitmap()
      // Setting Bitmap to ImageView
      binding.ivQRCode.setImageBitmap(bitmap)
      ivQrCode = bitmap
    } catch (e: WriterException) {
      Log.v(TAG, e.toString())
    }
  }

  private fun callApiGetProfile() {
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
          binding.tvName.setText(dataObject.optString("first_name"))
          kycStatus.value = dataObject.optString("kyc_status")
          binding.tvPhone.setText("+91" + dataObject.optString("mobile_number"))
          userPhone.value = dataObject.optString("mobile_number")
          var image = dataObject.optString("image_url")
          if (image.isNullOrEmpty()) {
            binding.ivProfile.setImageResource(R.drawable.ic_profile)
          } else {
            Picasso.get().load(image).into(binding.ivProfile)
          }
          var number = dataObject.optString("mobile_number")
          binding.tvInfo.text = "Anyone can scan this QR or send money to you on $number. You will receive money in your wallet."

        } else {
          //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
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
}