package com.weylar.bunny


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.graphics.ColorUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.weylar.bunny.EditTextFormatter.formatEditTextCVV
import com.weylar.bunny.EditTextFormatter.formatEditTextCardNumber
import com.weylar.bunny.EditTextFormatter.formatEditTextExpiry
import com.weylar.bunny.EditTextFormatter.formatEditTextNameHolder
import com.weylar.bunny.autodetectcard.DetectCardActivity
import com.weylar.bunny.autodetectcard.DetectCardManager.isBankDate
import com.weylar.bunny.autodetectcard.DetectCardManager.isCardDate
import com.weylar.bunny.data.BankPayViewData
import com.weylar.bunny.data.CardPayViewData
import kotlinx.android.synthetic.main.bunny_berry_bank.view.*
import kotlinx.android.synthetic.main.bunny_berry_card.view.*
import kotlinx.android.synthetic.main.bunny_berry_entry.view.*


const val SCAN_REQUEST_CODE = 300
const val MY_PREFS_NAME = "bunny"
const val CARD_DETAIL_LIST = "card_detail_list"
const val BANK_DETAIL_LIST = "bank_detail_list"
const val SEPARATOR = "&"
const val COLOR_ALPHA = 150


class PayView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : LinearLayout(mContext, attrs, defStyleAttr) {

    //TODO: Work on shared preference security
    //TODO: Write article on this
    //TODO: Create getters for all fields
    //TODO: Enable and disable bank or card payment
    /**
     * Gets the card number edit text view.
     */
    var cardNumberEditText = edit_text_card_number

    /**
     * Gets the card expiry date edit text view.
     */
    var cardExpiryEditText = edit_text_expiry_date

    /**
     * Gets card cvv edit text view.
     */
    var cardCVV = edit_text_security_code

    /**
     * Gets card holder name edit text view
     */
    var cardHolder = edit_text_name_on_card

    private var dialogView: View
    private var dialog: BottomSheetDialog

    private var bankSpinnerData = listOf(context.getString(R.string.bank_spinner_placeholder))
    private var bankAdapter: ArrayAdapter<String>


    companion object {
        /**
         * Should card scan option be enabled. This is true by default.
         */
        var mIsCardCardEnabled = true

        /**
         * Activity of the calling view
         */
        var mActivity: Activity? = null
    }


    init {

        mContext.setTheme(R.style.BunnyTheme)
        inflate(mContext, R.layout.bunny_berry_entry, this)
        dialogView = LayoutInflater.from(mContext).inflate(R.layout.loading_spinner_layout, null)
        dialog = BottomSheetDialog(mContext)
        dialog.setContentView(dialogView)
        setUpCustomAttributes(attrs)

        bankAdapter = ArrayAdapter(context, R.layout.bank_spinner_layout, bankSpinnerData)
        bank_spinner.adapter = bankAdapter

        formatExpiryEditTextContent()
        viewClick()


    }

