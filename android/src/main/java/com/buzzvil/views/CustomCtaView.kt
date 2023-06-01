package com.buzzvil.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.buzzvil.R
import com.buzzvil.buzzad.benefit.presentation.media.CtaView

class CustomCtaView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
  CtaView(context, attrs, defStyleAttr) {

  constructor(context: Context) :this(context, null)

  constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

  private val rewardImageView: ImageView
  private val ctaTextView: TextView

  init {
    inflate(getContext(), R.layout.view_customized_cta, this)
    rewardImageView = findViewById(R.id.imageReward)
    rewardImageView.setImageResource(R.drawable.mileage_icon)

    ctaTextView = findViewById(R.id.textCta)
    ctaTextView.text = "1,000"

  }

  // CTA 텍스트 설정
  fun setCtaText(ctaText: String?) {
    ctaTextView.text = ctaText
  }

  // CTA 아이콘 설정
  fun showRewardIcon() {
    rewardImageView.setImageResource(R.drawable.mileage_icon)
    rewardImageView.visibility = View.VISIBLE
  }

  // CTA 아이콘 숨기기 처리
  fun hideRewardIcon() {
    rewardImageView.visibility = View.GONE
  }

  // 사용자가 광고에 참여 중인 상태
  override fun renderViewParticipatingState(callToAction: String) {
    showRewardIcon()
    setCtaText("1,000")
  }

  // 사용자가 광고 참여를 완료한 상태
  override fun renderViewParticipatedState(callToAction: String) {
    showRewardIcon()
    setCtaText("1,000")
  }

  // 사용자가 아직 광고에 참여하지 않은 상태
  override fun renderViewRewardAvailableState(callToAction: String, reward: Int) {
    showRewardIcon()
    setCtaText("1,000")
  }

  // 사용자가 획득할 리워드가 없는 광고
  override fun renderViewRewardNotAvailableState(callToAction: String) {
    hideRewardIcon()
    setCtaText(callToAction)
  }
}
