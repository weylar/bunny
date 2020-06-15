package com.weylar.paystackui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.weylar.bunny.PayView
import com.weylar.bunny.SCAN_REQUEST_CODE
import com.weylar.bunny.autodetectcard.CARD_EXPIRY_DATE
import com.weylar.bunny.autodetectcard.CARD_NUMBER

class MainActivity : AppCompatActivity() {
    private lateinit var payView: PayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        payView = findViewById(R.id.pay_view)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            SCAN_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    payView.setCardNumber(data!!.getStringExtra(CARD_NUMBER)!!)
                    payView.setCardExpiryDate(data.getStringExtra(CARD_EXPIRY_DATE)!!)

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}