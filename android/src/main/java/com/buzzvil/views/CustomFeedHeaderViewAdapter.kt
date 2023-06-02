package com.buzzvil.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.buzzvil.R
import com.buzzvil.buzzad.benefit.BuzzAdBenefit
import com.buzzvil.buzzad.benefit.presentation.feed.header.FeedHeaderViewAdapter


class CustomFeedHeaderViewAdapter : FeedHeaderViewAdapter {
  private var title: String = "적립 찬스!"
  private var unitId: String = ""
  private var inquiryImage: ImageView? = null

  companion object {
    private const val name:String = "FeedHeaderViewAdapter"
  }

  fun setUnitId(unitId:String) {
    Log.d(name, "setUnitId: ${unitId}")
    this.unitId = unitId
  }

  fun setTitle(title:String){
    Log.d(name, "setTitle: ${title}")
    this.title = title
  }

  override fun onCreateView(context: Context, parent: ViewGroup): View {
    Log.d(name, "onCreateView")
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater.inflate(R.layout.feed_header_layout, parent, false)
  }

  override fun onBindView(view: View, reward: Int) {
    Log.d(name, "onBindView")

    inquiryImage = view.findViewById(R.id.inquiry)
    inquiryImage!!.setOnClickListener {
      Log.d(name, "[onClickListener] unitId:$unitId ")

      BuzzAdBenefit.getInstance().showInquiryPage(view.context, unitId)
    }

    val titleText = view.findViewById<TextView>(R.id.feedHeaderTitle)
    titleText.text = title
  }

  override fun onDestroyView() {
    Log.d(name, "onDestroyView")

    inquiryImage?.setOnClickListener(null)
    inquiryImage = null
  }
}
