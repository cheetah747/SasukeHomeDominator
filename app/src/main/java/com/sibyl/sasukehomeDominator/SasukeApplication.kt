package com.sibyl.sasukehomeDominator

import android.app.Application
import android.content.Context
import com.sibyl.sasukehomeDominator.util.PreferHelper
import java.util.concurrent.Executors

/**
 * @author Sasuke on 2019/6/23.
 */
class SasukeApplication: Application() {
    //线程池
    val executor by lazy { Executors.newCachedThreadPool() }

    companion object{
        @JvmStatic
        lateinit var app: SasukeApplication
    }



    override fun onCreate() {
        super.onCreate()
        app = this
        PreferHelper.init(this)

    }

}