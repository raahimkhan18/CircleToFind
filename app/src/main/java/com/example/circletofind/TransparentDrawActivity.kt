package com.example.circletofind

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt

class TransparentDrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the window itself transparent
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        // Optional: remove title bar
        supportActionBar?.hide()

        // Create a full-screen container
        val overlay = FrameLayout(this).apply {
            // Semi-transparent black background
            setBackgroundColor("#80000000".toColorInt()) // 50% black
        }

        // Add your drawing view
        val drawingView = DrawingView(this)
        overlay.addView(drawingView)

        // Set the overlay as content
        setContentView(overlay)
    }
}
