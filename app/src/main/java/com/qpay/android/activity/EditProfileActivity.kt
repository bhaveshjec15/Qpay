package com.qpay.android.activity

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.qpay.android.R
import com.qpay.android.R.color
import com.qpay.android.R.style
import com.qpay.android.databinding.ActivityEditProfileBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.profile_image
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

class EditProfileActivity : AppCompatActivity() {
  private lateinit var binding: ActivityEditProfileBinding
  var dialog: Dialog? = null
  private var selectedImagePath: String? = null
  private var outputFileUriDoc: Uri? = null
  private var imageFileDoc: File? = null
  private var imageBitmap: Bitmap? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
    callApiGetProfile()
  }

  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    binding.ivProfile.setOnClickListener {
      showPopmenu()
    }

    binding.btnSubmit.setOnClickListener {
      if (binding.etFirstName.text.isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter First Name")
      } else if (binding.etLastName.text.isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Last Name")
      } else if (binding.etEmailName.text.isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Email Address")
      } else if (binding.etAddress.text.isNullOrEmpty()) {
        showSnackBar(this, binding.mainLayout, "Enter Address")
      } else if (imageFileDoc == null) {
        showSnackBar(this, binding.mainLayout, "Upload profile photo")
      } else {
        callApi(this)
      }
    }
  }

  private fun callApi(editProfileActivity: EditProfileActivity) {
    binding.progress.visibility = View.VISIBLE
    val fname: Part =
      Part.createFormData("first_name", binding.etFirstName.text.toString())
    val lname: Part =
      Part.createFormData("last_name", binding.etLastName.text.toString())
    val email: Part =
      Part.createFormData("email", binding.etEmailName.text.toString())
    val address: Part =
      Part.createFormData("address", binding.etAddress.text.toString())

    var image1: Part? = null
    if (imageFileDoc == null) {
      val requestFile1: RequestBody =
        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
      image1 = Part.createFormData("image", "abc", requestFile1)
    }
    else {
      val requestFile11: RequestBody = RequestBody.create(
        "multipart/form-data".toMediaTypeOrNull(),
        imageFileDoc!!
      )
      image1 = Part.createFormData(
        "image",
        imageFileDoc!!.name,
        requestFile11
      )
    }

    val apiInterface = ApiInterface.create().updateProfile(
      getStringShrd(baseContext,
        CommonUtils.accessToken),fname,lname,email,address,image1
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1 = response.body()?.string()
        var jsonObject: JSONObject? = null
        Log.e("res", response1.toString())
        if(response1 ==null){
           jsonObject = JSONObject(response.errorBody()?.string())
        }
        else{
           jsonObject = JSONObject(response1)
        }
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode==200) {
          showSnackBar(editProfileActivity, binding.mainLayout, message)
          finish()
        } else {
          showSnackBar(editProfileActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

  private fun showPopmenu() {
    dialog = Dialog(this, style.AppTheme)
    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog!!.setContentView(R.layout.cameradialog)
    dialog!!.getWindow()!!
      .setBackgroundDrawable(ColorDrawable(resources.getColor(color.trans_black)))
    val mCamerabtn =
      dialog!!.findViewById(R.id.cameradialogbtn) as Button
    val mGallerybtn =
      dialog!!.findViewById(R.id.gallerydialogbtn) as Button
    val btnCancell =
      dialog!!.findViewById(R.id.canceldialogbtn) as Button
    dialog!!.getWindow()!!
      .setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)


    mCamerabtn.setOnClickListener {
      /* val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
       imageFileImage = File(
           Environment.getExternalStorageDirectory(),
           System.currentTimeMillis().toString() + ".jpg"
       )
       outputFileUriImage = Uri.fromFile(imageFileImage)
       if (Build.VERSION.SDK_INT >= 24) {
           outputFileUriImage = FileProvider.getUriForFile(
               activity!!,
               activity!!.getApplicationContext().getPackageName()
                   .toString() + ".provider",
               imageFileImage!!
           )
       } else {
           outputFileUriImage = Uri.fromFile(imageFileImage)
       }
       Log.d("TAG", "outputFileUri intent$outputFileUriImage")
       intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriImage)
       startActivityForResult(intent, 3)*/

      val values = ContentValues()
      imageFileDoc = File(
        Environment.getExternalStorageDirectory(),
        System.currentTimeMillis().toString() + ".jpg"
      )
      outputFileUriDoc = this.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
      )
      //camera intent
      val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriDoc)
      startActivityForResult(cameraIntent, 0)
      dialog!!.cancel()
    }

    mGallerybtn.setOnClickListener {
      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 1)
      dialog!!.cancel()
    }

    btnCancell.setOnClickListener { dialog?.dismiss()}
    dialog!!.show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      0 -> {
        if (resultCode == RESULT_OK) {
          if (outputFileUriDoc != null) {
            selectedImagePath =
              CommonUtils.getRealPathFromURI(this, outputFileUriDoc!!)
            imageFileDoc = File(selectedImagePath)
            var ims: InputStream? = null
            try {
              ims = this.getContentResolver()
                .openInputStream(outputFileUriDoc!!)
            } catch (e: FileNotFoundException) {
              e.printStackTrace()
            }
            imageBitmap = BitmapFactory.decodeStream(ims)
            binding.ivProfile.setImageBitmap(imageBitmap)

            if (imageFileDoc != null) {
              selectedImagePath =
                imageFileDoc!!.getAbsolutePath() // outputFileUri.getPath().
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
          outputFileUriDoc = data!!.data
          selectedImagePath =
            CommonUtils.getRealPathFromURI(this, outputFileUriDoc!!)
          imageFileDoc = File(selectedImagePath)
          var ims: InputStream? = null

          try {
            ims = this.getContentResolver()
              .openInputStream(outputFileUriDoc!!)
          } catch (e: FileNotFoundException) {
            e.printStackTrace()
          }
          imageBitmap = BitmapFactory.decodeStream(ims)
          binding.ivProfile.setImageBitmap(imageBitmap)

          if (imageFileDoc != null) {
            selectedImagePath =
              imageFileDoc!!.getAbsolutePath() // outputFileUri.getPath().
            //  imageFile = Util.compressImage(Env.currentActivity, imageFile);
          }
          // updateImageToServer(resultCode)
        }
      }
      else -> {
      }
    }

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
          binding.etFirstName.setText(dataObject.optString("first_name"))
          binding.etLastName.setText(dataObject.optString("last_name"))
          binding.etEmailName.setText(dataObject.optString("email"))
          binding.etAddress.setText(dataObject.optString("address"))
          var image = dataObject.optString("image_url")
          if(image.isNullOrEmpty()){
            binding.ivProfile.setImageResource(R.drawable.ic_profile)
          }else{
            Picasso.get().load(image).into(binding.ivProfile)
          }

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