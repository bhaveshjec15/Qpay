package com.qpay.android.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel

class SplashViewModel  : ViewModel() {

  fun goToMain() {

  }

  fun goToLogin() {
    /* var intent = Intent(this, MainActivity::class.java)
     startActivity(intent)*/
    Log.e("status","Splash")
  }
}