package com.sibyl.fuckwelcomeactivity.selectapp.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import com.sibyl.sasukehomeDominator.util.bitmap2Bytes
import com.sibyl.sasukehomeDominator.util.drawable2Bitmap
import com.sibyl.sasukehomeDominator.util.getPkgIcon
import java.io.Serializable

/**
 * @author Sasuke on 2021-06-05.
 */
data class PackData(
    var appName: String,//app名称
    var pkgName: String,//app包名
    @Transient
    var appIcon: ByteArray?,//app图标
    @Transient
    var appInfo: ApplicationInfo?,//用来后面子线程里初始化appIcon
//    var appIconBytes: ByteArray?,//图标的序列化
    var isSystemApp: Boolean,//是否是系统应用
//    var activityList: List<ActivityItem>? = listOf()
    var goalActivity: String = "",//目标跳转Activity
    var myWelcomePic: String = "",//自定义封面路径
    var isExported: Boolean = true,//跳转该Activity是否需要root  true不需要 false需要
    var stayTime: Float = 0.4f//持续时间
): Serializable {
//    data class ActivityItem(val activityName: String, val isExported: Boolean):Serializable
    /**利用appIcon来把appIconBytes初始化*/
//    fun initAppIconBytes(){
//        appIconBytes = bitmap2Bytes(drawable2Bitmap(appIcon))
//    }
    /**用于编辑老数据时的覆盖操作*/
    fun copyNewData(packData: PackData){
        appName = packData.appName
        pkgName = packData.pkgName
        isSystemApp = packData.isSystemApp
        goalActivity = packData.goalActivity
        myWelcomePic = packData.myWelcomePic
        isExported = packData.isExported
        stayTime = packData.stayTime
    }

    /**在子线程里生成图标*/
    fun createAppIcon(context: Context): ByteArray? = bitmap2Bytes(createIconBmp(context))

    fun createIconBmp(context: Context): Bitmap? = drawable2Bitmap(getPkgIcon(context,pkgName))


    /**获取短Activity*/
    fun getShortGoalActivity() = goalActivity.split(".").run { this[this.size - 1] }
}