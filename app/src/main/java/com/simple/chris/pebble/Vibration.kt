@file:Suppress("DEPRECATION")

package com.simple.chris.pebble

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object Vibration {

    fun notification(context: Context) {
        if (Values.vibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(Values.notification, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(Values.notification, -1)
            }
        }
    }

    fun strongFeedback(context: Context) {
        if (Values.vibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(Values.strongVibration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(Values.notification, -1)
            }
        }
    }

    fun mediumFeedback(context: Context) {
        if (Values.vibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(Values.mediumVibration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(Values.notification, -1)
            }
        }
    }

    fun lowFeedback(context: Context) {
        if (Values.vibrations) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(Values.lowVibration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(Values.notification, -1)

            }
        }
    }
}