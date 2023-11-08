@file:Suppress("DEPRECATION")

package com.simple.chris.pebble.functions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.simple.chris.pebble.functions.Values

object Vibration {

    fun notification(context: Context) {
        if (Values.settingVibration) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(VibrationEffect.createWaveform(Values.vibrationNotification, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun strongFeedback(context: Context) {
        if (Values.settingVibration) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(Values.vibrationStrong, -1)
        }
    }

    fun mediumFeedback(context: Context) {
        if (Values.settingVibration) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(Values.vibrationMedium, -1)
        }
    }

    fun lowFeedback(context: Context) {
        if (Values.settingVibration) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(Values.vibrationWeak, -1)
        }
    }
}