package com.weylar.bunny.data

data class CardPayViewData(
    val cardNumber: String,
    val cardExpiryMonth: Int,
    val cardExpiryYear: Int,
    val cardHolderName: String,
    val cardCVV: String

)