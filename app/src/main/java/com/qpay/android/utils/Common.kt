package com.qpay.android.utils

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kishandonga.csbx.CustomSnackbar
import com.qpay.android.QPayApplication
import com.qpay.android.R

fun showSnackBar(context: Context, view: View, msg: String) {
  /*
  https://github.com/kishandonga/custom-snackbar
  CustomSnackbar(this, root).show {
     textTypeface(BOLD | BOLD_ITALIC | ITALIC | NORMAL | CUSTOM)
     actionTypeface(BOLD | BOLD_ITALIC | ITALIC | NORMAL | CUSTOM)
     textColor(...)
     backgroundColor(...)
     border(..., ...)
     cornerRadius(...)
     padding(...)
     duration(LENGTH_INDEFINITE | LENGTH_LONG | LENGTH_SHORT)
     actionTextColor(...)
     message(...)
     withAction(android.R.string.ok) {
       it.dismiss()
     }
   }*/
  CustomSnackbar(context, view).show {
    textColor(context.resources.getColor(R.color.colorWhite))
    backgroundColor(context.resources.getColor(R.color.colorEditTxtHeading))
    padding(5)
    message(msg)
    withAction(android.R.string.ok) {
      it.dismiss()
    }
  }
}

fun hideKeyboardFrom(context: Context, view: View) {
  val imm: InputMethodManager =
    context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun saveStringShrd(context: Context, strKey: String, strValue: String){
  val editor: Editor? = QPayApplication.sharedpreferences?.edit()
  editor?.putString(strKey,strValue)
  editor?.commit()
}

fun saveBooleanShrd(context: Context, strKey: String, strValue: Boolean){
  val editor: Editor? = QPayApplication.sharedpreferences?.edit()
  editor?.putBoolean(strKey,strValue)
  editor?.commit()
}

fun getStringShrd(context: Context, strKey: String): String{
  var data = QPayApplication.sharedpreferences?.getString(strKey,"")
  return data.toString()
}

fun getBooleanShrd(strKey: String): Boolean? {
  var data = QPayApplication.sharedpreferences?.getBoolean(strKey,false)
  return data!!
}