package com.weylar.paystackui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.weylar.bunny.PayView
import com.weylar.bunny.SCAN_REQUEST_CODE
import com.weylar.bunny.autodetectcard.CARD_EXPIRY_DATE
import com.weylar.bunny.autodetectcard.CARD_NUMBER
import com.weylar.bunny.data.BankPayViewData
import com.weylar.bunny.data.CardPayViewData

class MainActivity : AppCompatActivity() {
    private lateinit var payView: PayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        payView = findViewById(R.id.pay_view)
       // payView.enableCardScan(this, true)
       // payView.setBunnyTheme(Color.BLACK)
       // payView.setBanks(listOf("Access", "Uba"))
        // payView.setAmount(100.0f)
       //  payView.setAmountColor(R.color.black)
        // payView.setPayButtonBackground(ActivityCompat.getDrawable(this, R.drawable.button_bg))
        // payView.setPayButtonTextColor(Color.YELLOW)
        // payView.setPayButtonText("Hello")
        // payView.setCardBackground(ActivityCompat.getDrawable(this, R.drawable.card_bg))
        // payView.setCardContentColor(ActivityCompat.getColor(this, R.color.white))
        // payView.enableDetailSave(false)


        payView.onPayClickListener(object : PayView.OnPayListener {
            override fun onBankPayListener(bankPayViewData: BankPayViewData) {
                Log.i("Aminu", bankPayViewData.accountNumber)
                Log.i("Aminu", bankPayViewData.bankName)
                Log.i("Aminu", bankPayViewData.dateOfBirth)
            }

            override fun onCardPayListener(cardPayDataView: CardPayViewData) {
                val card = Card(
                    cardPayDataView.cardNumber, cardPayDataView.cardExpiryMonth,
                    cardPayDataView.cardExpiryYear, cardPayDataView.cardCVV
                )
                if (card.isValid) {
                    payView.showLoadingSpinner()
                    val charge = Charge()
                    charge.card = card
                    charge.amount = 200
                    charge.email = "idrisaminu861@gmail.com"

                    PaystackSdk.chargeCard(
                        this@MainActivity,
                        charge,
                        object : Paystack.TransactionCallback {
                            override fun onSuccess(transaction: Transaction?) {
                                Log.i("Aminu", "Successful transaction")
                                payView.hideLoadingSpinner()
                            }

                            override fun beforeValidate(transaction: Transaction?) {
                                Log.i("Aminu", "Before validate is called")
                            }

                            override fun onError(error: Throwable?, transaction: Transaction?) {
                                Log.i("Aminu", "Error: " + error?.localizedMessage)
                                payView.hideLoadingSpinner()
                            }

                        })
                } else {
                    Log.i("Aminu", "It's not valid")
                }

            }
        })


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            SCAN_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    payView.setCardNumber(data!!.getStringExtra(CARD_NUMBER))
                    payView.setCardExpiryDate(data.getStringExtra(CARD_EXPIRY_DATE)!!)


                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}