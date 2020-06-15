package com.weylar.bunny

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import java.util.regex.Pattern

/**
 * Contains all edit text formatter utility methods
 * @author weylar
 */
object EditTextFormatter {

    val CODE_PATTERN: Pattern = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+")


    /**
     * Formats edittext text content into MM/YY formats
     */
    fun EditText.formatEditTextExpiry(cardExpireText: TextView) {
        val tw = object : TextWatcher {

            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty() && editable.length % 3 === 0) {
                    val c: Char = editable[editable.length - 1]
                    if ('/' == c) {
                        editable.delete(editable.length - 1, editable.length)
                    }
                }
                if (editable.isNotEmpty() && editable.length % 3 === 0) {
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

    /**
     * Formats editText text content into **** **** ****
     */
    fun EditText.formatEditTextCardNumber(
        textView: TextView,
        cardTypeIcon: ImageView,
        cardTypeIconBackground: ImageView
    ) {
        val tw = object : TextWatcher {
            val space = ' '

            override fun afterTextChanged(s: Editable) {
                textView.setCardNumberText(s.toString())


                if (s.isNotEmpty() /*&& !CODE_PATTERN.matcher(s).matches()*/) {
                    val input: String = s.toString()
                    val numbersOnly: String = keepNumbersOnly(input)
                    val code: String = formatNumbersAsCode(numbersOnly)
                    this@formatEditTextCardNumber.removeTextChangedListener(this)
                    this@formatEditTextCardNumber.setText(code)
                    if (code.length % 5 == 0 || code.length == 24) {
                        this@formatEditTextCardNumber.setSelection(code.length - 1)
                    }else{
                        this@formatEditTextCardNumber.setSelection(code.length)
                    }
                    this@formatEditTextCardNumber.addTextChangedListener(this)


                    when (CardType.detect(s.toString().replace(" ", ""))) {
                        CardType.VERVE -> {
                            this@formatEditTextCardNumber
                                .setCompoundDrawablesWithIntrinsicBounds(
                                    context.getDrawable(R.drawable.ic_verve_logo_input),
                                    null,
                                    null,
                                    null
                                )
                            cardTypeIcon.setImageResource(R.drawable.ic_verve_logo)
                            cardTypeIconBackground.setImageResource(R.drawable.ic_verve_logo)
                        }
                        CardType.VISA -> {
                            this@formatEditTextCardNumber
                                .setCompoundDrawablesWithIntrinsicBounds(
                                    context.getDrawable(R.drawable.ic_visa_logo_input),
                                    null,
                                    null,
                                    null
                                )
                            cardTypeIcon.setImageResource(R.drawable.ic_visa_logo)
                            cardTypeIconBackground.setImageResource(R.drawable.ic_visa_logo)
                        }
                        CardType.MASTERCARD -> {
                            cardTypeIcon.setImageResource(R.drawable.ic_mastercard_logo)
                            cardTypeIconBackground.setImageResource(R.drawable.ic_mastercard_logo)
                            this@formatEditTextCardNumber
                                .setCompoundDrawablesWithIntrinsicBounds(
                                    context.getDrawable(R.drawable.ic_mastercard_logo_input),
                                    null,
                                    null,
                                    null
                                )
                        }

                        CardType.UNKNOWN -> {
                            cardTypeIcon.setImageResource(R.drawable.ic_paystack_logo)
                            cardTypeIconBackground.setImageResource(R.drawable.ic_paystack_logo)
                            this@formatEditTextCardNumber
                                .setCompoundDrawablesWithIntrinsicBounds(
                                    context.getDrawable(R.drawable.ic_paystack_logo_input),
                                    null,
                                    null,
                                    null
                                )
                        }
                        else -> {
                            cardTypeIcon.setImageResource(R.drawable.ic_paystack_logo)
                            cardTypeIconBackground.setImageResource(R.drawable.ic_paystack_logo)
                            this@formatEditTextCardNumber
                                .setCompoundDrawablesWithIntrinsicBounds(
                                    context.getDrawable(R.drawable.ic_paystack_logo_input),
                                    null,
                                    null,
                                    null
                                )
                        }
                    }

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
                    hash += "*"
                    if (x % 4 == 0) {
                        hash += " "
                    }
                }
            }
            this.text = data + hash
        }
    }