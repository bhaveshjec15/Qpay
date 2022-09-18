package com.qpay.android.gas_cylinder.customParam

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.xendit.consumerapp.utils.extension.DATE_WITH_TIME_FORMAT
import co.xendit.consumerapp.utils.extension.FULL_DATE_FORMAT
import co.xendit.consumerapp.utils.extension.getFormattedDate
import co.xendit.consumerapp.utils.extension.parseDate
import com.qpay.android.R
import com.qpay.android.gas_cylinder.customParam.CustomParamAdapter.ViewHolder
import com.qpay.android.model.HistoryModel

class CustomParamAdapter(val mContext: Context, private val mList: List<CustomParamListModel>) :
  RecyclerView.Adapter<ViewHolder>() {

  // create new views
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    // inflates the card_view_design view
    // that is used to hold list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_custom_param, parent, false)

    return ViewHolder(view)
  }

  // binds the list items to a view
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val ItemsViewModel = mList[position]

    holder.title.hint = ItemsViewModel.paramName



    // sets the image to the imageview from our itemHolder class
    //  holder.imageView.setImageResource(ItemsViewModel.image)

  }



  // return the number of the items in the list
  override fun getItemCount(): Int {
    return mList.size
  }

  // Holds the views for adding it to image and text
  class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
    val title: EditText = itemView.findViewById(R.id.etAmountItem)


  }
}