    /**
     * Set list of bank item on bank spinner view. To get this, you have to explicitly fetch the
     * list of banks from paystack API here [https://api.paystack.co/bank?gateway=emandate&pay_with_bank=true],
     * then call this method passing in the value.
     *
     * @param banks List of bank names.
     */
    fun setBanks(banks: List<String>) {
        bankAdapter = ArrayAdapter(context, R.layout.bank_spinner_layout, banks)
        bank_spinner.adapter = bankAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setUpCustomAttributes(attrs: AttributeSet?) {
        val ta = mContext.obtainStyledAttributes(
            attrs,
            R.styleable.PayView, 0, 0
        )
        try {
            setTheme(
                ta.getColor(
                    R.styleable.PayView_bunnyTheme,
                    ActivityCompat.getColor(mContext, R.color.primary)
                )
            )

            if (ta.getColor(
                    R.styleable.PayView_bunnyTheme,
                    ActivityCompat.getColor(mContext, R.color.primary)
                )
                == ActivityCompat.getColor(mContext, R.color.primary)
            ) {

                text_view_amount_card.setTextColor(
                    ta.getColor(
                        R.styleable.PayView_amountColor,
                        ActivityCompat.getColor(mContext, R.color.primary)
                    )
                )

                text_view_amount_bank.setTextColor(
                    ta.getColor(
                        R.styleable.PayView_amountColor,
                        ActivityCompat.getColor(mContext, R.color.primary)
                    )
                )

                pay_button_card.setTextColor(
                    ta.getColor(
                        R.styleable.PayView_payButtonTextColor,
                        ActivityCompat.getColor(mContext, R.color.white)
                    )
                )

                pay_button_card.background = ta.getDrawable(R.styleable.PayView_payButtonBackground)
                    ?: ActivityCompat.getDrawable(mContext, R.drawable.button_bg)


                card_root_layout.background =
                    ta.getDrawable(R.styleable.PayView_cardBackground)
                        ?: ActivityCompat.getDrawable(
                            mContext,
                            R.drawable.card_bg
                        )


                card_header.setTextColor(
                    ColorUtils.setAlphaComponent(
                        ta.getColor(
                            R.styleable.PayView_cardContentColor,
                            ActivityCompat.getColor(mContext, R.color.white)
                        ), COLOR_ALPHA
                    )
                )

                card_number_description.setTextColor(
                    ColorUtils.setAlphaComponent(
                        ta.getColor(
                            R.styleable.PayView_cardContentColor,
                            ActivityCompat.getColor(mContext, R.color.white)
                        ), COLOR_ALPHA
                    )
                )




                card_digit.setTextColor(
                    ta.getColor(
                        R.styleable.PayView_cardContentColor,
                        ActivityCompat.getColor(mContext, R.color.white)
                    )
                )

                card_holder_description.setTextColor(
                    ColorUtils.setAlphaComponent(
                        ta.getColor(
                            R.styleable.PayView_cardContentColor,
                            ActivityCompat.getColor(mContext, R.color.white)
                        ), COLOR_ALPHA
                    )
                )

                card_name_holder.setTextColor(
                    ta.getColor(
                        R.styleable.PayView_cardContentColor,
                        ActivityCompat.getColor(mContext, R.color.white)
                    )
                )


                card_expiry_description.setTextColor(
                    ColorUtils.setAlphaComponent(
                        ta.getColor(
                            R.styleable.PayView_cardContentColor,
                            ActivityCompat.getColor(mContext, R.color.white)
                        ), COLOR_ALPHA
                    )
                )

                card_expiry.setTextColor(
                    ta.getColor(
                        R.styleable.PayView_cardContentColor,
                        ActivityCompat.getColor(mContext, R.color.white)
                    )
                )


            }



            pay_button_card.text =
                ta.getString(R.styleable.PayView_payButtonText) ?: resources.getString(R.string.pay)

            amount_description_text_view.text = ta.getString(R.styleable.PayView_amountDescription)
                ?: resources.getString(R.string.amount_description)

            amount_description_bank_text_view.text =
                ta.getString(R.styleable.PayView_amountDescription)
                    ?: resources.getString(R.string.amount_description)


            text_view_amount_card.text = "\u20A6" + ta.getFloat(
                R.styleable.PayView_amount,
                0F
            ).toString()

            text_view_amount_bank.text = "\u20A6" + ta.getFloat(
                R.styleable.PayView_amount,
                0F
            ).toString()


        } finally {
            ta.recycle()
        }
    }


    private fun isColorDark(color: Int): Boolean {
        val darkness: Double =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
                color
            )) / 255
        return darkness >= 0.5
    }

    private fun setTheme(color: Int) {
        entry_header_underline.setBackgroundColor(color)
        text_view_amount_card.setTextColor(color)
        text_view_amount_bank.setTextColor(color)
        card_root_layout.setBackgroundColor(color)
        pay_button_card.setBackgroundColor(color)
        pay_button_bank.setBackgroundColor(color)
        dialogView.findViewById<ProgressBar>(R.id.progress_bar).indeterminateDrawable
            .setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        if (isColorDark(color)) {
            setCardContentColor(Color.WHITE)
        } else {
            setCardContentColor(Color.BLACK)
        }

    }

    /**
     * A quick way to set a theme for the entire view. It sets colors and it background of all
     * customisable view.
     * If you set this, you don't need to set view colors explicitly, since it can be inferred from
     * the theme by alternating between black and white based on the theme color set..
     *
     * @param color Theme color to be used on all views.
     */
    fun setBunnyTheme(color:Int){
        setTheme(color)
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

    /**
     * Sets payment amount to be displays on the page.
     *@param amount to be paid.
     */
    @SuppressLint("SetTextI18n")
    fun setAmount(amount: Float) {
        text_view_amount_card.text = "\u20A6" + amount.toString()
        text_view_amount_bank.text = "\u20A6" + amount.toString()
    }


    /**
     * Set the background to a given Drawable or Color, or remove the background on the pay button.
     * By default it picks up your theme color.
     *
     * @param background The Drawable to use as the background.
     */
    fun setPayButtonBackground(background: Drawable?) {
        pay_button_card.background = background

    }


    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     * The default color is white.
     *
     * @param color
     */
    fun setPayButtonTextColor(color: Int) {
        pay_button_card.setTextColor(color)
    }

    /**
     * Sets the string value of the pay button. Pay button does not accept HTML-like formatting,
     * which you can do with text strings in XML resource files. To style your strings,
     * attach android.text.style.* objects to a SpannableString.
     *
     * @param text text to be displayed.
     */
    fun setPayButtonText(text: String) {
        pay_button_card.text = text
    }

    /**
     * Set the background to a given Drawable or Color, or remove the background on the card.
     * By default it picks up your theme color.
     *
     * @param background The Drawable to use as the background.
     */
    fun setCardBackground(background: Drawable?) {
        card_root_layout.background = background
    }

    /**
     * Sets all view color inside the card component to the given color.
     *
     * @param color Color to be used.
     */
    fun setCardContentColor(color: Int) {

        card_header.setTextColor(color)
        card_small_header.setTextColor(color)

        card_number_description.setTextColor(ColorUtils.setAlphaComponent(color, COLOR_ALPHA))
        card_digit.setTextColor(color)


        card_holder_description.setTextColor(ColorUtils.setAlphaComponent(color, COLOR_ALPHA))
        card_name_holder.setTextColor(color)

        card_expiry_description.setTextColor(ColorUtils.setAlphaComponent(color, COLOR_ALPHA))
        card_expiry.setTextColor(color)

    }

    /**
     * Sets text color on amount.
     *
     * @param color Color to be used.
     */
    fun setAmountColor(color: Int) {
        text_view_amount_card.setTextColor(color)
        text_view_amount_bank.setTextColor(color)
    }


    private fun formatExpiryEditTextContent() {

        edit_text_expiry_date.formatEditTextExpiry(card_expiry, edit_text_expiry_date_layout)
        edit_text_name_on_card.formatEditTextNameHolder(card_name_holder)
        edit_text_security_code.formatEditTextCVV(edit_text_security_code_layout)
        edit_text_card_number.formatEditTextCardNumber(
            card_digit,
            card_type_image,
            card_type_background,
            edit_text_card_number_layout
        )


    }

    fun enableCardScan(activity: Activity, isEnabled: Boolean) {
        mActivity = activity
        mIsCardCardEnabled = isEnabled
        val drawable = context.getDrawable(R.drawable.credit_card_30px)
        val cameraIcon = context.getDrawable(R.drawable.ic_baseline_camera_alt_24)
        cameraIcon?.setBounds(0, 0, 60, 60)
        drawable?.setBounds(0, 0, 70, 70)
        edit_text_card_number.setCompoundDrawables(
            drawable, null,
            if (mIsCardCardEnabled) cameraIcon else null,
            null
        )

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun viewClick() {
        pay_button_alternative_bank.setOnClickListener {
            view_flipper.displayedChild = 1
        }

        pay_button_alternative_card.setOnClickListener {
            view_flipper.displayedChild = 2
        }
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

    private fun saveCardState() {
        val sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val set = sharedPreferences.getStringSet(CARD_DETAIL_LIST, null)
        val newSet = mutableSetOf<String>()
        val value = edit_text_card_number.text.toString() +
                SEPARATOR + edit_text_expiry_date.text.toString() +
                SEPARATOR + edit_text_security_code.text.toString() +
                SEPARATOR + edit_text_name_on_card.text.toString()

        set?.let { newSet.addAll(set) }
        newSet.add(value)
        editor.putStringSet(CARD_DETAIL_LIST, newSet)
        editor.apply()


    }

    private fun saveBankState() {
        val sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val set = sharedPreferences.getStringSet(BANK_DETAIL_LIST, null)
        val newSet = mutableSetOf<String>()
        val value = bank_spinner.selectedItem.toString() +
                SEPARATOR + edit_text_account_number.text.toString() +
                SEPARATOR + edit_text_dob.text.toString()

        set?.let { newSet.addAll(set) }
        newSet.add(value)
        editor.putStringSet(BANK_DETAIL_LIST, newSet)
        editor.apply()


    }

    private fun validateCardInputs(): Boolean {
        when {
            edit_text_card_number.text.isNullOrBlank() -> {
                edit_text_card_number_layout.error =
                    context.getString(R.string.card_number_empty_error)
                edit_text_card_number.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            CardType.detect(
                edit_text_card_number.text.toString().replace(" ", "")
            ) == CardType.UNKNOWN -> {
                edit_text_card_number_layout.error =
                    context.getString(R.string.invalid_card_number_error)
                edit_text_card_number.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            else -> {
                edit_text_card_number_layout.error = null
            }
        }

        if (edit_text_expiry_date.text.isNullOrBlank()) {
            edit_text_expiry_date_layout.error = context.getString(R.string.expiry_empty_error)
            edit_text_expiry_date.background.mutate().setColorFilter(
                ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
            )
            return false
        } else if (!edit_text_expiry_date.text.toString().isCardDate()) {
            edit_text_expiry_date_layout.error = context.getString(R.string.expire_invalid_error)
            edit_text_expiry_date.background.mutate().setColorFilter(
                ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
            )
            return false
        } else {
            edit_text_expiry_date_layout.error = null
        }

        when {
            edit_text_security_code.text.isNullOrBlank() -> {
                edit_text_security_code_layout.error = context.getString(R.string.cvv_empty_error)
                edit_text_security_code.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            edit_text_security_code.length() < 3 -> {
                edit_text_security_code_layout.error =
                    context.getString(R.string.cvv_less_digit_error)
                edit_text_security_code.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            else -> {
                edit_text_security_code_layout.error = null
            }
        }
        return true
    }

    private fun validateBankInputs(): Boolean {
        if (bank_spinner.selectedItem.toString() == context.getString(R.string.bank_spinner_placeholder)) {
            val snack = Snackbar.make(
                root,
                context.getString(R.string.select_bank_error_message),
                Snackbar.LENGTH_LONG
            )
            snack.setAction("Ok"){
                snack.dismiss()
            }
            snack.setActionTextColor(Color.WHITE)
            snack.show()
            return false
        }

        when {
            edit_text_account_number.text.isNullOrBlank() -> {
                edit_text_account_number_layout.error =
                    context.getString(R.string.account_number_invalid_error)
                edit_text_account_number.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            edit_text_account_number.text.toString().length < 10 -> {
                edit_text_account_number_layout.error =
                    context.getString(R.string.acccount_number_less_error)
                edit_text_account_number.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            else -> {
                edit_text_account_number_layout.error = null
            }
        }

        when {
            edit_text_dob.text.isNullOrBlank() -> {
                edit_text_dob_layout.error = context.getString(R.string.dob_invalid_error)
                edit_text_dob.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            !edit_text_dob.text.toString().isBankDate() -> {
                edit_text_dob_layout.error = context.getString(R.string.dob_less_error)
                edit_text_dob.background.mutate().setColorFilter(
                    ActivityCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP
                )
                return false
            }
            else -> {
                edit_text_dob_layout.error = null
            }
        }
        return true
    }

    private fun initiateCardDetection() {
        mActivity?.let {
            startActivityForResult(
                it,
                Intent(mContext, DetectCardActivity::class.java), SCAN_REQUEST_CODE, null
            )
        }

    }

    private fun splitExpiryDate(date: String) =
        Pair(date.split("/")[0], date.split("/")[1])


    /**
     * Register a callback to be invoked when the pay button is clicked.
     * If one or more field is considered invalid, this callback won't be triggered
     * and appropriate error message will be displayed to user.
     *
     * @param onPayListener Callback to be invoked when user clicks on pay button.
     */
    fun onPayClickListener(onPayListener: OnPayListener) {

        pay_button_card.setOnClickListener {

            it.startAnimation(AlphaAnimation(1f, 0.6f))

            if (validateCardInputs()) {
                if (save_card_detail_switch.isChecked) saveCardState()
                val date = splitExpiryDate(edit_text_expiry_date.text.toString())
                val payDataView = CardPayViewData(
                    cardNumber = edit_text_card_number.text.toString().replace(" ", ""),
                    cardExpiryMonth = date.first.toInt(),
                    cardExpiryYear = date.second.toInt(),
                    cardHolderName = edit_text_name_on_card.text.toString(),
                    cardCVV = edit_text_security_code.text.toString()
                )

                onPayListener.onCardPayListener(payDataView)
            }
        }

        pay_button_bank.setOnClickListener {

            it.startAnimation(AlphaAnimation(1f, 0.6f))

            if (validateBankInputs()) {
                if (save_bank_detail_switch.isChecked) saveBankState()
                val bankPayDataView = BankPayViewData(
                    bankName = bank_spinner.selectedItem.toString(),
                    accountNumber = edit_text_account_number.text.toString(),
                    dateOfBirth = edit_text_dob.text.toString()
                )

                onPayListener.onBankPayListener(bankPayDataView)
            }
        }


        // Trigger when user selects pay with card option from the first view.
        pay_with_card.setOnClickListener {
            view_flipper.inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            view_flipper.outAnimation =
                AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
            view_flipper.displayedChild = 1

        }

        show_saved_cards.setOnClickListener {
            showPreviousCards(onPayListener)
        }

        // Trigger when user selects pay with bank option from the first view.
        pay_with_bank.setOnClickListener {
            view_flipper.inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            view_flipper.outAnimation =
                AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
            view_flipper.displayedChild = 2
        }

        show_saved_banks.setOnClickListener {
            showPreviousBanks(onPayListener)
        }

    }

    private fun showPreviousCards(onPayListener: OnPayListener) {
        val sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.getStringSet(CARD_DETAIL_LIST, null)?.let {
            val dialogView =
                LayoutInflater.from(mContext).inflate(R.layout.previous_cards_layout, null)
            val dialog = BottomSheetDialog(mContext)
            dialog.setContentView(dialogView)
            dialog.setCancelable(true)
            val root = dialogView.findViewById<LinearLayout>(R.id.previous_card_layout_root)

            it.map { content ->
                val values = content.split(SEPARATOR)
                val childView = LayoutInflater.from(mContext)
                    .inflate(R.layout.previous_cards_child_layout, root, false)

                childView.setOnClickListener {
                    val payDataView = CardPayViewData(
                        cardNumber = values[0].replace(" ", ""),
                        cardExpiryMonth = values[1].split("/")[0].toInt(),
                        cardExpiryYear = values[1].split("/")[1].toInt(),
                        cardCVV = values[2],
                        cardHolderName = values[3]
                    )

                    onPayListener.onCardPayListener(payDataView)
                    dialog.dismiss()

                }


                childView.findViewById<TextView>(R.id.card_number).text =
                    values[0].formatPreviousCardNumber()

                determineCardTypeToDisplay(values, childView)
                root.addView(childView)
            }

            dialog.show()
        }
    }

    private fun showPreviousBanks(onPayListener: OnPayListener) {
        val sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.getStringSet(BANK_DETAIL_LIST, null)?.let {
            val dialogView =
                LayoutInflater.from(mContext).inflate(R.layout.previous_cards_layout, null)
            val dialog = BottomSheetDialog(mContext)
            dialog.setContentView(dialogView)
            dialog.setCancelable(true)
            val root = dialogView.findViewById<LinearLayout>(R.id.previous_card_layout_root)
            val header = dialogView.findViewById<TextView>(R.id.previous_header)
            header.text = context.getString(R.string.saved_payment_banks)

            it.map { content ->
                val values = content.split(SEPARATOR)
                val childView = LayoutInflater.from(mContext)
                    .inflate(R.layout.previous_cards_child_layout, root, false)

                childView.setOnClickListener {
                    val payDataView = BankPayViewData(
                        bankName = values[0],
                        accountNumber = values[1],
                        dateOfBirth = values[2]
                    )

                    onPayListener.onBankPayListener(payDataView)
                    dialog.dismiss()

                }


                childView.findViewById<ImageView>(R.id.card_type_image).setImageResource(R.drawable.ic_bank_svgrepo_com)
                childView.findViewById<TextView>(R.id.card_number).text =
                    values[1].formatPreviousCardNumber()

                childView.findViewById<TextView>(R.id.card_type).text = values[0]
                root.addView(childView)
            }

            dialog.show()
        }
    }

    private fun determineCardTypeToDisplay(
        values: List<String>,
        childView: View
    ) {
        when (CardType.detect(values[0].replace(" ", ""))) {
            CardType.MASTERCARD -> {
                childView.findViewById<TextView>(R.id.card_type).text =
                    context.getString(R.string.master_card)
                childView.findViewById<ImageView>(R.id.card_type_image)
                    .setImageResource(R.drawable.mastercard_40px)
            }

            CardType.VERVE -> {
                childView.findViewById<TextView>(R.id.card_type).text =
                    context.getString(R.string.verve)
                childView.findViewById<ImageView>(R.id.card_type_image)
                    .setImageResource(R.drawable.verve)
            }

            CardType.VISA -> {
                childView.findViewById<TextView>(R.id.card_type).text =
                    context.getString(R.string.visa)
                childView.findViewById<ImageView>(R.id.card_type_image)
                    .setImageResource(R.drawable.visa)

            }

            else ->
                childView.findViewById<ImageView>(R.id.card_type_image)
                    .setImageResource(R.drawable.credit_card_30px)
        }
    }

    /**
     * Displays an adaptive loading progress, used to indicate pay progress.
     * @param cancellable Determine if progress is cancellable. Default value is false.
     *
     */
    fun showLoadingSpinner(cancellable: Boolean = false) {
        dialog.setCancelable(cancellable)
        dialog.show()
    }


    /**
     * Dismiss the loading progress.
     *
     * @param animation Sets whether or not to animate while dismissing progress. Default value is true
     */
    fun dismissLoadingSpinner(animation: Boolean = true) {
        dialog.dismissWithAnimation = animation
        dialog.dismiss()
    }


    private fun String.formatPreviousCardNumber(): String {
        var star = this.substring(0, 5)
        for (i in 0..this.replace(" ", "").length - 4) {
            star += if (this[i].toString().isNotBlank()) {
                "*"
            } else {
                " "
            }
        }
        return star
    }


    interface OnPayListener {
        fun onBankPayListener(bankPayViewData: BankPayViewData)
        fun onCardPayListener(cardPayDataView: CardPayViewData)
    }

}