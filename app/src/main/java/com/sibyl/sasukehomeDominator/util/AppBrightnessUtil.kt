package com.sibyl.sasukehomeDominator.util

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import android.content.ContentResolver
import android.content.Context
import android.provider.Settings


/**
 * @author Sasuke on 2022/1/15.
 * 设置屏幕亮度
 */
fun setAppScreenBrightness(activity: Activity,brightnessValue: Int){
    val lp: WindowManager.LayoutParams = activity.window.getAttributes()
    lp.screenBrightness = if (brightnessValue == -1) -1f else brightnessValue / 255.0f
    activity.window.setAttributes(lp)
}

/**
 * 1.获取系统默认屏幕亮度值 屏幕亮度值范围（0-255）
 */
fun getSystemBrightness(context: Context): Int {
    val contentResolver: ContentResolver = context.getContentResolver()
    val defVal = 125
    return Settings.System.getInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS, defVal)
}