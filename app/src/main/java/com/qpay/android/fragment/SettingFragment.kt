package com.qpay.android.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.activity.StaticPageActivity
import com.qpay.android.databinding.FragmentHistoryBinding
import com.qpay.android.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
  private lateinit var binding: FragmentSettingBinding


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_setting, container, false
    )
    // binding.viewmodel = vm//attach your viewModel to xml
    return binding.root
  }

  override fun onResume() {
    super.onResume()


    binding.layoutAbout.setOnClickListener {
      val intent = Intent(requireActivity(), StaticPageActivity::class.java)
      intent.putExtra("type", "about")
      startActivity(intent)
    }

    binding.layoutPrivacyPolicy.setOnClickListener {
      val intent = Intent(requireActivity(), StaticPageActivity::class.java)
      intent.putExtra("type", "privacy")
      startActivity(intent)

    }

    binding.layoutTerms.setOnClickListener {
      val intent = Intent(requireActivity(), StaticPageActivity::class.java)
      intent.putExtra("type", "terms")
      startActivity(intent)
    }
  }

}