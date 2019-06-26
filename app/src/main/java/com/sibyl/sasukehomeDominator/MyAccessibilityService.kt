package com.sibyl.sasukehomeDominator

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.view.accessibility.AccessibilityEvent
import com.sibyl.screenshotlistener.ScreenShotListenManager
import com.sibyl.screenshotlistener.WaterMarker
import org.jetbrains.anko.doAsync
import java.io.File


/**
 * @author Sasuke on 2019/6/22.
 */
class SasukeAccessibilityService : AccessibilityService() {
    private var mContext: Context? = null

    val manager: ScreenShotListenManager by lazy {
        ScreenShotListenManager.newInstance(this).apply {
            setListener { imagePath: String? ->
                doAsync {
                    Thread.sleep(1500)//有些垃圾系统截图时写入磁盘比较慢，所以这边要等一下。
                    WaterMarker(this@SasukeAccessibilityService).apply {
                        val psResultShot = drawWaterMark(imagePath, "WANGHAO@Pixel 3XL", "2019-06-25 16:58:21")
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
        mContext = applicationContext
        manager.startListen()
//        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        //从通知栏瓷贴点击过来的（默认false）
        when(true){
            //是从瓷贴截屏按钮点击过来的，就强行执行，忽略掉selected主界面的选择
            intent.getBooleanExtra(StaticVar.STRONG_SCRSHOT,false) -> {Handler().postDelayed({ performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) },550);return Service.START_STICKY}
            intent.getBooleanExtra(StaticVar.STRONG_LOCKSCREEN,false) -> {performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);return Service.START_STICKY}
            intent.getBooleanExtra(StaticVar.STRONG_POWER_LONGPRESS,false) -> {performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);return Service.START_STICKY}
        }

        //常规长按HOME键过来的
        when (selected) {
            StaticVar.LOCK_SCREEN -> performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            StaticVar.SCREEN_SHOT -> Handler().postDelayed({ performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) }, 1500)
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