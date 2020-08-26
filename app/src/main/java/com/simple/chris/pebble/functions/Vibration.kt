@file:Suppress("DEPRECATION")

package com.simple.chris.pebble.functions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.simple.chris.pebble.functions.Values

object Vibration {

    fun notification(context: Context) {
        if (Values.settingVibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(Values.notificationPattern, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(Values.notificationPattern, -1)
            }
        }
    }

    fun strongFeedback(context: Context) {
        if (Values.settingVibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(Values.strongVibration, -1)
        }
    }

    fun mediumFeedback(context: Context) {
        if (Values.settingVibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(Values.mediumVibration, -1)
        }
    }

    fun lowFeedback(context: Context) {
        if (Values.settingVibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(Values.weakVibration, -1)
        }
    }
}