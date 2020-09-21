package com.sibyl.sasukehomeDominator

import android.app.Application
import android.content.Context
import com.sibyl.sasukehomeDominator.util.PreferHelper

/**
 * @author Sasuke on 2019/6/23.
 */
class SasukeApplication: Application() {
    companion object{
        @JvmStatic
        var app: Context? = null
    }



    override fun onCreate() {
        super.onCreate()
        app = applicationContext
        PreferHelper.init(this)

    }

}