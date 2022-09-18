package com.qpay.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityAddMoneyBinding
import com.qpay.android.databinding.ActivityStatusBinding

class StatusActivity : AppCompatActivity() {
  private lateinit var binding: ActivityStatusBinding



  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_status)

    var type  = intent.extras?.getString("type")

    if(type.equals("addMoney")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("sendMoney")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("fastag")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("electricity")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("postpaid")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("recharge")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("dth")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }
    else if(type.equals("lpg_gas")){
      binding.tvMessage.text = intent.extras?.getString("message")
    }

    binding.btnGoToHome.setOnClickListener {
      val intent = Intent(baseContext, MainActivity::class.java)
      startActivity(intent)
    }
  }

  override fun onResume() {
    super.onResume()


  }
}