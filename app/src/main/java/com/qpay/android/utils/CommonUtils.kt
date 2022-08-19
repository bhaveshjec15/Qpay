package com.qpay.android.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import com.kishandonga.csbx.CustomSnackbar
import com.qpay.android.R

class CommonUtils {

  companion object {
    var isLogin = "isLogin"
    var accessToken = "accessToken"
    var userId = "userId"
    var mobileNumber = "mobileNumber"
    var userName = "userName"
    var kycStatus = "kycStatus"

// Electricity
    var paramInputKey = "paramInputKey"



    fun getRealPathFromURI(
      context: Context,
      contentUri: Uri
    ): String? {
      var cursor: Cursor? = null
      try {
        return if ("content" == contentUri.scheme) {
          val proj =
            arrayOf(MediaStore.Images.Media.DATA)
          cursor = context.contentResolver.query(contentUri, proj, null, null, null)
          val column_index =
            cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
          cursor.moveToFirst()
          cursor.getString(column_index)
        } else {
          contentUri.path
        }
      } catch (e: java.lang.Exception) {
        cursor?.close()
        e.printStackTrace()
      }
      return null
    }
  }

  var loginStatus = ""

}