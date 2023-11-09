package com.simple.chris.pebble.functions

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

fun vibrateWeak(context: Context) {
    if (Values.settingVibration) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(Values.vibrationWeak, -1)
    }
}

fun vibrateMedium(context: Context) {
    if (Values.settingVibration) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(Values.vibrationMedium, -1)
    }
}

fun vibrateStrong(context: Context) {
    if (Values.settingVibration) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(Values.vibrationStrong, -1)
    }
}

fun vibrateNotification(context: Context) {
    if (Values.settingVibration) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createWaveform(Values.vibrationNotification, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}