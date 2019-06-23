package com.sibyl.sasukehomeDominator

import android.app.Application

/**
 * @author Sasuke on 2019/6/23.
 */
class SasukeApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferHelper.init(this)
    }
}