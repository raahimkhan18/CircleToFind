package com.example.circletofind

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CastDialogActivity : AppCompatActivity() {

    private lateinit var projectionManager: MediaProjectionManager
    private val REQUEST_SCREEN_CAPTURE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_SCREEN_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCREEN_CAPTURE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val intent = Intent(this, ScreenCaptureActivity::class.java)
                intent.putExtra("resultCode", resultCode)
                intent.putExtra("data", data)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Screen capture failed!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
