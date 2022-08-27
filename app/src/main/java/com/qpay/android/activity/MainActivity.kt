package com.qpay.android.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.qpay.android.R
import com.qpay.android.fragment.BankFragment
import com.qpay.android.fragment.HistoryFragment
import com.qpay.android.fragment.HomeFragment
import com.qpay.android.fragment.ReqardFragment
import com.qpay.android.fragment.SettingFragment
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.saveStringShrd
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

  val userName: MutableLiveData<String?> = MutableLiveData("")
  val userImage: MutableLiveData<String?> = MutableLiveData("")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //val toolbar = getSupportActionBar();
    callApiGetProfile()
    bottom_navigation.selectedItemId = R.id.nav_home

    //tvUserName?.setTextColor(baseContext.resources.getColor(R.color.colorBtn))

    loadFragment(HomeFragment.newInstance())



    bottom_navigation.setOnItemSelectedListener { item ->
      var fragment: Fragment
      when (item.itemId) {
        R.id.nav_home -> {
          //tvUserName?.setText("Hi... Same")
        //  tvUserName?.setTextColor(baseContext.resources.getColor(R.color.colorBtn))
          fragment = HomeFragment()
          loadFragment(fragment)
          true
        }
        R.id.nav_history -> {
          // tv_toolbar_header?.setText("History")
          //  tv_toolbar_header?.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
          fragment = HistoryFragment()
          loadFragment(fragment)
          true

        }
        R.id.nav_bank -> {
          //  tv_toolbar_header?.setText("Bank")
          // tv_toolbar_header?.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
          fragment = BankFragment()
          loadFragment(fragment)
          true

        }
        R.id.nav_rewards -> {
          //  tv_toolbar_header?.setText("Rewards")
          //  tv_toolbar_header?.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
          fragment = ReqardFragment()
          loadFragment(fragment)
          true

        }
        R.id.nav_settings -> {
          //  tv_toolbar_header?.setText("Setting")
          // tv_toolbar_header?.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
          fragment = SettingFragment()
          loadFragment(fragment)
          true

        }
        else -> false
      }

    }

    bottom_navigation.setOnItemReselectedListener { item ->
      when (item.itemId) {
        R.id.nav_home -> {
          Toast.makeText(this, "Home Item reselected", Toast.LENGTH_SHORT).show()
        }
        R.id.nav_history -> {
          Toast.makeText(this, "Radio Item reselected", Toast.LENGTH_SHORT).show()
        }
        R.id.nav_rewards -> {
          Toast.makeText(this, "Search Item reselected", Toast.LENGTH_SHORT).show()
        }
        R.id.nav_bank -> {
          Toast.makeText(this, "My Music Item reselected", Toast.LENGTH_SHORT).show()
        }
        R.id.nav_settings -> {
          Toast.makeText(this, "Library Item reselected", Toast.LENGTH_SHORT).show()

        }

      }
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
          userName.value = dataObject.optString("first_name")
          userImage.value = dataObject.optString("image_url")
          saveStringShrd(baseContext,CommonUtils.userId,dataObject.optString("_id"))
          saveStringShrd(baseContext,CommonUtils.mobileNumber, dataObject.optString("mobile_number"))
          saveStringShrd(baseContext,CommonUtils.userName, dataObject.optString("first_name"))
          saveStringShrd(baseContext,CommonUtils.kycStatus, dataObject.optString("kyc_status"))
        } else {
          //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        // binding.progress.visibility = View.GONE
      }
    })
  }

  override fun onResume() {
    super.onResume()

    profile_image.setOnClickListener {
      val intent = Intent(this, MyAccountActivity::class.java)
      startActivity(intent)
    }

    userName.observe(this, Observer {
      if (it.isNullOrEmpty()) {
       // tvUserName.text = "Hi... Same"
        profile_image.setImageResource(R.drawable.ic_profile)
      } else {
      //  tvUserName.text = "Hi... $it"
        Picasso.get().load(it).into(profile_image)
      }
    })

    userImage.observe(this, Observer {
      if (it.isNullOrEmpty()) {
        profile_image.setImageResource(R.drawable.ic_profile)
      } else {
        Picasso.get().load(it).into(profile_image)
      }
    })

  }

  private fun loadFragment(fragment: Fragment) {
    // load fragment
    supportFragmentManager.beginTransaction()
      .replace(R.id.fragment_container, fragment)
      .commit()
  }

}