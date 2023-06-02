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
import java.text.DecimalFormat


class CustomFeedHeaderViewAdapter : FeedHeaderViewAdapter {

  private val rewardDecFormat:DecimalFormat = DecimalFormat("#,###")

  private var reward:Int = 0
  private var unitId: String = ""
  private var inquiryImage: ImageView? = null
  private var titleTextView: TextView? = null
  private var rewardTextView: TextView? = null
  private var rewardText:String =""

  companion object {
    private const val name: String = "FeedHeaderViewAdapter"
  }

  fun setUnitId(unitId: String) {
    Log.d(name, "setUnitId: $unitId")
    this.unitId = unitId
  }

  fun setTitle(title: String) {
    Log.d(name, "setTitle: $title")
    titleTextView?.text = title
    titleTextView?.visibility = if (title.isEmpty()) {
      TextView.GONE
    } else {
      TextView.VISIBLE
    }
  }

  fun setRewardText(text: String) {
    Log.d(name, "setRewardText: $text")
    this.rewardText = text
    rewardTextView?.text = "${rewardText}${rewardDecFormat.format(this.reward)}"
    rewardTextView?.visibility = if (text.isEmpty()) {
      TextView.GONE
    } else {
      TextView.VISIBLE
    }
  }

  override fun onCreateView(context: Context, parent: ViewGroup): View {
    Log.d(name, "onCreateView")
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater.inflate(R.layout.feed_header_layout, parent, false)
  }

  override fun onBindView(view: View, reward: Int) {
    this.reward = reward
    Log.d(name, "onBindView reward:$reward")

    inquiryImage = view.findViewById(R.id.inquiry)
    inquiryImage!!.setOnClickListener {
      Log.d(name, "[onClickListener] unitId:$unitId ")

      BuzzAdBenefit.getInstance().showInquiryPage(view.context, unitId)
    }

    titleTextView = view.findViewById(R.id.feedHeaderTitle)
    rewardTextView = view.findViewById(R.id.rewardText)

    if(rewardTextView?.visibility != TextView.GONE){
      rewardTextView?.text = "${rewardText}${rewardDecFormat.format(this.reward)}"
    }
  }

  override fun onDestroyView() {
    Log.d(name, "onDestroyView")

    inquiryImage?.setOnClickListener(null)
    inquiryImage = null
  }
}
