package com.qpay.android.rechargePrepad

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.PhoneLookup
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.qpay.android.R
import com.qpay.android.adapter.ContactListAdapter
import com.qpay.android.databinding.ActivityContactListBinding
import com.qpay.android.databinding.ActivitySendToMobileBinding
import com.qpay.android.utils.ContactsModel
import com.qpay.android.utils.showSnackBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

class ContactListActivity : AppCompatActivity() {
  private lateinit var binding: ActivityContactListBinding
  var list: ArrayList<ContactsModel> = ArrayList()
  var listTemp: ArrayList<ContactsModel> = ArrayList()
  val listSize: MutableLiveData<String?> = MutableLiveData("")
  var adapter: PrepadContactListAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_list)
    GlobalScope.launch { getContactList() }
  }

  override fun onResume() {
    super.onResume()
    binding.ivBack.setOnClickListener { finish() }

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
            adapter = PrepadContactListAdapter(baseContext, listTemp)
            binding.rvContactList.adapter = adapter
            adapter?.notifyDataSetChanged()
          }
          else{
            if(s.toString().isNotEmpty()){
              var model = ContactsModel()
              model.name = "Unknown"
              model.number = s.toString()
              listTemp.add(model)
              adapter = PrepadContactListAdapter(baseContext, listTemp)
              binding.rvContactList.adapter = adapter
              adapter?.notifyDataSetChanged()
            }
          }
        }
      }
    })
  }

  @SuppressLint("Range")
  suspend fun getContactList() {
    list.clear()
    binding.progress.visibility = View.GONE
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
        binding.progress.visibility - View.GONE
        adapter = PrepadContactListAdapter(this, list)
        binding.rvContactList.adapter = adapter
        adapter?.notifyDataSetChanged()
        binding.progress.visibility - View.GONE
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
}