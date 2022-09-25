package com.qpay.android.bank

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.droidbyme.dialoglib.DroidDialog
import com.qpay.android.R
import com.qpay.android.activity.AddBankActivity
import com.qpay.android.activity.BankAmountTransferActivity
import com.qpay.android.databinding.FragmentBankBinding
import com.qpay.android.interface1.onDeleteClick
import com.qpay.android.network.ApiInterface
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankFragment : Fragment(), onDeleteClick {
  private lateinit var binding: FragmentBankBinding
  var name: String = ""
  var email: String = ""
  var phone: String = ""

  var state: String = ""
  var pinCode: String = ""

  var list: ArrayList<ShowBankModel> = ArrayList()
  var adapter: ShowBankAdapter? = null

  private var onDeleteClick: onDeleteClick? = null

  var mContext: Context? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_bank, container, false
    )
    // binding.viewmodel = vm//attach your viewModel to xml
    mContext = requireContext()

    onDeleteClick = this
    return binding.root

  }


  override fun onResume() {
    super.onResume()
    callApiGetBank()

    binding.layoutBankItem.setOnClickListener {
      val intent = Intent(mContext, BankAmountTransferActivity::class.java)
      startActivity(intent)
    }

    binding.btnSubmit.setOnClickListener {
      val intent = Intent(mContext, AddBankActivity::class.java)
      startActivity(intent)

    }

     binding.btnAddBank.setOnClickListener {
       val intent = Intent(mContext, AddBankActivity::class.java)
       startActivity(intent)
     }

  }

  private fun callApiGetBank() {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().getSavedBank(
      getStringShrd(
        mContext!!,
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
        binding.progress.visibility = View.GONE
        list.clear()
        Log.e("response", "getSavedBank: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataArray = jsonObject.optJSONArray("data")
          if (dataArray.length() == 0) {
            binding.layoutAddBank.visibility = View.VISIBLE
            binding.layoutBankItem.visibility = View.GONE
            binding.btnAddBank.visibility = View.GONE
          } else {
            binding.layoutAddBank.visibility = View.GONE
            binding.layoutBankItem.visibility = View.VISIBLE
            binding.btnAddBank.visibility = View.VISIBLE
            var ss = dataArray.length() - 1
            for (i in 0..ss) {
              var model = ShowBankModel()
              var obj = dataArray.getJSONObject(i)
              model.bankAccount = obj.optString("bankAccount")
              model.code = obj.optString("ifsc")
              model.city = obj.optString("city")
              model.state = obj.optString("state")
              model.isDefault = obj.optBoolean("is_default")
              model.id = obj.optString("_id")

              list.add(model)
            }
            if (list.size > 0) {
              if (isAdded) {
                adapter = ShowBankAdapter(requireActivity(), list, onDeleteClick!!)
                binding.layoutBankItem.adapter = adapter
                adapter?.notifyDataSetChanged()
              }

            }
          }

          /* var dataObject = jsonObject.optJSONObject("data")

           binding.tvBankName.text = dataObject.optString("bankAccount")
           binding.tvCode.text = dataObject.optString("ifsc")
           binding.tvCity.text = dataObject.optString("city")
           binding.tvState.text = dataObject.optString("state")

           name=dataObject.optString("name")
           email=dataObject.optString("email")
           phone=dataObject.optString("phone")
           bankAccount=dataObject.optString("bankAccount")
           ifsc=dataObject.optString("ifsc")
           address=dataObject.optString("address1")
           city=dataObject.optString("city")
           state=dataObject.optString("state")
           pinCode=dataObject.optString("pincode")*/

        } else {
          binding.layoutAddBank.visibility = View.VISIBLE
          binding.layoutBankItem.visibility = View.GONE
          binding.btnAddBank.visibility = View.GONE
          // showSnackBar(bankTransferActivity, binding.mainLayout, message)
        }
      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }


  private fun deleteBank(str: String) {

    DroidDialog.Builder(mContext)
      .title("Delete Bank!")
      .content("Are you sure you want to delete?")
      .cancelable(true, true)
      .positiveButton(
        "Yes"
      ) {
        it.dismiss()
        callApiDelete(str)
      }
      .negativeButton("No", DroidDialog.onNegativeListener {
        it.cancel()
      })

      .show()
  }

  private fun callApiDelete(str: String) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().deleteBank(
      getStringShrd(
        mContext!!,
        CommonUtils.accessToken
      ),str
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1: String? = null
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(mContext!!, binding.mainLayout, message)
          callApiGetBank()

        } else {
          showSnackBar(mContext!!, binding.mainLayout, message)
        }
        Log.e("response", "pinsetup: " + response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }

  private fun callApiSetPrimary(str: String) {
    binding.progress.visibility = View.VISIBLE
    val apiInterface = ApiInterface.create().setPrimaryBank(
      getStringShrd(
        mContext!!,
        CommonUtils.accessToken
      ),str
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1: String? = null
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          showSnackBar(mContext!!, binding.mainLayout, message)
          callApiGetBank()

        } else {
          showSnackBar(mContext!!, binding.mainLayout, message)
        }
        Log.e("response", "setPrimary: " + response1.toString())

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
  fun setOnDeleteListener(listener: onDeleteClick) {
    onDeleteClick = listener
  }

  override fun onClick1(str: String, type: String) {
    if(type.equals("delete", true)){
      deleteBank(str)
    }else{
      callApiSetPrimary(str)
    }

  }


}