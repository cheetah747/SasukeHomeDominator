package com.sibyl.fuckwelcomeactivity.selectapp.presenter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.sibyl.fuckwelcomeactivity.selectapp.model.PackData
import com.sibyl.sasukehomeDominator.selectapp.view.AppListActivity
import com.sibyl.sasukehomeDominator.util.CountDownDominator
import java.util.*


/**
 * @author Sasuke on 2021-06-05.
 */
class AppListPre(val activity: AppListActivity) {
    val packages: MutableList<PackData> = mutableListOf()
    private val SEARCH_PERIOD = 1 * 1000L//输入完成后过多少秒自动执行搜索


    //    final int AUTO_SEARCH_DELAY = 2;//自动执行搜索的延时。（秒）
    //    int goal;//目标时间。
    //    int t = 0;//当前秒数。
    var autoTimer: Timer? = null //用来在输入框发生改变时，自动倒数进行刷新的装置。
    var counter: CountDownDominator? = null

    init {
        //构造自动搜索
        autoTimer = Timer()
        counter = CountDownDominator(activity, activity).apply { build(autoTimer) }
    }

    /**找出所有已安装应用*/
    fun queryAllActivity(isIncludeSystem: Boolean): List<PackData>{
        packages.clear()
        try {
            val packageInfos: List<PackageInfo> = activity.getPackageManager().getInstalledPackages(
                /*PackageManager.GET_ACTIVITIES or */PackageManager.GET_SERVICES
            )
            loop@ for (info in packageInfos) {
//                if (!isIncludeSystem && isSystemAPP(info)) {//如果不需要系统应用 && 它正是系统应用
//                    continue@loop
//                }
                packages.add(
                    PackData(
                        info.applicationInfo.loadLabel(activity.packageManager).toString(),
                        info.packageName,
                        null,//bitmap2Bytes(drawable2Bitmap(info.applicationInfo.loadIcon(activity.packageManager))),
                        info.applicationInfo,
                        isSystemAPP(info)
//                        info.activities?.map { PackData.ActivityItem(it.name,it.exported) }
                    )

                )
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return packages.filter { if (!isIncludeSystem) !it.isSystemApp else true }
    }


    fun notifyAllIcons(){

    }

    /**开始倒计时*/
    fun startCountDownSearch(){
        counter?.start(SEARCH_PERIOD)
    }

    /**通过输入的关键字来搜索APP*/
    fun realSearchApp(key: String, isIncludeSystem: Boolean) = packages.filter {
        (
                key in it.appName.toLowerCase(Locale.getDefault())
                        || key in it.pkgName.toLowerCase(Locale.getDefault()))
                && (if (!isIncludeSystem) !it.isSystemApp else true)
    }


    /**判断是否是系统应用*/
    private fun isSystemAPP(pakInfo: PackageInfo): Boolean{
        val isSysApp = pakInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
        val isSysUpd = pakInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 1
        return isSysApp || isSysUpd
    }
}