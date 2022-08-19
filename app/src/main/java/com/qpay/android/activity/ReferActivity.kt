package com.qpay.android.activity

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.PhoneLookup
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.adapter.ContactListAdapter
import com.qpay.android.adapter.ReferContactListAdapter
import com.qpay.android.databinding.ActivityReferBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.ContactsModel
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream

class ReferActivity : AppCompatActivity() {
  private lateinit var binding: ActivityReferBinding
  var list: ArrayList<ContactsModel> = ArrayList()
  var listTemp: ArrayList<ContactsModel> = ArrayList()
  var adapter: ReferContactListAdapter? = null
  var referCode: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_refer)
    binding.ivBack.setOnClickListener { finish() }

    GlobalScope.launch { getContactList() }
  }

  override fun onResume() {
    super.onResume()

    callApiGetProfile()

    binding.layoutShare.setOnClickListener {
      if (referCode.isNotEmpty()) {
        val intent2: Intent = Intent()
        intent2.action = Intent.ACTION_SEND
        intent2.type = "text/plain"
        intent2.putExtra(Intent.EXTRA_TEXT, "Refer Code: $referCode")
        startActivity(Intent.createChooser(intent2, "Share via"))
      }
    }

    binding.etSearch.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //yourFunction();
      }

      override fun afterTextChanged(s: Editable) {
        listTemp.clear()
        if(list.size>0){
          for (i in list.indices){
            var typeName = s.toString()
            if(typeName.startsWith("9") || typeName.startsWith("8")|| typeName.startsWith("7")||typeName.startsWith("6")){
              var findName = list[i].number
              if(findName.startsWith(typeName, true)){
                listTemp.add(list[i])
              }
            }else{
              var findName = list[i].name
              if(findName.startsWith(typeName, true)){
                listTemp.add(list[i])
              }
            }
          }
          if(listTemp.size>0){
            adapter = ReferContactListAdapter(baseContext, listTemp)
            binding.rvContactList.adapter = adapter
            adapter?.notifyDataSetChanged()
          }
        }
      }
    })

  }

  @SuppressLint("Range")
  suspend fun getContactList() {
    list.clear()
    showSnackBar(this, binding.mainLayout, "Loading Contacts...Please wait few seconds")
    val cr = contentResolver
    val cur: Cursor? = cr.query(
      Contacts.CONTENT_URI,
      null, null, null, null
    )
    if ((if (cur != null) cur.getCount() else 0) > 0) {
      while (cur != null && cur.moveToNext()) {
        val id: String = cur.getString(cur.getColumnIndex(Contacts._ID))
        val name: String = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME))
        if (cur.getInt(
            cur.getColumnIndex(Contacts.HAS_PHONE_NUMBER)
          ) > 0
        ) {
          val pCur: Cursor? = cr.query(
            Phone.CONTENT_URI,
            null,
            Phone.CONTACT_ID + " = ?", arrayOf(id), null
          )
          while (pCur?.moveToNext() == true) {
            val phoneNo: String = pCur?.getString(
              pCur?.getColumnIndex(
                Phone.NUMBER
              )
            )
            var model = ContactsModel()
            model.name = name
            model.number = phoneNo
            model.img = retrieveContactPhoto(baseContext, phoneNo)
            list.add(model)
            //  Log.i(TAG, "Name: $name")
            //  Log.i(TAG, "Phone Number: $phoneNo")
          }
          pCur?.close()
        }
      }
    }
    if (cur != null) {
      cur.close()
    }
    runOnUiThread {
      if (list.size > 0) {
        adapter = ReferContactListAdapter(this, list)
        binding.rvContactList.adapter = adapter
        adapter?.notifyDataSetChanged()
      }
    }

  }

  fun retrieveContactPhoto(context: Context, number: String?): Bitmap? {
    val contentResolver: ContentResolver = context.getContentResolver()
    var contactId: String? = null
    val uri: Uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
    val projection = arrayOf(PhoneLookup.DISPLAY_NAME, PhoneLookup._ID)
    val cursor = contentResolver.query(
      uri,
      projection,
      null,
      null,
      null
    )
    if (cursor != null) {
      while (cursor.moveToNext()) {
        contactId = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID))
      }
      cursor.close()
    }
    var photo = BitmapFactory.decodeResource(
      context.getResources(),
      R.drawable.ic_profile
    )
    try {
      if (contactId != null) {
        val inputStream: InputStream? = Contacts.openContactPhotoInputStream(
          context.getContentResolver(),
          ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId.toLong())
        )
        if (inputStream != null) {
          photo = BitmapFactory.decodeStream(inputStream)
        }
        assert(inputStream != null)
        inputStream?.close()
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return photo
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
          referCode = dataObject.optString("referral_code")

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