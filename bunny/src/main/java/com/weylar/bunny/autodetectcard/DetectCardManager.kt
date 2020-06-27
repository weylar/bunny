package com.weylar.bunny.autodetectcard

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.weylar.bunny.CardType
import com.weylar.bunny.autodetectcard.DetectCardManager.extractDate
import java.util.regex.Pattern


const val NOT_FOUND = "Not found"

object DetectCardManager {

    fun String.extractCardNumber(): String {
        val numberOnly = this.replace("\\D+", "")
        if (CardType.detect(numberOnly.removeSpace()) != CardType.UNKNOWN
            && (numberOnly.removeSpace().length == 16
                    || numberOnly.removeSpace().length == 18
                    || numberOnly.removeSpace().length == 19)
        ) {
            return numberOnly
        }
        return NOT_FOUND
    }

    fun String.extractDate(): String? {
        val regex = "^(0[1-9]|1[0-2])\\/\\d{2}\$"
        if (this.contains("/")) {
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(this)
            var result: String?
            if (matcher.find()) {
                result = matcher.group(0)
                return result
            }
        }
        return NOT_FOUND
    }


    private fun String.removeSpace() = this.replace(" ", "")


    fun String.isCardDate(): Boolean{
        val regex = "^(0[1-9]|1[0-2])\\/\\d{2}\$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(this)
        var result: String?
        if (matcher.find()) {
            result = matcher.group(0)
            return true
        }
        return false
    }

    fun String.isBankDate(): Boolean{
        val regex = "^\\d{4}-\\d{2}-\\d{2}\$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(this)
        var result: String?
        if (matcher.find()) {
            result = matcher.group(0)
            return true
        }
        return false
    }

    fun vibrateDevice(context: Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200,
                VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

}