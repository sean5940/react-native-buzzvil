package com.buzzvil

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Choreographer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.buzzvil.buzzad.benefit.BuzzAdBenefit
import com.buzzvil.buzzad.benefit.core.ad.AdError
import com.buzzvil.buzzad.benefit.presentation.feed.BuzzAdFeed
import com.buzzvil.buzzad.benefit.presentation.feed.FeedConfig
import com.buzzvil.buzzad.benefit.presentation.feed.FeedFragment
import com.buzzvil.model.ScreenSize
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


  private val reactContext: ReactApplicationContext

  private var inquiryView: ImageView? = null;

  init {
    this.reactContext = reactContext
  }

  override fun getName() = "BuzzvilFeedViewManager"

  override fun createViewInstance(reactContext: ThemedReactContext): LinearLayout {
    Log.d(name, "call createViewInstance")

    deleteFragment()

    val layout = LinearLayout(reactContext)
    layout.orientation = LinearLayout.VERTICAL

    return layout
  }

  override fun onDropViewInstance(view: LinearLayout) {
    Log.d(name, "call onDropViewInstance")
    deleteFragment()
    inquiryView?.setOnClickListener(null)
    inquiryView = null;
    super.onDropViewInstance(view)
  }

  @ReactProp(name = "unitId")
  fun setUnitId(view: LinearLayout?, unitId: String) {
    Log.d(name, "call setUnitId")
    this.unitId = unitId
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

      createHeader(root)
      createFeedFragment(reactNativeViewId)

    }
  }

  private fun createHeader(root: LinearLayout) {
    val header: LinearLayout = LinearLayout(reactContext)


    val headerLayoutParams = LinearLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        50f,
        reactContext.resources.displayMetrics
      ).toInt()
    )

    header.layoutParams = headerLayoutParams
    header.orientation = LinearLayout.HORIZONTAL
    header.gravity = Gravity.CENTER_VERTICAL
    header.background = reactContext.resources.getDrawable(R.color.bz_feed_toolbar_default)

    val paddingHorizontal = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      10f,
      reactContext.resources.displayMetrics
    ).toInt()

    val paddingVertical = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      5f,
      reactContext.resources.displayMetrics
    ).toInt()

    header.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

    val title: TextView = TextView(reactContext)

    title.layoutParams = LinearLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )

    title.text = "적립 찬스!"
    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    title.typeface = Typeface.create(title.typeface, Typeface.BOLD)
    title.setTextColor(reactContext.resources.getColor(R.color.bz_feed_header_text))
    header.addView(title)

    val space: View = View(reactContext)
    space.layoutParams = LinearLayout.LayoutParams(
      0,
      ViewGroup.LayoutParams.WRAP_CONTENT,
      1f
    )
    header.addView(space)

    inquiryView = createInquiryView()
    header.addView(inquiryView)

    root.addView(header);
  }

  private fun createFeedFragment(reactNativeViewId: Int) {
    val feedConfig = FeedConfig.Builder(unitId)
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

    val feedFragment = buzzAdFeed.getFragment()
    val activity = reactContext.currentActivity as FragmentActivity

    activity.supportFragmentManager
      .beginTransaction()
      .replace(reactNativeViewId, feedFragment)
      .commitAllowingStateLoss()
  }

  private fun createInquiryView(): ImageView {
    val inquiryView = ImageView(reactContext)
    inquiryView.setImageResource(R.drawable.inquiry)

    val layoutParams = LinearLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )

    inquiryView.layoutParams = layoutParams

    inquiryView.setOnClickListener {
      BuzzAdBenefit.getInstance().showInquiryPage(reactContext, unitId)
    }

    return inquiryView;
  }

  private fun deleteFragment() {
    var fragmentCount = 0;
    val activity = reactContext.currentActivity as FragmentActivity

    val transaction = activity.supportFragmentManager.beginTransaction()
    activity.supportFragmentManager.fragments.filterIsInstance<FeedFragment>()
      .forEach { feedFragment ->
        ++fragmentCount
        transaction.remove(feedFragment)
      }
    transaction.commit()

    Log.d(name, "fragment Count:$fragmentCount")
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
