package com.simple.chris.pebble

object AppHashMaps {

    /**
     * Browse Menu
     */
    fun browseMenuArray() : ArrayList<HashMap<String, Int>> {
        val menuArray = ArrayList<HashMap<String, Int>>()
        val myGradients = HashMap<String, Int>()
        myGradients["buttonIcon"] = R.drawable.icon_apps
        myGradients["buttonText"] = R.string.myGradientsTitle
        menuArray.add(myGradients)

        val feedback = HashMap<String, Int>()
        feedback["buttonIcon"] = R.drawable.icon_feedback
        feedback["buttonText"] = R.string.feedbackTitle
        menuArray.add(feedback)

        val settings = HashMap<String, Int>()
        settings["buttonIcon"] = R.drawable.icon_settings
        settings["buttonText"] = R.string.settingsTitle
        menuArray.add(settings)

        val refresh = HashMap<String, Int>()
        refresh["buttonIcon"] = R.drawable.icon_reload
        refresh["buttonText"] = R.string.refreshTitle
        menuArray.add(refresh)

        return menuArray
    }

    /**
     * Data Warning
     */
    fun dataWarningArrayList() : ArrayList<HashMap<String, Int>> {
        val dataWarningArray = ArrayList<HashMap<String, Int>>()
        val useDataHash = HashMap<String, Int>()
        useDataHash["buttonTitle"] = R.string.dialog_button_eng_use_data
        useDataHash["buttonIcon"] = R.drawable.icon_cell_wifi
        dataWarningArray.add(useDataHash)

        val useForeverHash = HashMap<String, Int>()
        useForeverHash["buttonTitle"] = R.string.dialog_button_eng_use_forever
        useForeverHash["buttonIcon"] = R.drawable.icon_cell_wifi
        dataWarningArray.add(useForeverHash)

        val tryWifiHash = HashMap<String, Int>()
        tryWifiHash["buttonTitle"] = R.string.dialog_button_eng_try_wifi
        tryWifiHash["buttonIcon"] = R.drawable.icon_wifi_green
        dataWarningArray.add(tryWifiHash)

        return dataWarningArray
    }

