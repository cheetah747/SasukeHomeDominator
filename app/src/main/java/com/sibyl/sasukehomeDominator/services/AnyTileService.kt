package com.sibyl.sasukehomeDominator.services

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar

/**
 * @author Sasuke on 2021/5/10.
 */
class AnyTileService : TileService() {
    var appName = ""
    var activityName = ""

    override fun onClick() {
        super.onClick()
        //收起通知栏
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))

        PreferHelper.getInstance().getString(StaticVar.KEY_ANY_TILE, "")?.takeIf { it.isNotEmpty() && it.split("/").size == 2 }?.run {
            appName =  this.split("/")[0]
            activityName = this.split("/")[1].run { if (this.startsWith(".")) appName + this else this}

            val appIntent = packageManager?.getLaunchIntentForPackage(appName)
            if (appIntent == null){
                Toast.makeText(this@AnyTileService,"未安装${appName}", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent().apply {
                    setClassName(appName, activityName)
                    flags = FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }




    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
    }


    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.updateTile()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}