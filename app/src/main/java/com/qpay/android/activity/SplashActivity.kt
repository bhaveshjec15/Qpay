package com.qpay.android.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.qpay.android.R
import com.qpay.android.databinding.ActivitySplashBinding
import com.qpay.android.utils.getBooleanShrd
import com.qpay.android.viewModel.SplashViewModel

class SplashActivity : AppCompatActivity() {

  private lateinit var binding: ActivitySplashBinding
  val viewModel: SplashViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

    /* val permissions =
       arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)*/
    val permissions =
      arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE)
    Permissions.check(
      this /*context*/,
      permissions,
      null /*rationale*/,
      null /*options*/,
      object : PermissionHandler() {
        override fun onGranted() {
          // do your task.
          goToMain()
        }
      })
  }

  private fun goToMain() {
   // throw RuntimeException("Test Crash")
    if (getBooleanShrd( "isLogin") == true) {
      Handler().postDelayed({
     val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
       /* val intent = Intent(this, PinActivity::class.java)
        startActivity(intent)
        finish()*/
      }, 2000)
    } else {
      Handler().postDelayed({
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
      }, 2000)
    }

  }

}