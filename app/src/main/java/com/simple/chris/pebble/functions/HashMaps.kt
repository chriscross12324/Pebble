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

        /*val feedback = HashMap<String, Int>()
        feedback["buttonIcon"] = R.drawable.icon_feedback
        feedback["buttonText"] = R.string.word_feedback
        menuArray.add(feedback)*/

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

    fun restartArray() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()
        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_restart
        one["buttonIcon"] = R.drawable.icon_reload
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
        three["buttonTitle"] = R.string.word_blur
        three["buttonIcon"] = R.drawable.icon_blur_on
        array.add(three)

        val four = HashMap<String, Int>()
        four["buttonTitle"] = R.string.dual_split_screen
        four["buttonIcon"] = R.drawable.split_screen
        array.add(four)

        val five = HashMap<String, Int>()
        five["buttonTitle"] = R.string.dual_use_data
        five["buttonIcon"] = R.drawable.icon_cell_wifi
        array.add(five)

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

        val three = HashMap<String, Int>()
        three["buttonIcon"] = R.drawable.icon_dollar_2
        three["buttonTitle"] = R.string.word_usd
        array.add(three)

        val four = HashMap<String, Int>()
        four["buttonIcon"] = R.drawable.icon_dollar_5
        four["buttonTitle"] = R.string.word_usd
        array.add(four)

        val six = HashMap<String, Int>()
        six["buttonIcon"] = R.drawable.icon_dollar_10
        six["buttonTitle"] = R.string.word_usd
        array.add(ten)

        return array
    }

    fun onOffAsk() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.word_on
        one["buttonIcon"] = R.drawable.icon_on
        array.add(one)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.dual_ask_everytime
        three["buttonIcon"] = R.drawable.icon_reload
        array.add(three)

        return array
    }

    /** Search By Colour Buttons **/
    fun searchByColourButtons() : ArrayList<HashMap<String, String>> {
        val array = ArrayList<HashMap<String, String>>()

        val redHash = HashMap<String, String>()
        redHash["buttonHex"] = "#F44336"
        redHash["buttonColour"] = "red"
        array.add(redHash)

        val orangeHash = HashMap<String, String>()
        orangeHash["buttonHex"] = "#FF9800"
        orangeHash["buttonColour"] = "orange"
        array.add(orangeHash)

        val yellowHash = HashMap<String, String>()
        yellowHash["buttonHex"] = "#FFEB3B"
        yellowHash["buttonColour"] = "yellow"
        array.add(yellowHash)

        val greenHash = HashMap<String, String>()
        greenHash["buttonHex"] = "#4CAF50"
        greenHash["buttonColour"] = "green"
        array.add(greenHash)

        val blueHash = HashMap<String, String>()
        blueHash["buttonHex"] = "#2196F3"
        blueHash["buttonColour"] = "blue"
        array.add(blueHash)

        val purpleHash = HashMap<String, String>()
        purpleHash["buttonHex"] = "#9C27B0"
        purpleHash["buttonColour"] = "purple"
        array.add(purpleHash)

        val darkHash = HashMap<String, String>()
        darkHash["buttonHex"] = "#1c1c1c"
        darkHash["buttonColour"] = "dark"
        array.add(darkHash)

        val lightHash = HashMap<String, String>()
        lightHash["buttonHex"] = "#f5f5f5"
        lightHash["buttonColour"] = "light"
        array.add(lightHash)

        /*val whiteHash = HashMap<String, String>()
        whiteHash["buttonColour"] = "#f5f5f5"
        array.add(whiteHash)*/

        return array
    }

    fun colours() : ArrayList<HashMap<String, String>> {
        val array = ArrayList<HashMap<String, String>>()

        val redHash = HashMap<String, String>()
        redHash["buttonHex"] = "#d48ec3"
        redHash["buttonColour"]
        array.add(redHash)

        val orangeHash = HashMap<String, String>()
        orangeHash["buttonHex"] = "#bf96ff"
        array.add(orangeHash)

        val yellowHash = HashMap<String, String>()
        yellowHash["buttonHex"] = "#679fd6"
        array.add(yellowHash)

        val redash = HashMap<String, String>()
        redash["buttonHex"] = "#8acf8a"
        array.add(redash)

        val orangeHsh = HashMap<String, String>()
        orangeHsh["buttonHex"] = "#d6d05a"
        array.add(orangeHsh)

        val yellwHash = HashMap<String, String>()
        yellwHash["buttonHex"] = "#ebab57"
        array.add(yellwHash)

        val yelwHash = HashMap<String, String>()
        yelwHash["buttonHex"] = "#eb3d3d"
        array.add(yelwHash)

        return array
    }
}