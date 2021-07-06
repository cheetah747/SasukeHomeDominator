package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import java.io.Serializable

/**
 * @author Sasuke on 2021-06-07.
 */
class ActivityInfo2: Serializable {
    var activityName: String = ""
    var exported: Boolean = true
    @Transient
    var appInfo: ApplicationInfo? = null//留着用于生成图标

    fun copyActivityInfo(context: Context, activityInfo: ActivityInfo){
//        if (appIcon == null){
//            appIcon = bitmap2Bytes(drawable2Bitmap(activityInfo.applicationInfo.loadIcon(context.packageManager)))
//        }
        activityName = activityInfo.name
        exported = activityInfo.exported
        appInfo = activityInfo.applicationInfo
    }

    /**在子线程里生成图标*/
    fun createAppIcon(context: Context): ByteArray? = bitmap2Bytes(drawable2Bitmap(appInfo?.loadIcon(context.packageManager)))

    /**获取短Activity*/
    fun getShortGoalActivity() = activityName.split(".").run { this[this.size - 1] }
}