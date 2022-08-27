package com.qpay.android.rechargePrepad

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
import com.qpay.android.R
import com.qpay.android.activity.OTPActivity
import com.qpay.android.activity.SendAmountActivity
import com.qpay.android.utils.ContactsModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class PrepadContactListAdapter(val mContext: Context, private val mList: List<ContactsModel>) :
  RecyclerView.Adapter<PrepadContactListAdapter.ViewHolder>() {

  // create new views
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    // inflates the card_view_design view
    // that is used to hold list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_contact, parent, false)

    return ViewHolder(view)
  }

  // binds the list items to a view
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val ItemsViewModel = mList[position]


    holder.name.setText(ItemsViewModel.name)
    holder.number.setText(ItemsViewModel.number)
   /* if(ItemsViewModel.img !=null){
      holder.profile.setImageBitmap(ItemsViewModel.img)
    }*/
    holder.profile.text = ItemsViewModel.name[0].toString()
    holder.layoutMain.setOnClickListener {
      val intent = Intent(mContext, AllPlansActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra("number",ItemsViewModel.number)
      mContext.startActivity(intent)
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
    val name: TextView = itemView.findViewById(R.id.name)
    val number: TextView = itemView.findViewById(R.id.number)
    val layoutMain: LinearLayout = itemView.findViewById(R.id.layoutMain)
    val profile: TextView = itemView.findViewById(R.id.iv_profile)

  }
}