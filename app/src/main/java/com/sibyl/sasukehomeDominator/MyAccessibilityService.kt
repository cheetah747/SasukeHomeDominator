package com.sibyl.sasukehomeDominator

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.accessibility.AccessibilityEvent


/**
 * @author Sasuke on 2019/6/22.
 */
class SasukeAccessibilityService : AccessibilityService() {
    private var mContext: Context? = null

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
//        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        when (selected) {
            StaticVar.LOCK_SCREEN -> performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            StaticVar.SCREEN_SHOT -> Handler().postDelayed({ performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) },1500)
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

    class LockScreenMsg


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: LockScreenMsg) {
//
//    }
}