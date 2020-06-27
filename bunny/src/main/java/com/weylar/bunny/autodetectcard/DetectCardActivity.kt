package com.weylar.bunny.autodetectcard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import androidx.core.util.valueIterator
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.weylar.bunny.R
import com.weylar.bunny.autodetectcard.DetectCardManager.extractCardNumber
import com.weylar.bunny.autodetectcard.DetectCardManager.extractDate
import kotlinx.android.synthetic.main.activity_detect_card.*
import java.io.IOException
import java.util.*


const val REQUEST_CAMERA_PERMISSION_ID = 100
const val CARD_NUMBER = "card_number"
const val CARD_EXPIRY_DATE = "card_expiry_date"

class DetectCardActivity : AppCompatActivity() {


    private lateinit var cameraSource: CameraSource
    private var isCardNumberDetected = false
    private var isCardDateDetected = false
    private var cardNumberDetectedResult = ""
    private var cardDateDetectedResult = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_card)

        dismissCenterText()

        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        if (textRecognizer.isOperational) {
            cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(0.2f)
                .setAutoFocusEnabled(true)
                .build()

            surface_view.holder.addCallback(object : Callback {
                override fun surfaceChanged(
                    holder: SurfaceHolder?,
                    format: Int,
                    width: Int,
                    height: Int
                ) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                    cameraSource.stop()
                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    try {

                        if (ActivityCompat.checkSelfPermission(
                                this@DetectCardActivity,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@DetectCardActivity,
                                arrayOf(Manifest.permission.CAMERA),
                                REQUEST_CAMERA_PERMISSION_ID
                            )

                            return
                        }
                        cameraSource.start(surface_view.holder)
                    } catch (exception: IOException) {
                        exception.printStackTrace()
                    }
                }
            })

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {}

                override fun receiveDetections(detector: Detector.Detections<TextBlock>) {
                    val item = detector.detectedItems
                    if (item.isNotEmpty()) {
                        for (textBlock in item.valueIterator()) {
                            val text = textBlock.value
                            if (text.extractCardNumber() != NOT_FOUND) {
                                isCardNumberDetected = true
                                cardNumberDetectedResult = text.extractCardNumber()
                                break
                            }
                        }

                        for (textBlock in item.valueIterator()) {
                            val text = textBlock.value
                            if (text.extractDate() != NOT_FOUND) {
                                isCardDateDetected = true
                                cardDateDetectedResult = text.extractDate()!!
                                break
                            }
                        }


                    }

                    if (isCardNumberDetected && isCardDateDetected) {
                        cardCapturedViewChange(true)
                        DetectCardManager.vibrateDevice(this@DetectCardActivity)
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                runOnUiThread {
                                    val resultIntent = Intent()
                                    resultIntent.putExtra(CARD_NUMBER, cardNumberDetectedResult)
                                    resultIntent.putExtra(CARD_EXPIRY_DATE, cardDateDetectedResult)
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                }

                            }
                        }, 1500, 1000)

                    }
                }

            })
        }


    }

    private fun cardCapturedViewChange(value: Boolean) {
        runOnUiThread {
            if (value) success_view.visibility = View.VISIBLE
            else success_view.visibility = View.GONE
        }

    }


    private fun dismissCenterText() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    center_text.visibility = View.GONE
                    this.cancel()
                }

            }
        }, 3000, 1000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION_ID -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                            this@DetectCardActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        return
                    }
                    try {
                        cameraSource.start(surface_view.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}




