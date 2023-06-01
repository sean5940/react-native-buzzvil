package com.buzzvil

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager


class BuzzvilPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    val moduleList: MutableList<NativeModule> = mutableListOf();
    moduleList.add(ReactNativeBuzzvilAdModule(reactContext))
    return moduleList
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(BuzzVilFeedViewManager(reactContext), BuzzVilNativeViewManager(reactContext))
  }
}
