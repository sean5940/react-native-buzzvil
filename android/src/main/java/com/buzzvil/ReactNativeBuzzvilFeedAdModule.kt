package com.buzzvil

import android.util.Log
import com.buzzvil.buzzad.benefit.BuzzAdBenefit
import com.buzzvil.buzzad.benefit.BuzzAdBenefitConfig
import com.buzzvil.buzzad.benefit.core.ad.AdError
import com.buzzvil.buzzad.benefit.core.models.UserProfile
import com.buzzvil.buzzad.benefit.presentation.feed.BuzzAdFeed
import com.buzzvil.buzzad.benefit.presentation.feed.FeedConfig
import com.buzzvil.model.FeedIdInfo
import com.buzzvil.model.UserInfo
import com.buzzvil.views.CustomFeedHeaderViewAdapter
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap


class ReactNativeBuzzvilFeedAdModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val reactContext: ReactApplicationContext;

  companion object {
    const val NAME = "BuzzvilFeedAdModule"
  }

  init {
    this.reactContext = reactContext
  }

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun show(promise: Promise) {
    Log.d(name, "FeedAd show")

    val buzzAdFeed: BuzzAdFeed = BuzzAdFeed.Builder().build()

    buzzAdFeed.load(object : BuzzAdFeed.FeedLoadListener {

      override fun onSuccess() {
        buzzAdFeed.show(reactContext)
      }

      override fun onError(error: AdError?) {
        Log.d(name, "error: ${error.toString()}")
        Log.d(name, "errorType: ${error?.adErrorType?.name}")
      }
    })

    promise.resolve(null)
  }



}


