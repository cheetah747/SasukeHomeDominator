package com.sibyl.sasukehomeDominator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author Sasuke on 2019/6/23.
 */
class HomeLongPressAct: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //把服务跑起来
        startMyService()
        finish()
    }

    fun startMyService() {
        startService(Intent(this, SasukeAccessibilityService::class.java))
    }

    fun stopMyService() {
        stopService(Intent(this, SasukeAccessibilityService::class.java))
    }

}