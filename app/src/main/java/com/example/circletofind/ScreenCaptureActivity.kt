package com.example.circletofind

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.graphics.Bitmap
import android.media.ImageReader
import android.view.Display
import android.view.Surface
import android.hardware.display.VirtualDisplay
import android.view.WindowManager
import android.os.Environment
import java.io.FileOutputStream

class ScreenCaptureActivity : AppCompatActivity() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var imageReader: ImageReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED)
        val data = intent.getParcelableExtra<Intent>("data")

        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, data!!)

        captureScreen()
    }

    private fun captureScreen() {
        try {
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val display: Display = windowManager.defaultDisplay
            val width = display.width
            val height = display.height
            val density = resources.displayMetrics.densityDpi

            imageReader = ImageReader.newInstance(width, height, 0x1, 2)
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "screencap",
                width,
                height,
                density,
                Display.FLAG_SECURE,
                imageReader.surface,
                null,
                null
            )

            Thread.sleep(1000)

            val image = imageReader.acquireLatestImage()
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()

            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "capture.png")
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            openGoogleLens(file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Screen capture failed!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openGoogleLens(file: File) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setClassName("com.google.android.googlequicksearchbox", "com.google.android.apps.search.lens.LensActivity")
                data = android.net.Uri.fromFile(file)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
            Toast.makeText(this, "Opening Google Lens…", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Google Lens app not found!", Toast.LENGTH_SHORT).show()
        } finally {
            finish()
        }
    }
}
