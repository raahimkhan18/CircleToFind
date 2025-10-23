package com.example.circletofind

import android.app.Service
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private var drawingOverlay: View? = null

    override fun onBind(intent: android.content.Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inflate gesture bar overlay
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_gesture_bar, FrameLayout(this), false)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            150,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE, // deprecated for old devices
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 150 // offset from bottom

        // Long press listener
        overlayView.setOnTouchListener(object : View.OnTouchListener {
            private var longPressStart = 0L

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> longPressStart = System.currentTimeMillis()
                    MotionEvent.ACTION_UP -> {
                        val duration = System.currentTimeMillis() - longPressStart
                        if (duration >= 500) { // long press
                            showDrawingOverlay()
                        } else {
                            performClick()
                        }
                    }
                }
                return true
            }

            private fun performClick(): Boolean {
                overlayView.performClick()
                return true
            }
        })

        windowManager.addView(overlayView, params)
    }

    private fun showDrawingOverlay() {
        if (drawingOverlay != null) return // already showing

        val drawingView = DrawingView(this)

        // Create overlay layout to hold drawing and close button
        val overlayLayout = FrameLayout(this)
        overlayLayout.addView(drawingView)

        // Close button
        val closeButton = ImageView(this)
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        val closeParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            topMargin = 50
            rightMargin = 50
        }
        closeButton.layoutParams = closeParams
        closeButton.setOnClickListener { removeDrawingOverlay() }

        overlayLayout.addView(closeButton)

        // WindowManager params for full screen overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayLayout, params)
        drawingOverlay = overlayLayout
    }

    private fun removeDrawingOverlay() {
        drawingOverlay?.let { windowManager.removeView(it) }
        drawingOverlay = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) windowManager.removeView(overlayView)
        removeDrawingOverlay()
    }
}
