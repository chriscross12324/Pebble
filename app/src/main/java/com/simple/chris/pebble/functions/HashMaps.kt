package com.simple.chris.pebble.functions

import com.simple.chris.pebble.R

object HashMaps {

    /**
     * Browse Menu
     */
    fun browseMenuArray() : ArrayList<HashMap<String, Int>> {
        val menuArray = ArrayList<HashMap<String, Int>>()
        /*val myGradients = HashMap<String, Int>()
        myGradients["buttonIcon"] = R.drawable.icon_apps
        myGradients["buttonText"] = R.string.dual_my_gradients
        menuArray.add(myGradients)*/

        val feedback = HashMap<String, Int>()
        feedback["buttonIcon"] = R.drawable.icon_feedback
        feedback["buttonText"] = R.string.word_feedback
        menuArray.add(feedback)

        val support = HashMap<String, Int>()
        support["buttonIcon"] = R.drawable.icon_money
        support["buttonText"] = R.string.word_donate
        menuArray.add(support)

        val settings = HashMap<String, Int>()
        settings["buttonIcon"] = R.drawable.icon_settings
        settings["buttonText"] = R.string.word_settings
        menuArray.add(settings)

        val refresh = HashMap<String, Int>()
        refresh["buttonIcon"] = R.drawable.icon_reload
        refresh["buttonText"] = R.string.word_refresh
        menuArray.add(refresh)

        return menuArray
    }

    /**
     * Single Button HashMaps
     */
    fun okArray() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()
        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_ok
        one["buttonIcon"] = R.drawable.icon_check
        array.add(one)

