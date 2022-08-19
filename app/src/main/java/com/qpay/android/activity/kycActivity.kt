package com.qpay.android.activity

import android.R.layout
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.color
import com.qpay.android.R.style
import com.qpay.android.databinding.ActivityKycBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class kycActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

  private lateinit var binding: ActivityKycBinding
  var selectedId: String =""

  // for front side
  var dialogFront: Dialog? = null
  private var selectedImagePathFront: String? = null
  private var outputFileUriDocFront: Uri? = null
  private var imageFileDocFront: File? = null
  private var imageBitmapFront: Bitmap? = null

  // for back side
  var dialogBack: Dialog? = null
  private var selectedImagePathBack: String? = null
  private var outputFileUriDocBack: Uri? = null
  private var imageFileDocBack: File? = null
  private var imageBitmapBack: Bitmap? = null

  var country = arrayOf("AADHAR", "PEN")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this,R.layout.activity_kyc)
    binding.spinner.setOnItemSelectedListener(this);
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(this, layout.simple_spinner_item, country)
    aa.setDropDownViewResource(layout.simple_spinner_dropdown_item)
    binding.spinner.setAdapter(aa)

    binding.layoutFront.setOnClickListener { showPopmenuFront() }

    binding.layoutBack.setOnClickListener {showPopmenuBack()  }

   binding.btnSubmit.setOnClickListener {
     if (imageFileDocFront == null) {
       showSnackBar(this, binding.mainLayout, "Upload Front photo")
     }
     else if (imageFileDocBack == null) {
       showSnackBar(this, binding.mainLayout, "Upload Back photo")
     }
     else {
       callApi(this)
     }
   }
  }
  override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
   // Toast.makeText(applicationContext, country[position], Toast.LENGTH_LONG).show()
    selectedId = country[position]
  }

  override fun onNothingSelected(arg0: AdapterView<*>?) {
    // TODO Auto-generated method stub
  }

  private fun showPopmenuFront() {
    dialogFront = Dialog(this, style.AppTheme)
    dialogFront!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogFront!!.setContentView(R.layout.cameradialog)
    dialogFront!!.getWindow()!!
      .setBackgroundDrawable(ColorDrawable(resources.getColor(color.trans_black)))
    val mCamerabtn =
      dialogFront!!.findViewById(R.id.cameradialogbtn) as Button
    val mGallerybtn =
      dialogFront!!.findViewById(R.id.gallerydialogbtn) as Button
    val btnCancell =
      dialogFront!!.findViewById(R.id.canceldialogbtn) as Button
    dialogFront!!.getWindow()!!
      .setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)


    mCamerabtn.setOnClickListener {


      val values = ContentValues()
      imageFileDocFront = File(
        Environment.getExternalStorageDirectory(),
        System.currentTimeMillis().toString() + ".jpg"
      )
      outputFileUriDocFront = this.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
      )
      //camera intent
      val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriDocFront)
      startActivityForResult(cameraIntent, 0)
      dialogFront!!.cancel()
    }

    mGallerybtn.setOnClickListener {
      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 1)
      dialogFront!!.cancel()
    }

    btnCancell.setOnClickListener { dialogFront?.dismiss() }
    dialogFront!!.show()
  }

  private fun showPopmenuBack() {
    dialogBack = Dialog(this, style.AppTheme)
    dialogBack!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogBack!!.setContentView(R.layout.cameradialog)
    dialogBack!!.getWindow()!!
      .setBackgroundDrawable(ColorDrawable(resources.getColor(color.trans_black)))
    val mCamerabtn =
      dialogBack!!.findViewById(R.id.cameradialogbtn) as Button
    val mGallerybtn =
      dialogBack!!.findViewById(R.id.gallerydialogbtn) as Button
    val btnCancell =
      dialogBack!!.findViewById(R.id.canceldialogbtn) as Button
    dialogBack!!.getWindow()!!
      .setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)


    mCamerabtn.setOnClickListener {

      val values = ContentValues()
      imageFileDocBack = File(
        Environment.getExternalStorageDirectory(),
        System.currentTimeMillis().toString() + ".jpg"
      )
      outputFileUriDocBack = this.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
      )
      //camera intent
      val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriDocBack)
      startActivityForResult(cameraIntent, 2)
      dialogBack!!.cancel()
    }

    mGallerybtn.setOnClickListener {
      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 3)
      dialogBack!!.cancel()
    }

    btnCancell.setOnClickListener { dialogBack?.dismiss() }
    dialogBack!!.show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      0 -> {
        if (resultCode == RESULT_OK) {
          if (outputFileUriDocFront != null) {
            var ims: InputStream? = null
            try {
              ims = this.getContentResolver()
                .openInputStream(outputFileUriDocFront!!)
            } catch (e: FileNotFoundException) {
              e.printStackTrace()
            }
            imageBitmapFront = BitmapFactory.decodeStream(ims)
            binding.ivFront.setImageBitmap(imageBitmapFront)

            if (imageFileDocFront != null) {
              selectedImagePathFront =
                imageFileDocFront!!.getAbsolutePath() // outputFileUri.getPath().
              /* imageBitmap =
                 AppUtils.rotateImageBitmap(selectedImagePath, imageBitmap!!)*/
              // imageFileDoc = AppUtils.compressImage(activity!!, imageFileDoc)
            }
            //    updateImageToServer(resultCode)

          }
          // iv_doc.setImageBitmap(data!!.extras!!.get("data") as Bitmap)
        }
      }
      1 -> {
        if (resultCode == RESULT_OK) {
          outputFileUriDocFront = data!!.data
          selectedImagePathFront =
            CommonUtils.getRealPathFromURI(this, outputFileUriDocFront!!)
          imageFileDocFront = File(selectedImagePathFront)
          var ims: InputStream? = null

          try {
            ims = this.getContentResolver()
              .openInputStream(outputFileUriDocFront!!)
          } catch (e: FileNotFoundException) {
            e.printStackTrace()
          }
          imageBitmapFront = BitmapFactory.decodeStream(ims)
          binding.ivFront.setImageBitmap(imageBitmapFront)

          if (imageFileDocFront != null) {
            selectedImagePathFront =
              imageFileDocFront!!.getAbsolutePath() // outputFileUri.getPath().
            //  imageFile = Util.compressImage(Env.currentActivity, imageFile);
          }
          // updateImageToServer(resultCode)
        }
      }
      2 -> {
        if (resultCode == RESULT_OK) {
          if (outputFileUriDocBack != null) {
            var ims: InputStream? = null
            try {
              ims = this.getContentResolver()
                .openInputStream(outputFileUriDocBack!!)
            } catch (e: FileNotFoundException) {
              e.printStackTrace()
            }
            imageBitmapBack = BitmapFactory.decodeStream(ims)
            binding.ivBackImage.setImageBitmap(imageBitmapBack)

            if (imageFileDocBack != null) {
              selectedImagePathBack =
                imageFileDocBack!!.getAbsolutePath() // outputFileUri.getPath().
              /* imageBitmap =
                 AppUtils.rotateImageBitmap(selectedImagePath, imageBitmap!!)*/
              // imageFileDoc = AppUtils.compressImage(activity!!, imageFileDoc)
            }
            //    updateImageToServer(resultCode)

          }
          // iv_doc.setImageBitmap(data!!.extras!!.get("data") as Bitmap)
        }
      }
      3 -> {
        if (resultCode == RESULT_OK) {
          outputFileUriDocBack = data!!.data
          selectedImagePathBack =
            CommonUtils.getRealPathFromURI(this, outputFileUriDocBack!!)
          imageFileDocBack = File(selectedImagePathBack)
          var ims: InputStream? = null

          try {
            ims = this.getContentResolver()
              .openInputStream(outputFileUriDocBack!!)
          } catch (e: FileNotFoundException) {
            e.printStackTrace()
          }
          imageBitmapBack = BitmapFactory.decodeStream(ims)
          binding.ivBackImage.setImageBitmap(imageBitmapBack)

          if (imageFileDocBack != null) {
            selectedImagePathBack =
              imageFileDocBack!!.getAbsolutePath() // outputFileUri.getPath().
            //  imageFile = Util.compressImage(Env.currentActivity, imageFile);
          }
          // updateImageToServer(resultCode)
        }
      }
      else -> {
      }
    }

  }

  private fun callApi(editProfileActivity: kycActivity) {
    binding.progress.visibility = View.VISIBLE
    val fname: Part =
      Part.createFormData("type", selectedId)

    var imageFront: Part? = null
    var imageBack: Part? = null
    if (imageFileDocFront == null) {
      val requestFile1: RequestBody =
        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
      imageFront = Part.createFormData("image_front", "abc", requestFile1)
    }
    else {
      val requestFile11: RequestBody = RequestBody.create(
        "multipart/form-data".toMediaTypeOrNull(),
        imageFileDocFront!!
      )
      imageFront = Part.createFormData(
        "image_front",
        imageFileDocFront!!.getName(),
        requestFile11
      )
    }

    // for back image
    if (imageFileDocBack == null) {
      val requestFile1: RequestBody =
        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
      imageBack = Part.createFormData("image_back", "abc_back", requestFile1)
    }
    else {
      val requestFile11: RequestBody = RequestBody.create(
        "multipart/form-data".toMediaTypeOrNull(),
        imageFileDocBack!!
      )
      imageBack = Part.createFormData(
        "image_back",
        imageFileDocBack!!.getName(),
        requestFile11
      )
    }

    val apiInterface = ApiInterface.create().kycRequest(
      getStringShrd(baseContext,
        CommonUtils.accessToken),fname,imageFront,imageBack
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1 = response.body()?.string()
        Log.e("res", "response_kyc"+response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode==200) {
          showSnackBar(editProfileActivity, binding.mainLayout, message)
          //finish()
        } else {
          showSnackBar(editProfileActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}