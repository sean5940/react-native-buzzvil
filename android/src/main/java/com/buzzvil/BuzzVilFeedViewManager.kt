package com.buzzvil

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.buzzvil.buzzad.benefit.presentation.feed.BuzzAdFeed
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactPropGroup


class BuzzVilFeedViewManager(reactContext: ReactApplicationContext) :
  ViewGroupManager<FrameLayout>() {

  companion object {
    private const val COMMAND_CREATE = "create"
  }

  private var propWidth = 0
  private var propHeight = 0

  private val reactContext: ReactApplicationContext

  init {
    this.reactContext = reactContext
  }

  override fun getName() = "BuzzvilFeedViewManager"

  override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
    return FrameLayout(reactContext)
  }

  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: FrameLayout?, index: Int, value: Int) {
    if (index == 0) {
      propWidth = value
    }
    if (index == 1) {
      propHeight = value
    }
  }

  override fun receiveCommand(root: FrameLayout, commandId: String?, args: ReadableArray?) {
    super.receiveCommand(root, commandId, args)
    Log.d(name, "call receiveCommand $commandId")

    val reactNativeViewId = args!!.getInt(0)

    when (commandId!!) {
      COMMAND_CREATE -> createFragment(root, reactNativeViewId)
      else -> {}
    }
  }

  private fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
    Log.d(name, "call createFragment")

    val parentView = root.findViewById(reactNativeViewId) as ViewGroup
    parentView.parent
    setupLayout(parentView)

    val buzzAdFeed: BuzzAdFeed = BuzzAdFeed.Builder().build()
    val feedFragment = buzzAdFeed.getFragment()
    val activity = reactContext.currentActivity as FragmentActivity

    activity.supportFragmentManager
      .beginTransaction()
      .replace(reactNativeViewId, feedFragment)
      .commit()
  }

  private fun setupLayout(view: View) {
    Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        manuallyLayoutChildren(view)
        view.viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }

  /**
   * Layout all children properly
   */
  fun manuallyLayoutChildren(view: View) {

    val (screenWidth, screenHeight) = getScreenSize(reactContext)

    val width = if (propWidth > 0) {
      propWidth
    } else {
      screenWidth
    }
    val height = if (propHeight > 0) {
      propHeight
    } else {
      screenHeight
    }

    view.measure(
      View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    )

    view.layout(0, 0, width, height)

  }

  private fun getScreenSize(context: Context): ScreenSize {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowMetrics = wm.currentWindowMetrics
      val insets = windowMetrics.windowInsets
        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

      ScreenSize(
        windowMetrics.bounds.width() - insets.left - insets.right,
        windowMetrics.bounds.height() - insets.bottom - insets.top
      )
    } else {
      val displayMetrics = DisplayMetrics()
      wm.defaultDisplay.getMetrics(displayMetrics)
      ScreenSize(displayMetrics.widthPixels, displayMetrics.heightPixels)

    }
  }

}

data class ScreenSize(val width: Int, val height: Int)
