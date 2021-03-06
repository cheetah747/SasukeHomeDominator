package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.widget.Toast

/**
 * @author Administrator on 2021-07-13 013.
 */
class JumpWrapper(val context: Context) {
    var pkgName = ""
    var activityName = ""

    fun jump(){
        PreferHelper.getInstance().getString(StaticVar.KEY_ANY_TILE, "")?.takeIf { it.isNotEmpty() && it.split("/").size == 2 }?.run {
            pkgName =  this.split("/")[0]
            activityName = this.split("/")[1].run { if (this.startsWith(".")) pkgName + this else this}

            val appIntent = context.packageManager?.getLaunchIntentForPackage(pkgName)
            if (appIntent == null){
                Toast.makeText(context,"未安装${pkgName}", Toast.LENGTH_SHORT).show()
            }else{
                val isRoot = PreferHelper.getInstance().getBoolean(StaticVar.KEY_ANY_TILE_IS_ROOT, false)
                //通过root跳转
                if (isRoot){
                    JumpActivityDominator.jumpByRoot(context,pkgName,activityName)
                    //普通跳转
                }else{
                    JumpActivityDominator.jumpNoRoot(context,pkgName,activityName)
                }
            }
        }
    }
}