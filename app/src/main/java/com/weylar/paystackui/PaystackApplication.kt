package com.weylar.paystackui

import android.app.Application
import co.paystack.android.PaystackSdk


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PaystackSdk.initialize(applicationContext)
    }
}
