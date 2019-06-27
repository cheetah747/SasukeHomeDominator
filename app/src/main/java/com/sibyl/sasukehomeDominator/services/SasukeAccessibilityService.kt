package com.sibyl.sasukehomeDominator.services

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.view.accessibility.AccessibilityEvent
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_IS_SHOW_WATERMARK
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_TIME_TO_SCRSHOT
import com.sibyl.screenshotlistener.ScreenShotListenManager
import com.sibyl.screenshotlistener.WaterMarker
import org.jetbrains.anko.doAsync
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Sasuke on 2019/6/22.
 */
class SasukeAccessibilityService : AccessibilityService() {
//    private var mContext: Context? = null

    val manager: ScreenShotListenManager by lazy {
        ScreenShotListenManager.newInstance(this).apply {
            setListener { imagePath: String? ->
                //如果水印开关关掉了，那就不画水印
                if (! PreferHelper.getInstance().getBoolean(KEY_IS_SHOW_WATERMARK,false)) return@setListener
                doAsync {
                    Thread.sleep(1500)//有些垃圾系统截图时写入磁盘比较慢，所以这边要等一下。
                    WaterMarker(this@SasukeAccessibilityService).apply {
                        val phoneInfo = PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME,"").run { if(isNotBlank()) "${this}@" else "" } +
                                                                PreferHelper.getInstance().getString(StaticVar.KEY_PHONE_MODEL,android.os.Build.MODEL)
                        //开始把水印画上去
                        val psResultShot = drawWaterMark(imagePath,phoneInfo,SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                        saveBmp2File(psResultShot, File(imagePath), Bitmap.CompressFormat.JPEG)
//                        imagePath?.let {
//                            mergeScrShot2BottomCard(imagePath, bottomCard)
//                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
//        mContext = applicationContext
        manager.startListen()
//        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //截图延时
        val scrShotDelay = PreferHelper.getInstance().getInt(KEY_TIME_TO_SCRSHOT,0).toLong()

        //从通知栏瓷贴点击过来的（默认false）
        when(intent.getStringExtra(StaticVar.KEY_ACCESSIBILITY_TYPE)){
            //是从瓷贴截屏按钮点击过来的，就强行执行，忽略掉selected主界面的选择
            StaticVar.STRONG_SCRSHOT -> {Handler().postDelayed({ performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) },600 + scrShotDelay);return Service.START_STICKY}
            StaticVar.STRONG_LOCKSCREEN -> {performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);return Service.START_STICKY}
            StaticVar.STRONG_POWER_LONGPRESS -> {performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);return Service.START_STICKY}
        }

        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        //常规长按HOME键过来的
        when (selected) {
            StaticVar.LOCK_SCREEN -> performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            StaticVar.SCREEN_SHOT -> Handler().postDelayed({ performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) }, 1500 + scrShotDelay)
            StaticVar.POWER_LONGPRESS -> performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        }

        return Service.START_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        //锁屏(安卓9.0适用)
//        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        //按下返回键
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_BACK)
        //向下拉出状态栏
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
        //向下拉出状态栏并显示出所有的快捷操作按钮
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
        //按下HOME键
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_HOME)
        //显示最近任务
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS)
        //长按电源键
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        //分屏
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
        //截屏(安卓9.0适用)
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
        //打开快速设置
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    override fun onInterrupt() {
    }

//    class LockScreenMsg


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: LockScreenMsg) {
//
//    }
}