        return array
    }

    fun understandArray() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()
        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.dual_i_understand
        one["buttonIcon"] = R.drawable.icon_check
        array.add(one)

        return array
    }


    /**
     * Dual Button HashMaps
     */
    fun allowDeny() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()
        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_allow
        one["buttonIcon"] = R.drawable.icon_check
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_deny
        two["buttonIcon"] = R.drawable.icon_close
        array.add(two)

        return array
    }

    fun onOff() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()
        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_on
        one["buttonIcon"] = R.drawable.icon_on
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_off
        two["buttonIcon"] = R.drawable.icon_off
        array.add(two)

        return array
    }


    /**
     * Trio Button HashMaps
     */
    fun lightDarkDarker() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()
        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_light
        one["buttonIcon"] = R.drawable.icon_theme_light
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_dark
        two["buttonIcon"] = R.drawable.icon_theme_dark
        array.add(two)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.word_darker
        three["buttonIcon"] = R.drawable.icon_theme_black
        array.add(three)

        return array
    }

    /**
     * Data Warning
     */
    fun dataWarningArrayList() : ArrayList<HashMap<String, Int>> {
        val dataWarningArray = ArrayList<HashMap<String, Int>>()
        val useDataHash = HashMap<String, Int>()
        useDataHash["buttonTitle"] = R.string.dual_use_data
        useDataHash["buttonIcon"] = R.drawable.icon_cell_wifi
        dataWarningArray.add(useDataHash)

        val useForeverHash = HashMap<String, Int>()
        useForeverHash["buttonTitle"] = R.string.dual_use_forever
        useForeverHash["buttonIcon"] = R.drawable.icon_cell_wifi
        dataWarningArray.add(useForeverHash)

        val tryWifiHash = HashMap<String, Int>()
        tryWifiHash["buttonTitle"] = R.string.dual_try_wifi
        tryWifiHash["buttonIcon"] = R.drawable.icon_wifi_full
        dataWarningArray.add(tryWifiHash)

        return dataWarningArray
    }

    /**
     * No Connection
     */
    fun noConnectionArrayList() : ArrayList<HashMap<String, Int>> {
        val noConnectionArray = ArrayList<HashMap<String, Int>>()
        val retryHash = HashMap<String, Int>()
        retryHash["buttonTitle"] = R.string.word_retry
        retryHash["buttonIcon"] = R.drawable.icon_reload
        noConnectionArray.add(retryHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.word_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        noConnectionArray.add(cancelHash)

        return noConnectionArray
    }

    fun offlineAvailableArrayList() : ArrayList<HashMap<String, Int>> {
        val noConnectionArray = ArrayList<HashMap<String, Int>>()
        val retryHash = HashMap<String, Int>()
        retryHash["buttonTitle"] = R.string.word_retry
        retryHash["buttonIcon"] = R.drawable.icon_reload
        noConnectionArray.add(retryHash)

        val offlineHashHash = HashMap<String, Int>()
        offlineHashHash["buttonTitle"] = R.string.dual_go_offline
        offlineHashHash["buttonIcon"] = R.drawable.icon_wifi_empty
        noConnectionArray.add(offlineHashHash)

        return noConnectionArray
    }

    /**
     * Offline Mode
     */
    fun offlineModeArrayList() : ArrayList<HashMap<String, Int>> {
        val offlineModeArray = ArrayList<HashMap<String, Int>>()
        val goOnlineHash = HashMap<String, Int>()
        goOnlineHash["buttonTitle"] = R.string.dual_go_online
        goOnlineHash["buttonIcon"] = R.drawable.icon_reload
        offlineModeArray.add(goOnlineHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.word_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        offlineModeArray.add(cancelHash)

        return offlineModeArray
    }

    /**
     * Set Wallpaper
     */
    fun setWallpaperArrayList() : ArrayList<HashMap<String, Int>> {
        val setWallpaperArray = ArrayList<HashMap<String, Int>>()
        val homeScreenHash = HashMap<String, Int>()
        homeScreenHash["buttonTitle"] = R.string.dual_home_screen
        homeScreenHash["buttonIcon"] = R.drawable.icon_home
        setWallpaperArray.add(homeScreenHash)

        val lockScreenHash = HashMap<String, Int>()
        lockScreenHash["buttonTitle"] = R.string.dual_lock_screen
        lockScreenHash["buttonIcon"] = R.drawable.icon_lock
        setWallpaperArray.add(lockScreenHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.word_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        setWallpaperArray.add(cancelHash)

        return setWallpaperArray
    }

    fun createGradientArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val makeAnotherHash = HashMap<String, Int>()
        makeAnotherHash["buttonTitle"] = R.string.dual_i_understand
        makeAnotherHash["buttonIcon"] = R.drawable.icon_check
        missingInfoArray.add(makeAnotherHash)

        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_cancel
        backHash["buttonIcon"] = R.drawable.icon_close
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun missingInfoArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_back
        backHash["buttonIcon"] = R.drawable.icon_back
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun gradientExistsArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_back
        backHash["buttonIcon"] = R.drawable.icon_back
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun gradientSubmittedArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val makeAnotherHash = HashMap<String, Int>()
        makeAnotherHash["buttonTitle"] = R.string.word_copy
        makeAnotherHash["buttonIcon"] = R.drawable.icon_copy
        missingInfoArray.add(makeAnotherHash)

        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_close
        backHash["buttonIcon"] = R.drawable.icon_close
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun readWritePermissionArrayList() : ArrayList<HashMap<String, Int>> {
        val readWritePermissionArray = ArrayList<HashMap<String, Int>>()
        val iUnderstandHash = HashMap<String, Int>()
        iUnderstandHash["buttonTitle"] = R.string.dual_i_understand
        iUnderstandHash["buttonIcon"] = R.drawable.icon_view
        readWritePermissionArray.add(iUnderstandHash)

        return readWritePermissionArray
    }

    fun saveGradientArrayList() : ArrayList<HashMap<String, Int>> {
        val saveGradientArray = ArrayList<HashMap<String, Int>>()
        val saveGradientHash = HashMap<String, Int>()
        saveGradientHash["buttonTitle"] = R.string.word_yes
        saveGradientHash["buttonIcon"] = R.drawable.icon_download
        saveGradientArray.add(saveGradientHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.word_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        saveGradientArray.add(cancelHash)

        return saveGradientArray
    }

    fun gradientSavedArrayList() : ArrayList<HashMap<String, Int>> {
        val gradientSavedArray = ArrayList<HashMap<String, Int>>()
        val openLocationHash = HashMap<String, Int>()
        openLocationHash["buttonTitle"] = R.string.word_open
        openLocationHash["buttonIcon"] = R.drawable.icon_open
        gradientSavedArray.add(openLocationHash)

        val closePopupHash = HashMap<String, Int>()
        closePopupHash["buttonTitle"] = R.string.word_close
        closePopupHash["buttonIcon"] = R.drawable.icon_close
        gradientSavedArray.add(closePopupHash)

        return gradientSavedArray
    }

    fun gradientArrayNotUpdated() : ArrayList<HashMap<String, Int>> {
        val gradientSavedArray = ArrayList<HashMap<String, Int>>()
        val openLocationHash = HashMap<String, Int>()
        openLocationHash["buttonTitle"] = R.string.dual_go_online
        openLocationHash["buttonIcon"] = R.drawable.icon_reload
        gradientSavedArray.add(openLocationHash)

        val closePopupHash = HashMap<String, Int>()
        closePopupHash["buttonTitle"] = R.string.word_cancel
        closePopupHash["buttonIcon"] = R.drawable.icon_close
        gradientSavedArray.add(closePopupHash)

        return gradientSavedArray
    }

    fun BAClose() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val closePopupHash = HashMap<String, Int>()
        closePopupHash["buttonTitle"] = R.string.word_close
        closePopupHash["buttonIcon"] = R.drawable.icon_close
        array.add(closePopupHash)

        return array
    }

    fun BABackCancel() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_back
        one["buttonIcon"] = R.drawable.icon_back
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_cancel
        two["buttonIcon"] = R.drawable.icon_close
        array.add(two)

        return array
    }

    fun arrayYesCancel() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_yes
        one["buttonIcon"] = R.drawable.icon_check
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_cancel
        two["buttonIcon"] = R.drawable.icon_close
        array.add(two)

        return array
    }

    fun arrayContinueOfflineRetry() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_continue
        one["buttonIcon"] = R.drawable.icon_arrow
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.dual_go_offline
        two["buttonIcon"] = R.drawable.icon_wifi_empty
        array.add(two)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.word_retry
        three["buttonIcon"] = R.drawable.icon_reload
        array.add(three)

        return array
    }

    fun arraySureNotThisTimeDontAsk() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_sure
        one["buttonIcon"] = R.drawable.icon_check
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.dual_not_now
        two["buttonIcon"] = R.drawable.icon_close
        array.add(two)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.sentence_dont_ask_again
        three["buttonIcon"] = R.drawable.icon_close
        array.add(three)

        return array
    }

    fun settingsArray() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_theme
        one["buttonIcon"] = R.drawable.icon_brush
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_vibration
        two["buttonIcon"] = R.drawable.icon_vibrate_on
        array.add(two)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.dual_special_effects
        three["buttonIcon"] = R.drawable.icon_blur_on
        array.add(three)

        val four = HashMap<String, Int>()
        four["buttonTitle"] = R.string.dual_use_data
        four["buttonIcon"] = R.drawable.icon_cell_wifi
        array.add(four)

        return array
    }

    fun donateArray() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val ad = HashMap<String, Int>()
        ad["buttonIcon"] = R.drawable.icon_advertise
        ad["buttonTitle"] = R.string.word_ad
        array.add(ad)

        val one = HashMap<String, Int>()
        one["buttonIcon"] = R.drawable.icon_dollar_1
        one["buttonTitle"] = R.string.word_usd
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonIcon"] = R.drawable.icon_dollar_2
        two["buttonTitle"] = R.string.word_usd
        array.add(two)

        val five = HashMap<String, Int>()
        five["buttonIcon"] = R.drawable.icon_dollar_5
        five["buttonTitle"] = R.string.word_usd
        array.add(five)

        val ten = HashMap<String, Int>()
        ten["buttonIcon"] = R.drawable.icon_dollar_10
        ten["buttonTitle"] = R.string.word_usd
        array.add(ten)

        return array
    }

    fun onOffAsk() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_on
        one["buttonIcon"] = R.drawable.icon_on
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.word_off
        two["buttonIcon"] = R.drawable.icon_off
        array.add(two)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.dual_ask_everytime
        three["buttonIcon"] = R.drawable.icon_reload
        array.add(three)

        return array
    }


    /**
     * Support HashMap
     */
    fun supportHashMaps() : ArrayList<HashMap<String, String>> {
        val array = ArrayList<HashMap<String, String>>()

        val one = HashMap<String, String>()
        one["buttonTitle"] = "Buy me a dollar"
        one["buttonBody"] = "I'll cherish that dollar forever"
        one["buttonAmount"] = "$1"
        array.add(one)

        val two = HashMap<String, String>()
        two["buttonTitle"] = "Buy me two dollars"
        two["buttonBody"] = "Double the cherishing than the option to the left"
        two["buttonAmount"] = "$2"
        array.add(two)

        val three = HashMap<String, String>()
        three["buttonTitle"] = "Buy me some chocolate"
        three["buttonBody"] = "I might get something else, don't want to eat too much!"
        three["buttonAmount"] = "$3"
        array.add(three)

        val four = HashMap<String, String>()
        four["buttonTitle"] = "Buy me a mouse-pad"
        four["buttonBody"] = "They go for about $4 at the dollar store"
        four["buttonAmount"] = "$4"
        array.add(four)

        val five = HashMap<String, String>()
        five["buttonTitle"] = "Buy me a coffee"
        five["buttonBody"] = "Or something else, I don't drink coffee"
        five["buttonAmount"] = "$5"
        array.add(five)

        val ten = HashMap<String, String>()
        ten["buttonTitle"] = "Buy me motivation"
        ten["buttonBody"] = "And I'll give you a cool feature!"
        ten["buttonAmount"] = "$10"
        array.add(ten)

        val fifteen = HashMap<String, String>()
        fifteen["buttonTitle"] = "Drop some money"
        fifteen["buttonBody"] = "Sweet, just found $15 on the ground!"
        fifteen["buttonAmount"] = "$15"
        array.add(fifteen)

        val twenty = HashMap<String, String>()
        twenty["buttonTitle"] = "You're too kind"
        twenty["buttonBody"] = "Now that's a kind donation"
        twenty["buttonAmount"] = "$20"
        array.add(twenty)

        val twentyfive = HashMap<String, String>()
        twentyfive["buttonTitle"] = "Buy me a starbucks coffee"
        twentyfive["buttonBody"] = "Or are they $30? Either way I don't drink coffee, so I'll go for the smoothie instead..."
        twentyfive["buttonAmount"] = "$25"
        array.add(twentyfive)

        val fifty = HashMap<String, String>()
        fifty["buttonTitle"] = "Mis-click?"
        fifty["buttonBody"] = "Did you accidentally consider this amount?"
        fifty["buttonAmount"] = "$50"
        array.add(fifty)

        val onehundred = HashMap<String, String>()
        onehundred["buttonTitle"] = "WOW!"
        onehundred["buttonBody"] = "Imma cap it here, if you do give this amount... I am VERY grateful"
        onehundred["buttonAmount"] = "$100"
        array.add(onehundred)

        return array
    }
}