    /**
     * No Connection
     */
    fun noConnectionArrayList() : ArrayList<HashMap<String, Int>> {
        val noConnectionArray = ArrayList<HashMap<String, Int>>()
        val retryHash = HashMap<String, Int>()
        retryHash["buttonTitle"] = R.string.dialog_button_eng_retry
        retryHash["buttonIcon"] = R.drawable.icon_reload
        noConnectionArray.add(retryHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.text_eng_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        noConnectionArray.add(cancelHash)

        return noConnectionArray
    }

    /**
     * Offline Mode
     */
    fun offlineModeArrayList() : ArrayList<HashMap<String, Int>> {
        val offlineModeArray = ArrayList<HashMap<String, Int>>()
        val goOnlineHash = HashMap<String, Int>()
        goOnlineHash["buttonTitle"] = R.string.dialog_button_eng_go_online
        goOnlineHash["buttonIcon"] = R.drawable.icon_reload
        offlineModeArray.add(goOnlineHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.text_eng_cancel
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
        homeScreenHash["buttonTitle"] = R.string.dialog_button_eng_home_screen
        homeScreenHash["buttonIcon"] = R.drawable.icon_home
        setWallpaperArray.add(homeScreenHash)

        val lockScreenHash = HashMap<String, Int>()
        lockScreenHash["buttonTitle"] = R.string.dialog_button_eng_lock_screen
        lockScreenHash["buttonIcon"] = R.drawable.icon_lock
        setWallpaperArray.add(lockScreenHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.text_eng_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        setWallpaperArray.add(cancelHash)

        return setWallpaperArray
    }

    fun createGradientArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val makeAnotherHash = HashMap<String, Int>()
        makeAnotherHash["buttonTitle"] = R.string.text_eng_i_understand
        makeAnotherHash["buttonIcon"] = R.drawable.icon_check
        missingInfoArray.add(makeAnotherHash)

        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.text_eng_cancel
        backHash["buttonIcon"] = R.drawable.icon_close
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun missingInfoArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.text_eng_back
        backHash["buttonIcon"] = R.drawable.icon_back
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun gradientExistsArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.text_eng_back
        backHash["buttonIcon"] = R.drawable.icon_back
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun gradientSubmittedArrayList() : ArrayList<HashMap<String, Int>> {
        val missingInfoArray = ArrayList<HashMap<String, Int>>()
        val makeAnotherHash = HashMap<String, Int>()
        makeAnotherHash["buttonTitle"] = R.string.text_eng_copy
        makeAnotherHash["buttonIcon"] = R.drawable.icon_copy
        missingInfoArray.add(makeAnotherHash)

        val backHash = HashMap<String, Int>()
        backHash["buttonTitle"] = R.string.text_eng_close
        backHash["buttonIcon"] = R.drawable.icon_close
        missingInfoArray.add(backHash)

        return missingInfoArray
    }

    fun readWritePermissionArrayList() : ArrayList<HashMap<String, Int>> {
        val readWritePermissionArray = ArrayList<HashMap<String, Int>>()
        val iUnderstandHash = HashMap<String, Int>()
        iUnderstandHash["buttonTitle"] = R.string.text_eng_i_understand
        iUnderstandHash["buttonIcon"] = R.drawable.icon_view
        readWritePermissionArray.add(iUnderstandHash)

        return readWritePermissionArray
    }

    fun saveGradientArrayList() : ArrayList<HashMap<String, Int>> {
        val saveGradientArray = ArrayList<HashMap<String, Int>>()
        val saveGradientHash = HashMap<String, Int>()
        saveGradientHash["buttonTitle"] = R.string.text_eng_yes
        saveGradientHash["buttonIcon"] = R.drawable.icon_download
        saveGradientArray.add(saveGradientHash)

        val cancelHash = HashMap<String, Int>()
        cancelHash["buttonTitle"] = R.string.text_eng_cancel
        cancelHash["buttonIcon"] = R.drawable.icon_close
        saveGradientArray.add(cancelHash)

        return saveGradientArray
    }

    fun gradientSavedArrayList() : ArrayList<HashMap<String, Int>> {
        val gradientSavedArray = ArrayList<HashMap<String, Int>>()
        val openLocationHash = HashMap<String, Int>()
        openLocationHash["buttonTitle"] = R.string.text_eng_open_gradient
        openLocationHash["buttonIcon"] = R.drawable.icon_open
        gradientSavedArray.add(openLocationHash)

        val closePopupHash = HashMap<String, Int>()
        closePopupHash["buttonTitle"] = R.string.text_eng_close
        closePopupHash["buttonIcon"] = R.drawable.icon_close
        gradientSavedArray.add(closePopupHash)

        return gradientSavedArray
    }

    fun gradientArrayNotUpdated() : ArrayList<HashMap<String, Int>> {
        val gradientSavedArray = ArrayList<HashMap<String, Int>>()
        val openLocationHash = HashMap<String, Int>()
        openLocationHash["buttonTitle"] = R.string.dialog_button_eng_go_online
        openLocationHash["buttonIcon"] = R.drawable.icon_reload
        gradientSavedArray.add(openLocationHash)

        val closePopupHash = HashMap<String, Int>()
        closePopupHash["buttonTitle"] = R.string.text_eng_cancel
        closePopupHash["buttonIcon"] = R.drawable.icon_close
        gradientSavedArray.add(closePopupHash)

        return gradientSavedArray
    }

    fun BAClose() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val closePopupHash = HashMap<String, Int>()
        closePopupHash["buttonTitle"] = R.string.text_eng_close
        closePopupHash["buttonIcon"] = R.drawable.icon_close
        array.add(closePopupHash)

        return array
    }

    fun BABackCancel() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.text_eng_back
        one["buttonIcon"] = R.drawable.icon_back
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.text_eng_cancel
        two["buttonIcon"] = R.drawable.icon_close
        array.add(two)

        return array
    }

    fun arrayYesCancel() : ArrayList<HashMap<String, Int>> {
        val array = ArrayList<HashMap<String, Int>>()

        val one = HashMap<String, Int>()
        one["buttonTitle"] = R.string.text_eng_yes
        one["buttonIcon"] = R.drawable.icon_check
        array.add(one)

        val two = HashMap<String, Int>()
        two["buttonTitle"] = R.string.text_eng_cancel
        two["buttonIcon"] = R.drawable.icon_close
        array.add(two)

        return array
    }
}