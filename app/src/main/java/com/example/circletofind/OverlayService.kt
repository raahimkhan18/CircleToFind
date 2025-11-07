package com.example.circletofind

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private var isLongPressed = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = layoutInflater.inflate(R.layout.overlay_gesture_bar, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            150,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.CENTER

        val handler = Handler()
        val longPressRunnable = Runnable {
            isLongPressed = true
            val intent = Intent(this, CastDialogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        overlayView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLongPressed = false
                    handler.postDelayed(longPressRunnable, 800) // long press 0.8 sec
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                }
            }
            true
        }

        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) windowManager.removeView(overlayView)
    }
}
