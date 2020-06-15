package com.weylar.bunny


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.weylar.bunny.EditTextFormatter.formatEditTextCardNumber
import com.weylar.bunny.EditTextFormatter.formatEditTextExpiry
import com.weylar.bunny.EditTextFormatter.formatEditTextNameHolder
import com.weylar.bunny.autodetectcard.DetectCardActivity
import kotlinx.android.synthetic.main.bunny_berry.view.*
import kotlinx.android.synthetic.main.cvv_helper_layout.view.*


const val SCAN_REQUEST_CODE = 300
const val MY_PREFS_NAME = "bunny"
const val IS_CARD_SAVED = "is_card_saved"

class PayView(private val mContext: Context, private val attributeSet: AttributeSet) :
    LinearLayout(mContext, attributeSet) {



    constructor(mContext: Context, attributeSet: AttributeSet, defStyle: Int) : this(mContext, attributeSet) {
        LinearLayout(mContext, attributeSet, defStyle)

    }


    private var sheetBehavior: BottomSheetBehavior<RelativeLayout>


    init {

        mContext.setTheme(R.style.BunnyTheme)

        inflate(mContext, R.layout.bunny_berry, this)
        sheetBehavior = BottomSheetBehavior.from(cvv_bottom_sheet)

        formatExpiryEditTextContent()
        viewClick()


    }

    /**
     * Sets card number
     * @param cardNumber Card number
     */
    fun setCardNumber(cardNumber: String) {
        edit_text_card_number.setText(cardNumber)

    }

    /**
     * Sets card expiry date
     * @param cardExpiry Card expiry
     */
    fun setCardExpiryDate(cardExpiry: String) {
        edit_text_expiry_date.setText(cardExpiry)
    }

    /**
     * Sets card holder name
     * @param holderName Card holder name
     */
    fun setCardHolderName(holderName: String) {
        edit_text_name_on_card.setText(holderName)
    }

    /**
     * Sets card CVV
     * @param cvv Card CVV
     */
    fun setCardCVV(cvv: String) {
        edit_text_security_code.setText(cvv)
    }


    private fun checkPreviousCard() {
        save_card_detail_switch.setOnCheckedChangeListener { _, isChecked ->
            val editor: Editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
            editor.putBoolean(IS_CARD_SAVED, isChecked)
            editor.apply()
        }
    }

    private fun formatExpiryEditTextContent() {

        edit_text_expiry_date.formatEditTextExpiry(card_expiry)
        edit_text_name_on_card.formatEditTextNameHolder(card_name_holder)
        edit_text_card_number.formatEditTextCardNumber(
            card_digit,
            card_type_image,
            card_type_background
        )

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun viewClick() {

        cvv_title.setOnClickListener {
            val dialogView: View =
                LayoutInflater.from(mContext).inflate(R.layout.cvv_helper_layout, null)
            val dialog = BottomSheetDialog(mContext)
            dialog.setContentView(dialogView)
            dialog.show()
        }

        edit_text_card_number.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                edit_text_card_number.compoundDrawables[drawableRight]?.let {
                    if (event.rawX >= edit_text_card_number.right -
                        edit_text_card_number.compoundDrawables[drawableRight].bounds.width()
                    ) {
                        initiateCardDetection()
                        return@OnTouchListener true
                    }
                }

            }
            false
        })
    }


    private fun initiateCardDetection() {
        (mContext as Activity).startActivityForResult(
            Intent(
                mContext,
                DetectCardActivity::class.java
            ), SCAN_REQUEST_CODE
        )

    }


}