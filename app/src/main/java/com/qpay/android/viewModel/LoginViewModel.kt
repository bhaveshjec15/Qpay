package com.qpay.android.viewModel

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.kishandonga.csbx.CustomSnackbar
import com.qpay.android.QPayApplication
import com.qpay.android.R
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.CurrentContext
import com.qpay.android.utils.showSnackBar
import com.qpay.android.utils.zip

class LoginViewModel : ViewModel() {

  var isSubmitButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
  var phoneNumber: MutableLiveData<String> = MutableLiveData("")

  init {

  }

  fun afterTextChanged(s: Editable?) {
    if (s.isNullOrBlank()) {
      return
    } else {
      phoneNumber.value = s.toString()
    }
  }

  fun onClickSubmit(v: View) {
  }

}