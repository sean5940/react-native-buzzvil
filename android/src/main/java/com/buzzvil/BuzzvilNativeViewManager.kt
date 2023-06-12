package com.buzzvil

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.buzzvil.buzzad.benefit.core.ad.AdError
import com.buzzvil.buzzad.benefit.nativead2.api.NativeAd2
import com.buzzvil.buzzad.benefit.nativead2.api.NativeAd2EventListener
import com.buzzvil.buzzad.benefit.nativead2.api.NativeAd2StateChangedListener
import com.buzzvil.buzzad.benefit.nativead2.api.NativeAd2View
import com.buzzvil.buzzad.benefit.nativead2.api.NativeAd2ViewBinder
import com.buzzvil.buzzad.benefit.presentation.feed.navigation.NativeToFeedLayout
import com.buzzvil.buzzad.benefit.presentation.media.MediaView
import com.buzzvil.buzzad.benefit.presentation.reward.RewardResult
import com.buzzvil.model.ScreenSize
import com.buzzvil.views.CustomCtaView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.facebook.react.uimanager.events.RCTEventEmitter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class BuzzvilNativeViewManager(reactContext: ReactApplicationContext) :
  ViewGroupManager<FrameLayout>() {

  companion object {
    private const val COMMAND_CREATE = "create"
  }

  private var propWidth = 0
  private var propHeight = 0
  private var unitId = ""
  private var backgroundDrawable: Drawable

  private val reactContext: ReactApplicationContext

  init {
    this.reactContext = reactContext
    backgroundDrawable = ColorDrawable(Color.parseColor("#F7F5F5"))
  }

  override fun getName(): String {
    return "BuzzVilNativeViewManager"
  }

  override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
    return FrameLayout(reactContext)
  }


  @ReactProp(name = "unitId")
  fun setUnitId(view: FrameLayout?, unitId: String) {
    Log.d(name, "call setUnitId")
    this.unitId = unitId
  }

  @ReactProp(name = "background")
  fun setBackground(view: FrameLayout?, background: String) {
    Log.d(name, "call setBackground")
    backgroundDrawable = ColorDrawable(Color.parseColor(background))
  }

  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: FrameLayout?, index: Int, value: Int) {
    Log.d(name, "call setStyle index:${index} value:${value}")

    if (index == 0) {
      propWidth = value
    }
    if (index == 1) {
      propHeight = value
    }
  }

  override fun receiveCommand(root: FrameLayout, commandId: String?, args: ReadableArray?) {
    Log.d(name, "call receiveCommand $commandId")
    super.receiveCommand(root, commandId, args)

    when (commandId!!) {
      COMMAND_CREATE -> createNativeAd(root, args!!.getInt(0))
    }
  }

  private fun createNativeAd(root: FrameLayout, reactNativeViewId: Int) {
    Log.d(name, "call createNativeAd")

    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)

    if (parentView != null) {
      setupLayout(parentView)

      val view = inflate(reactContext, R.layout.native_ad, parentView)
      val nativeAd2View = view.findViewById<NativeAd2View>(R.id.nativeAd2View)
      nativeAd2View.background = backgroundDrawable

      val mediaView = view.findViewById<MediaView>(R.id.mediaView)
      val titleTextView = view.findViewById<TextView>(R.id.textTitle)
      val descriptionTextView = view.findViewById<TextView>(R.id.textDescription)
      val iconImageView = view.findViewById<ImageView>(R.id.imageIcon)

      val ctaView = view.findViewById<CustomCtaView>(R.id.ctaView)

      val nativeToFeedLayout = view.findViewById<NativeToFeedLayout>(R.id.native_to_feed_layout)
      nativeToFeedLayout.setNativeUnitId(unitId)

      val binder: NativeAd2ViewBinder = NativeAd2ViewBinder.Builder()
        .nativeAd2View(nativeAd2View)
        .mediaView(mediaView)
        .titleTextView(titleTextView)
        .descriptionTextView(descriptionTextView)
        .iconImageView(iconImageView)
        .ctaView(ctaView)
        .build(unitId)

      hideView(parentView)

      binder.addNativeAd2StateChangedListener(object : NativeAd2StateChangedListener {
        override fun onRequested() {
          Log.d(name, "onRequested")
          hideView(parentView)
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onRequested", null)
        }

        override fun onNext(nativeAd2: NativeAd2) {
          Log.d(name, "onNext")
          showView(parentView)
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onNext", null)

        }

        override fun onComplete() {
          Log.d(name, "onComplete")
          showView(parentView)
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onComplete", null)
        }

        override fun onError(adError: AdError) {
          Log.d(name, "error: ${adError.message}")
          Log.d(name, "errorType: ${adError?.adErrorType?.name}")
          hideView(parentView)
          Executors.newSingleThreadScheduledExecutor().schedule({
            binder.bind()
          }, 30, TimeUnit.MINUTES)
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onError", Arguments.createMap().apply { putString("error", adError?.adErrorType?.name) })
        }
      })

      binder.addNativeAd2EventListener(object:NativeAd2EventListener{
        override fun onClicked(nativeAd2: NativeAd2) {
          Log.d(name, "onClicked")
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onClicked", null)
        }

        override fun onImpressed(nativeAd2: NativeAd2) {
          Log.d(name, "onImpressed")
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onImpressed", null)
        }

        override fun onParticipated(nativeAd2: NativeAd2) {
          Log.d(name, "onParticipated")
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onParticipated", null)
        }

        override fun onRewardRequested(nativeAd2: NativeAd2) {
          Log.d(name, "onRewardRequested")
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onRewardRequested", null)
        }

        override fun onRewarded(nativeAd2: NativeAd2, rewardResult: RewardResult) {
          Log.d(name, "onRewarded")
          reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(root.id, "onRewarded", null)
        }
      })

      binder.bind()
    }
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any?>? {
    val superTypeConstants = super.getExportedCustomDirectEventTypeConstants()
    val export = superTypeConstants ?: MapBuilder.newHashMap<String, Any?>()
    export["onClicked"] = MapBuilder.of("registrationName", "onClicked")
    export["onImpressed"] = MapBuilder.of("registrationName", "onImpressed")
    export["onParticipated"] = MapBuilder.of("registrationName", "onParticipated")
    export["onRewardRequested"] = MapBuilder.of("registrationName", "onRewardRequested")
    export["onRewarded"] = MapBuilder.of("registrationName", "onRewarded")
    export["onRequested"] = MapBuilder.of("registrationName", "onRequested")
    export["onNext"] = MapBuilder.of("registrationName", "onNext")
    export["onComplete"] = MapBuilder.of("registrationName", "onComplete")
    export["onError"] = MapBuilder.of("registrationName", "onError")
    return export
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

  private fun showView(rootView: View) {
    rootView.visibility = View.VISIBLE
  }

  private fun hideView(rootView: View) {
    rootView.visibility = View.GONE
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
