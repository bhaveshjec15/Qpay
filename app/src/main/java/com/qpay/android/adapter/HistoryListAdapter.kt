package com.qpay.android.adapter

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
import com.qpay.android.activity.OTPActivity
import com.qpay.android.activity.SendAmountActivity
import com.qpay.android.adapter.HistoryListAdapter.ViewHolder
import com.qpay.android.model.HistoryModel
import com.qpay.android.utils.ContactsModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class HistoryListAdapter(val mContext: Context, private val mList: List<HistoryModel>) :
  RecyclerView.Adapter<ViewHolder>() {

  // create new views
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    // inflates the card_view_design view
    // that is used to hold list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_history, parent, false)

    return ViewHolder(view)
  }

  // binds the list items to a view
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val ItemsViewModel = mList[position]

    val date = parseDate(FULL_DATE_FORMAT, ItemsViewModel.created_at)
    var dateOriginal = getFormattedDate(DATE_WITH_TIME_FORMAT, date)


    holder.des.setText(ItemsViewModel.remark)
    holder.date.setText(dateOriginal)
    holder.title.setText(ItemsViewModel.category.replace("_"," "))
    if(ItemsViewModel.category.equals("Add_Money", true)|| ItemsViewModel.category.equals("RECEIVE_MONEY", true)){
      holder.amount.setTextColor(mContext.resources.getColor(R.color.colorBtn))
      holder.amount.setText("+ "+mContext.resources.getString(R.string.Rs)+" "+ItemsViewModel.amount)
      holder.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_money_credit))
      holder.des.visibility = View.GONE
    }else{
      holder.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_money_debit))
      holder.amount.setTextColor(mContext.resources.getColor(R.color.colorRed))
      holder.amount.setText("- "+mContext.resources.getString(R.string.Rs)+" "+ItemsViewModel.amount)
      holder.des.visibility = View.VISIBLE
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
    val title: TextView = itemView.findViewById(R.id.title)
    val des: TextView = itemView.findViewById(R.id.des)
    val amount: TextView = itemView.findViewById(R.id.amount)
    val date: TextView = itemView.findViewById(R.id.date)
    val icon: ImageView = itemView.findViewById(R.id.icon)

  }
}