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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.buzzvil.buzzad.benefit.core.ad.AdError
import com.buzzvil.buzzad.benefit.presentation.feed.BuzzAdFeed
import com.buzzvil.buzzad.benefit.presentation.feed.FeedConfig
import com.buzzvil.buzzad.benefit.presentation.feed.FeedFragment
import com.buzzvil.model.ScreenSize
import com.buzzvil.views.CustomFeedHeaderViewAdapter
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup


class BuzzVilFeedViewManager(reactContext: ReactApplicationContext) :
  ViewGroupManager<LinearLayout>() {

  companion object {
    private const val COMMAND_CREATE = "create"
  }

  private var propWidth = 0
  private var propHeight = 0
  private var unitId = ""
  private var title = ""


  private val reactContext: ReactApplicationContext

  private var feedFragment: FeedFragment? = null
  private var inquiryView: ImageView? = null

  init {
    this.reactContext = reactContext
  }

  override fun getName() = "BuzzvilFeedViewManager"

  override fun createViewInstance(reactContext: ThemedReactContext): LinearLayout {
    Log.d(name, "call createViewInstance")

    val layout = LinearLayout(reactContext)
    layout.orientation = LinearLayout.VERTICAL

    return layout
  }

  override fun onDropViewInstance(view: LinearLayout) {
    Log.d(name, "call onDropViewInstance")
    deleteFragment()
    inquiryView?.setOnClickListener(null)
    inquiryView = null
    super.onDropViewInstance(view)
  }

  @ReactProp(name = "unitId")
  fun setUnitId(view: LinearLayout?, unitId: String) {
    Log.d(name, "call setUnitId")
    this.unitId = unitId
  }

  @ReactProp(name = "title")
  fun setTitle(view: LinearLayout?, title: String) {
    Log.d(name, "call setTitle")
    this.title = title
  }

  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: LinearLayout?, index: Int, value: Int) {
    if (index == 0) {
      propWidth = value
    }
    if (index == 1) {
      propHeight = value
    }
  }

  override fun receiveCommand(root: LinearLayout, commandId: String?, args: ReadableArray?) {
    Log.d(name, "call receiveCommand $commandId")
    super.receiveCommand(root, commandId, args)

    when (commandId!!) {
      COMMAND_CREATE -> createView(root, args!!.getInt(0))
    }
  }

  private fun createView(root: LinearLayout, reactNativeViewId: Int) {
    Log.d(name, "call createView")

    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)

    if (parentView != null) {
      setupLayout(parentView)

      createFeedFragment(reactNativeViewId)

    }
  }

  private fun createFeedFragment(reactNativeViewId: Int) {

    val activity = reactContext.currentActivity as FragmentActivity

    val feedConfig = FeedConfig.Builder(unitId)
      .feedHeaderViewAdapterClass(CustomFeedHeaderViewAdapter().javaClass)
      .build()

    val buzzAdFeed: BuzzAdFeed = BuzzAdFeed.Builder().feedConfig(feedConfig).build()

    buzzAdFeed.load(object : BuzzAdFeed.FeedLoadListener {

      override fun onSuccess() {
        val feedTotalReward = buzzAdFeed.getAvailableRewards() // 적립 가능한 총 포인트 금액
        Log.d(name, "feedTotalReward: $feedTotalReward")
      }

      override fun onError(error: AdError?) {
        Log.d(name, "error: ${error.toString()}")
        Log.d(name, "errorType: ${error?.adErrorType?.name}")
      }
    })

    feedFragment = buzzAdFeed.getFragment()

    activity.supportFragmentManager
      .beginTransaction()
      .replace(reactNativeViewId, feedFragment!!)
      .runOnCommit {

        val feedHeader = feedFragment!!::class.java.declaredFields.single {
          it.type.toString().endsWith("FeedHeaderViewAdapter")
        }.apply { isAccessible = true }.get(feedFragment!!) as CustomFeedHeaderViewAdapter

        Log.d(name, "create feedHeader")

        feedHeader.setUnitId(unitId)
        feedHeader.setTitle(title)

      }
      .commitAllowingStateLoss()
  }

  private fun deleteFragment() {
    if (feedFragment != null) {
      val activity = reactContext.currentActivity as FragmentActivity
      activity.supportFragmentManager.beginTransaction().remove(feedFragment!!).commit()
      feedFragment == null;
    }

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
