package com.qpay.android.bank

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qpay.android.R
import com.qpay.android.interface1.onDeleteClick
import com.qpay.android.bank.ShowBankAdapter.ViewHolder

class ShowBankAdapter(
  val mContext: Context,
  private val mList: List<ShowBankModel>,
  var onDeleteClick: onDeleteClick
) :
  RecyclerView.Adapter<ViewHolder>() {

  // create new views
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    // inflates the card_view_design view
    // that is used to hold list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_show_bank, parent, false)

    return ViewHolder(view)
  }

  // binds the list items to a view
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val ItemsViewModel = mList[position]

    holder.tvBankName.text = ItemsViewModel.bankAccount
    holder.tvCode.text = ItemsViewModel.code
    holder.tvCity.text = ItemsViewModel.city
    holder.tvState.text = ItemsViewModel.state

    if (ItemsViewModel.isDefault) {
      holder.tvPrimaryStatus.visibility = View.VISIBLE
    } else {
      holder.tvPrimaryStatus.visibility = View.INVISIBLE
    }

    holder.ivMore.setOnClickListener {

      if (holder.layoutDialog.visibility == View.VISIBLE) {
        holder.layoutDialog.visibility = View.GONE
      } else {
        holder.layoutDialog.visibility = View.VISIBLE
      }
    }

    holder.tvPrimary.setOnClickListener {
      onDeleteClick.onClick1(ItemsViewModel.id, "primary")
      holder.layoutDialog.visibility = View.GONE
    }

    holder.tvDelete.setOnClickListener {
      onDeleteClick.onClick1(ItemsViewModel.id, "delete")
      holder.layoutDialog.visibility = View.GONE
    }

  }

  // return the number of the items in the list
  override fun getItemCount(): Int {
    return mList.size
  }

  // Holds the views for adding it to image and text
  class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
    val tvBankName: TextView = itemView.findViewById(R.id.tvBankName)
    val tvPrimaryStatus: TextView = itemView.findViewById(R.id.tvPrimaryStatus)
    val tvCode: TextView = itemView.findViewById(R.id.tvCode)
    val tvCity: TextView = itemView.findViewById(R.id.tvCity)
    val tvState: TextView = itemView.findViewById(R.id.tvState)
    val ivMore: ImageView = itemView.findViewById(R.id.ivMore)
    val layoutDialog: LinearLayout = itemView.findViewById(R.id.layoutDialog)
    val tvPrimary: TextView = itemView.findViewById(R.id.tvPrimary)
    val tvDelete: TextView = itemView.findViewById(R.id.tvDelete)

  }
}