package com.qpay.android.electricty

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qpay.android.R
import com.qpay.android.electricty.ElectricityListAdapter.ViewHolder
import com.squareup.picasso.Picasso

class ElectricityListAdapter(val mContext: Context, private val mList: List<ElectrictyBillerListModel>) :
  RecyclerView.Adapter<ViewHolder>() {

  // create new views
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    // inflates the card_view_design view
    // that is used to hold list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_fastag_biller, parent, false)

    return ViewHolder(view)
  }

  // binds the list items to a view
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val ItemsViewModel = mList[position]

    holder.name.setText(ItemsViewModel.billerName)

    holder.layoutMain.setOnClickListener {
    val intent = Intent(mContext, ElectricityInputActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra("billerId", ItemsViewModel.billerId)
      intent.putExtra("billerName", ItemsViewModel.billerName)
      intent.putExtra("paramName", ItemsViewModel.paramName)
      intent.putExtra("logo", ItemsViewModel.logoUrl)
      mContext.startActivity(intent)
    }

    var logo = ItemsViewModel.logoUrl
    if (logo == null || logo.equals("null")) {
      holder.logo.setImageResource(R.drawable.ic_bbps)
    } else {
      Picasso.get().load(logo).placeholder(R.drawable.ic_bbps).into(holder.logo)
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
    val name: TextView = itemView.findViewById(R.id.billerName)
    val layoutMain: LinearLayout = itemView.findViewById(R.id.layoutMain)
    val logo: ImageView = itemView.findViewById(R.id.iv_biller_logo)

  }
}