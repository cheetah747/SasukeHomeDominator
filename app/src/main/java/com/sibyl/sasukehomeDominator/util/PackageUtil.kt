package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * @author Administrator on 2021/6/7 0007.
 */

/**通过包名获取Icon*/
fun getPkgIcon(context: Context, pkgName: String): Drawable?{
    try {
        context.packageManager.run {
            return getApplicationIcon(getApplicationInfo(pkgName, PackageManager.GET_META_DATA))
        }
    }catch (e: PackageManager.NameNotFoundException){
        e.printStackTrace()
    }
    return null
}


/**通过包名获取所有Activity
 *  （含应用图标）
 * */
fun getPkgActivities(context: Context, pkgName: String):List<ActivityInfo2>?{
    try {
        context.packageManager.run {
            val activities2 = mutableListOf<ActivityInfo2>()
            getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES).activities?.forEach{
                activities2.add(ActivityInfo2().apply { copyActivityInfo(context,it) })
            }
            return activities2.apply { sortBy { !it.exported } }
        }
    }catch (e: PackageManager.NameNotFoundException){
        e.printStackTrace()
    }
    return null
}