package com.buzzvil

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class BuzzvilPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(
      ReactNativeBuzzvilAdModule(reactContext), ReactNativeBuzzvilFeedAdModule(reactContext)
    )
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(BuzzvilFeedViewManager(reactContext), BuzzvilNativeViewManager(reactContext))
  }
}
