package com.sibyl.sasukehomeDominator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sibyl.sasukehomeDominator.services.SasukeAccessibilityService

/**
 * @author Sasuke on 2019/6/23.
 */
class BridgeAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //把服务跑起来
        startHomeService()
        finish()
    }

    /**长按HOME键时的跳转服务*/
    fun startHomeService() {
        startService(Intent(this, SasukeAccessibilityService::class.java))
    }

    /**点击通知栏瓷贴时的跳转服务*/
//    fun startTileClickService(){
//        startService(Intent(this, SasukeAccessibilityService::class.java).apply {
//            putExtra(StaticVar.KEY_IS_FROM_SCRSHOT_TILE,true)
//        })
//    }

//    fun stopMyService() {
//        stopService(Intent(this, SasukeAccessibilityService::class.java))
//    }

}