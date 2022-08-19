package com.qpay.android.rechargePrepad.all

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.qpay.android.R
import com.qpay.android.R.string
import com.qpay.android.network.ApiInterface
import com.qpay.android.rechargePrepad.AllPlansActivity
import com.qpay.android.rechargePrepad.PaymentValidationRequestModel
import com.qpay.android.rechargePrepad.RechargePayActivity
import com.qpay.android.rechargePrepad.all.AllListAdapter.ViewHolder
import com.qpay.android.requestModel.FasTagBillerRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.squareup.picasso.Picasso
import com.vdx.designertoast.DesignerToast
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllListAdapter(val mContext: Context, private val mList: List<AllPackModel>) :
  RecyclerView.Adapter<ViewHolder>() {

  // create new views
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    // inflates the card_view_design view
    // that is used to hold list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_all_pack, parent, false)

    return ViewHolder(view)
  }

  // binds the list items to a view
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val ItemsViewModel = mList[position]

    holder.price.setText(mContext.resources.getString(R.string.Rs) + " " + ItemsViewModel.price)
    holder.description.text = ItemsViewModel.pkgDescription
    holder.validity.text = ItemsViewModel.validitiy

    holder.layoutMain.setOnClickListener {
      /*   val intent = Intent(mContext, ElectricityInputActivity::class.java)
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           intent.putExtra("billerId", ItemsViewModel.billerId)
           intent.putExtra("billerName", ItemsViewModel.billerName)
           intent.putExtra("paramName", ItemsViewModel.paramName)
           intent.putExtra("logo", ItemsViewModel.logoUrl)
           mContext.startActivity(intent)*/
      AllPlansActivity.paramAmount = ItemsViewModel.price
      AllPlansActivity.paramPlanName = ItemsViewModel.planName
      AllPlansActivity.paramValidity = ItemsViewModel.validitiy
      AllPlansActivity.paramDescription = ItemsViewModel.pkgDescription

      var param = FasTagBillerRequest(mContext.resources.getString(string.param_prepaid), 0, 0)
      callApiGetBillerId(param, AllPlansActivity.operatorName.toString())
    }

    // sets the image to the imageview from our itemHolder class
    //  holder.imageView.setImageResource(ItemsViewModel.image)

  }

  // return the number of the items in the list
  override fun getItemCount(): Int {
    return mList.size
  }

  // Holds the views for adding it to image and text
  class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
    val price: TextView = itemView.findViewById(R.id.tvPrice)
    val validity: TextView = itemView.findViewById(R.id.tvValidity)
    val description: TextView = itemView.findViewById(R.id.tvDescription)
    val layoutMain: LinearLayout = itemView.findViewById(R.id.layoutMain)
    // val logo: ImageView = itemView.findViewById(R.id.iv_biller_logo)

  }

  private fun callApiGetBillerId(param: FasTagBillerRequest, selectedOperatorName: String) {
    DesignerToast.defaultToast(mContext, "Processing...", Gravity.BOTTOM, Toast.LENGTH_LONG);
    val apiInterface = ApiInterface.create().getFasTagList(
      getStringShrd(
        mContext,
        CommonUtils.accessToken
      ), param
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
        Log.e("response", "getBillerId: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataArray = jsonObject.optJSONArray("data")
          var ss = dataArray.length() - 1
          for (i in 0..ss) {
            var obj = dataArray.getJSONObject(i)
            var billerName = obj.optString("billerName")

            if (selectedOperatorName.equals(billerName, true)) {
              AllPlansActivity.paramBillerId = obj.optString("billerId")
              AllPlansActivity.paramBillerName = obj.optString("billerName")
              var sendName: String? = null
              var name = getStringShrd(mContext, CommonUtils.userName)
              if (name.equals("")) sendName = "Testing" else sendName = getStringShrd(mContext, CommonUtils.userName)

              var params = PaymentValidationRequestModel(
                AllPlansActivity.paramAmount,
                AllPlansActivity.paramBillerId,
                AllPlansActivity.paramCircleRefId,
                AllPlansActivity.paramCustomerMobile,
                sendName,
              AllPlansActivity.paramOperatorCode
              )
              callApiGetRefId(params)
              break
            }
          }

        } else {
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
      }
    })
  }

  private fun callApiGetRefId(param: PaymentValidationRequestModel) {
    val apiInterface = ApiInterface.create().getPaymentValidation(
      getStringShrd(
        mContext,
        CommonUtils.accessToken
      ), param
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
        Log.e("response", "getPaymentValidation: " + response1.toString())
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataObj = jsonObject.optJSONObject("data")

          var refId = dataObj.optString("refId")

          val intent = Intent(mContext, RechargePayActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          intent.putExtra("amount", AllPlansActivity.paramAmount)
          intent.putExtra("billerId", AllPlansActivity.paramBillerId)
          intent.putExtra("billerName", AllPlansActivity.paramBillerName)
          intent.putExtra("circleRefId", AllPlansActivity.paramCircleRefId)
          intent.putExtra("fetchRefId", refId)
          intent.putExtra("operatorCode", AllPlansActivity.paramOperatorCode)
          intent.putExtra("planId", AllPlansActivity.paramPlanName)
          intent.putExtra("validity", AllPlansActivity.paramValidity)
          intent.putExtra("description", AllPlansActivity.paramDescription)
          intent.putExtra("number", AllPlansActivity.paramCustomerMobile)

          mContext.startActivity(intent)

        } else {
          DesignerToast.Error(mContext, message, Gravity.BOTTOM, Toast.LENGTH_SHORT);
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
      }
    })
  }
}