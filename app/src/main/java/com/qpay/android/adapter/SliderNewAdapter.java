package com.qpay.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.qpay.android.R;
import com.qpay.android.model.SliderData;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class SliderNewAdapter extends SliderViewAdapter<SliderNewAdapter.SliderAdapterVH> {

  // creating a variable for
  // context and array list.
  private Context context;
  private List<SliderData> mSliderItems = new ArrayList<>();

  // constructor for our adapter class.
  public SliderNewAdapter(Context context, List<SliderData> mSliderItems) {
    this.context = context;
    this.mSliderItems = mSliderItems;
  }

  @Override
  public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
    // inside the on Create view holder method we are
    // inflating our layout file which we have created.
    View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_item_new, null);
    return new SliderAdapterVH(inflate);
  }

  @Override
  public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
    // inside on bind view holder method we are
    // getting url of image from our modal class
    // and setting that url for image inside our
    // image view using Picasso.
    final SliderData sliderItem = mSliderItems.get(position);
    Picasso.get().load(sliderItem.getImgUrl()).into(viewHolder.imageView);
  }

  @Override
  public int getCount() {
    // returning the size of our array list.
    return mSliderItems.size();
  }

  // view holder class for initializing our view holder.
  class SliderAdapterVH extends ViewHolder {

    // variables for our view and image view.
    View itemView;
    ImageView imageView;

    public SliderAdapterVH(View itemView) {
      super(itemView);
      // initializing our views.
      imageView = itemView.findViewById(R.id.idIVimage);
      this.itemView = itemView;
    }
  }
}

