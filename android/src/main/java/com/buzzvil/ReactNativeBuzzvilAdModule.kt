package com.buzzvil

import android.util.Log
import com.buzzvil.buzzad.benefit.BuzzAdBenefit
import com.buzzvil.buzzad.benefit.BuzzAdBenefitConfig
import com.buzzvil.buzzad.benefit.core.models.UserProfile
import com.buzzvil.buzzad.benefit.presentation.feed.FeedConfig
import com.buzzvil.model.FeedIdInfo
import com.buzzvil.model.UserInfo
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap


class ReactNativeBuzzvilAdModule(reactContext: ReactApplicationContext?) :
  ReactContextBaseJavaModule(reactContext) {

  companion object {
    const val NAME = "BuzzvilAdModule"
  }

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun initialize(requestFeedInfo: ReadableMap?, promise: Promise) {
    Log.d(name, "buzzAd initialize")

    val (feedId) = buildAdIdInfo(requestFeedInfo)

    val buzzAdBenefitConfig: BuzzAdBenefitConfig = if (feedId == null) {
      BuzzAdBenefitConfig.Builder(reactApplicationContext.applicationContext)
        .build()
    } else {
      Log.d(name, "buzzAd feedId: $feedId")

      val feedConfig = FeedConfig.Builder(feedId)
        .build()
      BuzzAdBenefitConfig.Builder(reactApplicationContext.applicationContext)
        .setDefaultFeedConfig(feedConfig)
        .build()
    }

    BuzzAdBenefit.init(reactApplicationContext.applicationContext, buzzAdBenefitConfig)

    promise.resolve(null)
  }

  @ReactMethod
  fun setUserInfo(requestUserInfo: ReadableMap?, promise: Promise) {

    val userProfileBuilder = UserProfile.Builder(BuzzAdBenefit.getUserProfile())

    val (userId, gender, birthYear) = buildUserInfo(requestUserInfo)
    Log.d(name, "setUserInfo userId: $userId")
    Log.d(name, "setUserInfo gender: $gender")
    Log.d(name, "setUserInfo birthYear: $birthYear")


    userProfileBuilder.userId(userId)

    if (gender != null) {
      userProfileBuilder.gender(gender)
    }

    if (birthYear != null) {
      userProfileBuilder.birthYear(birthYear)
    }

    BuzzAdBenefit.setUserProfile(userProfileBuilder.build());

    promise.resolve(null)
  }

  private fun buildUserInfo(requestUserinfo: ReadableMap?): UserInfo {
    var userId: String = ""
    var gender: UserProfile.Gender? = null
    var birthYear: Int? = null

    if (requestUserinfo?.hasKey("userId") == true) {
      userId = requestUserinfo.getString("userId").toString();
    }

    if (requestUserinfo?.hasKey("gender") == true) {
      gender =
        UserProfile.Gender.valueOf(requestUserinfo.getString("gender").toString().uppercase());
    }

    if (requestUserinfo?.hasKey("birthYear") == true) {
      birthYear = requestUserinfo.getInt("birthYear")
    }

    return UserInfo(userId, gender, birthYear)
  }

  private fun buildAdIdInfo(requestFeedIdInfo: ReadableMap?): FeedIdInfo {
    var feedId: String? = null

    if (requestFeedIdInfo?.hasKey("feedId") == true) {
      feedId = requestFeedIdInfo.getString("feedId");
    }

    return FeedIdInfo(feedId)
  }

}


