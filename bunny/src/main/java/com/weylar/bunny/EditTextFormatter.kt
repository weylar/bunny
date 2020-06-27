package com.weylar.bunny

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

/**
 * Contains all edit text formatter utility methods
 * @author weylar
 */
object EditTextFormatter {


    /**
     * Formats edittext text content into MM/YY formats
     */
    fun EditText.formatEditTextExpiry(
        cardExpireText: TextView, editTextLayout: TextInputLayout
    ) {
        val tw = object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {
                //Remove previously set error message
                editTextLayout.error = null
                if (editable.isNotEmpty() && editable.length % 3 == 0) {
                    val c: Char = editable[editable.length - 1]
                    if ('/' == c) {
                        editable.delete(editable.length - 1, editable.length)
                    }
                }
                if (editable.isNotEmpty() && editable.length % 3 == 0) {
                    val c: Char = editable[editable.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(
                            editable.toString(),
                            "/"
                        ).size <= 2
                    ) {
                        if (editable.toString().substring(0, 2).toInt() > 12) {
                            editable.delete(1, 3)
                        } else editable.insert(editable.length - 1, "/")
                    }
                }

                cardExpireText.text = if (editable.isEmpty()) "MM/YY" else editable.toString()


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {

            }
        }
        this.addTextChangedListener(tw)
    }

    fun EditText.formatEditTextNameHolder(name: TextView) {
        val tw = object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {

                name.text = if (editable.isEmpty()) "NO CARD NAME" else editable.toString()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {

            }
        }
        this.addTextChangedListener(tw)


    }

    fun EditText.formatEditTextCVV(editTextLayout: TextInputLayout) {
        val tw = object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {
                //Remove previously set error message
                editTextLayout.error = null


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {

            }
        }
        this.addTextChangedListener(tw)


    }

    /**
     * Formats editText text content into **** **** ****
     */
    fun EditText.formatEditTextCardNumber(
        textView: TextView,
        cardTypeIcon: ImageView,
        cardTypeIconBackground: ImageView,
        editTextLayout: TextInputLayout
    ) {
        val tw = object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                textView.setCardNumberText(s.toString())


                if (s.isNotEmpty()) {
                    //Remove previously set error message
                    editTextLayout.error = null

                    val input: String = s.toString()
                    val numbersOnly: String = keepNumbersOnly(input)
                    val code: String = formatNumbersAsCode(numbersOnly)
                    this@formatEditTextCardNumber.removeTextChangedListener(this)
                    this@formatEditTextCardNumber.setText(code)
                    if (code.length % 5 == 0 || code.length == 24) {
                        this@formatEditTextCardNumber.setSelection(code.length - 1)
                    } else {
                        this@formatEditTextCardNumber.setSelection(code.length)
                    }
                    this@formatEditTextCardNumber.addTextChangedListener(this)


                    updateEditTextIcons(PayView.mIsCardCardEnabled, s, cardTypeIcon, cardTypeIconBackground)

                }
            }


            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                start: Int,
                removed: Int,
                added: Int
            ) {

            }
        }
        this.addTextChangedListener(tw)
    }

    private fun EditText.updateEditTextIcons(
        isCardCardEnabled: Boolean,
        s: Editable,
        cardTypeIcon: ImageView,
        cardTypeIconBackground: ImageView
    ) {
        when (CardType.detect(s.toString().replace(" ", ""))) {
            CardType.VERVE -> {
                val drawable = context.getDrawable(R.drawable.verve)
                drawable?.setBounds(0, 0, 90, 60)
                val cameraIcon = context.getDrawable(R.drawable.ic_baseline_camera_alt_24)
                cameraIcon?.setBounds(0, 0, 60, 60)
                this.setCompoundDrawables(
                        drawable, null,
                        if (isCardCardEnabled) cameraIcon else null,
                        null
                    )

                cardTypeIcon.setImageResource(R.drawable.ic_verve_logo)
                cardTypeIconBackground.setImageResource(R.drawable.ic_verve_logo)
            }
            CardType.VISA -> {
                val drawable = context.getDrawable(R.drawable.visa)
                drawable?.setBounds(0, 0, 90, 60)
                val cameraIcon = context.getDrawable(R.drawable.ic_baseline_camera_alt_24)
                cameraIcon?.setBounds(0, 0, 60, 60)
                this.setCompoundDrawables(
                        drawable, null,
                        if (isCardCardEnabled) cameraIcon else null,
                        null
                    )
                cardTypeIcon.setImageResource(R.drawable.ic_visa_logo)
                cardTypeIconBackground.setImageResource(R.drawable.ic_visa_logo)
            }
            CardType.MASTERCARD -> {
                cardTypeIcon.setImageResource(R.drawable.ic_mastercard_logo)
                cardTypeIconBackground.setImageResource(R.drawable.ic_mastercard_logo)
                val drawable = context.getDrawable(R.drawable.mastercard_40px)
                val cameraIcon = context.getDrawable(R.drawable.ic_baseline_camera_alt_24)
                cameraIcon?.setBounds(0, 0, 60, 60)
                drawable?.setBounds(0, 0, 70, 70)
                this.setCompoundDrawables(
                        drawable, null,
                        if (isCardCardEnabled) cameraIcon else null,
                        null
                    )
            }

            CardType.UNKNOWN -> {
                cardTypeIcon.setImageResource(R.drawable.ic_paystack_logo)
                cardTypeIconBackground.setImageResource(R.drawable.ic_paystack_logo)
                val drawable = context.getDrawable(R.drawable.credit_card_30px)
                val cameraIcon = context.getDrawable(R.drawable.ic_baseline_camera_alt_24)
                cameraIcon?.setBounds(0, 0, 60, 60)
                drawable?.setBounds(0, 0, 70, 70)
                this.setCompoundDrawables(
                        drawable, null,
                        if (isCardCardEnabled) cameraIcon else null,
                        null
                    )

            }
            else -> {
                cardTypeIcon.setImageResource(R.drawable.ic_paystack_logo)
                cardTypeIconBackground.setImageResource(R.drawable.ic_paystack_logo)
                val drawable = context.getDrawable(R.drawable.credit_card_30px)
                val cameraIcon = context.getDrawable(R.drawable.ic_baseline_camera_alt_24)
                cameraIcon?.setBounds(0, 0, 60, 60)
                drawable?.setBounds(0, 0, 70, 70)
                this.setCompoundDrawables(
                        drawable, null,
                        if (isCardCardEnabled) cameraIcon else null,
                        null
                    )
            }
        }
    }

    private fun keepNumbersOnly(s: CharSequence): String {
        return s.toString().replace("[^0-9]".toRegex(), "")
    }

    private fun formatNumbersAsCode(s: CharSequence): String {
        var groupDigits = 0
        var tmp = ""
        for ((i, element) in s.withIndex()) {
            tmp += element
            ++groupDigits
            if (groupDigits == 4 || i == 18) {
                tmp += " "
                groupDigits = 0
            }
        }
        return tmp
    }


    private fun TextView.setCardNumberText(data: String) {
        var hash = ""
        if (data.length < 19) {
            for (x in 1..19 - data.length) {
                hash += "X"
                if (x % 4 == 0) {
                    hash += " "
                }
            }
        }
        this.text = data + hash
    }


}