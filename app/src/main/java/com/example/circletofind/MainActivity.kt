package com.example.circletofind

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var switchContainer: ConstraintLayout
    private lateinit var switchThumb: View
    private lateinit var switchOnText: TextView
    private lateinit var switchOffText: TextView
    private var isSwitchOn = false

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayService()
                setSwitchState(true)
            } else {
                setSwitchState(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchContainer = findViewById(R.id.customSwitchContainer)
        switchThumb = findViewById(R.id.switchThumb)
        switchOnText = findViewById(R.id.switchOnText)
        switchOffText = findViewById(R.id.switchOffText)

        switchContainer.setOnClickListener {
            if (isSwitchOn) {
                stopOverlayService()
                setSwitchState(false)
            } else {
                if (!Settings.canDrawOverlays(this)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    overlayPermissionLauncher.launch(intent)
                } else {
                    startOverlayService()
                    setSwitchState(true)
                }
            }
        }
    }

    private fun setSwitchState(on: Boolean) {
        isSwitchOn = on
        if (on) {
            switchContainer.setBackgroundResource(R.drawable.custom_switch_track_on)
            switchOnText.visibility = View.VISIBLE
            switchOffText.visibility = View.GONE
            (switchThumb.layoutParams as ConstraintLayout.LayoutParams).apply {
                startToStart = ConstraintLayout.LayoutParams.UNSET
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                marginEnd = 6
            }.also { switchThumb.layoutParams = it }
        } else {
            switchContainer.setBackgroundResource(R.drawable.custom_switch_track_off)
            switchOnText.visibility = View.GONE
            switchOffText.visibility = View.VISIBLE
            (switchThumb.layoutParams as ConstraintLayout.LayoutParams).apply {
                endToEnd = ConstraintLayout.LayoutParams.UNSET
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                marginStart = 6
            }.also { switchThumb.layoutParams = it }
        }
    }

    private fun startOverlayService() {
        val i = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i)
        } else {
            startService(i)
        }
    }

    private fun stopOverlayService() {
        val i = Intent(this, OverlayService::class.java)
        stopService(i)
    }
}
