package com.simple.chris.pebble.functions

import com.simple.chris.pebble.R

object HashMaps {

    /**
     * Browse Menu
     */
    fun browseMenuArray() : ArrayList<HashMap<String, Int>> {
        val menuArray = ArrayList<HashMap<String, Int>>()

        val support = HashMap<String, Int>()
        support["buttonIcon"] = R.drawable.icon_question
        support["buttonText"] = R.string.word_about
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

    fun backButtonArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_back
        backHash["buttonIcon"] = R.drawable.icon_arrow_left
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun gradientSubmittedArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()

        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_close
        backHash["buttonIcon"] = R.drawable.icon_close
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun watchAdArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()

        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.word_view
        backHash["buttonIcon"] = R.drawable.icon_view
        missingInfoArray.add(backHash)

        val closeHash = HashMap<String, Int>()
        closeHash["buttonTitle"] = R.string.word_cancel
        closeHash["buttonIcon"] = R.drawable.icon_close
        missingInfoArray.add(closeHash)

        return missingInfoArray
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
        two["buttonIcon"] = R.drawable.icon_vibrate
        array.add(two)

        val three = HashMap<String, Int>()
        three["buttonTitle"] = R.string.word_blur
        three["buttonIcon"] = R.drawable.icon_blur_on
        array.add(three)

        val four = HashMap<String, Int>()
        four["buttonTitle"] = R.string.dual_split_screen
        four["buttonIcon"] = R.drawable.icon_split_screen
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

        /*val one = HashMap<String, Int>()
        one["buttonIcon"] = R.drawable.image_donate_1
        one["buttonTitle"] = R.string.dual_donate_1
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonIcon"] = R.drawable.image_donate_2
        two["buttonTitle"] = R.string.dual_donate_2
        array.add(two)

        val five = HashMap<String, Int>()
        five["buttonIcon"] = R.drawable.image_donate_3
        five["buttonTitle"] = R.string.dual_donate_3
        array.add(five)

        val ten = HashMap<String, Int>()
        ten["buttonIcon"] = R.drawable.image_donate_4
        ten["buttonTitle"] = R.string.dual_donate_4
        array.add(ten)*/

        return array
    }

    fun aboutArray() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val ad = HashMap<String, Int>()
        ad["buttonIcon"] = R.drawable.icon_dev
        ad["buttonTitle"] = R.string.word_developer
        array.add(ad)

        val one = HashMap<String, Int>()
        one["buttonIcon"] = R.drawable.icon_build
        one["buttonTitle"] = R.string.build_date
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonIcon"] = R.drawable.icon_server_version
        two["buttonTitle"] = R.string.server_version
        array.add(two)

        /*val changelog = HashMap<String, Int>()
        changelog["buttonIcon"] = R.drawable.icon_changelog
        changelog["buttonTitle"] = R.string.word_changelog
        array.add(changelog)*/
        val jr = HashMap<String, Int>()
        jr["buttonIcon"] = R.drawable.icon_jr_dev
        jr["buttonTitle"] = R.string.jr_dev
        array.add(jr)

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

        return array
    }
}