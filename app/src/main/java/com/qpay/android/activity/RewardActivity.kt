package com.qpay.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.databinding.ActivityBankTransferBinding
import com.qpay.android.databinding.ActivityRewardBinding

class RewardActivity : AppCompatActivity() {
  private lateinit var binding: ActivityRewardBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_reward)
    binding.ivBack.setOnClickListener { finish() }
  }
}