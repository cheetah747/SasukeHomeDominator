package com.sibyl.sasukehomeDominator

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.sibyl.sasukehomeDominator.services.SasukeAccessibilityService
import com.sibyl.sasukehomeDominator.util.BaseActivity
import com.sibyl.sasukehomeDominator.util.ClipboardUtil
import com.sibyl.sasukehomeDominator.util.StaticVar

/**
 * @author Sasuke on 2022/5/29.
 */
class BridgeActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //把服务跑起来
        //二改：不要重复跑服务，在某些ROM如氧OS上会崩
//        startHomeService()
        val intentTag = intent.getStringExtra(StaticVar.KEY_ACCESSIBILITY_TYPE)
        performActionByTag(intentTag)

        finish()
    }

    /**根据intent传进来的tag来执行特定任务*/
    fun performActionByTag(tag: String?){
        ClipboardUtil.clear(SasukeApplication.app)
        if (TextUtils.isEmpty(tag)) return
        sendBroadcast(Intent().apply {
            setAction(StaticVar.TILE_BROADCAST)
            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE, tag2StrongType(tag))
        })
    }

    private fun tag2StrongType(tag: String?): String? = when(tag){
        StaticVar.LOCK_SCREEN -> StaticVar.STRONG_LOCKSCREEN
        StaticVar.POWER_LONGPRESS -> StaticVar.STRONG_POWER_LONGPRESS
        StaticVar.SHARINGAN -> StaticVar.STRONG_SHARINGAN
        StaticVar.NOTIFI -> StaticVar.STRONG_NOTIFI
        StaticVar.FUCKBRIGHTNESS -> StaticVar.STRONG_FUCK_BRIGHTNESS
        else -> null
    }

    /**长按HOME键时的跳转服务*/
    fun startHomeService() {
        startService(Intent(this, SasukeAccessibilityService::class.java))
    }

    companion object{
        @JvmStatic
        fun initClassIntent(thirdIntent: Intent){
            thirdIntent.setClassName(SasukeApplication.app.packageName, BridgeActivity::class.java.canonicalName!!)
        }

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