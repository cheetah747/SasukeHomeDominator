package com.sibyl.sasukehomeDominator.services

import android.content.Intent
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.sibyl.sasukehomeDominator.util.StaticVar

/**
 * @author Sasuke on 2019-6-24 0024.
 */
class ScreenShotTileService: TileService() {

    override fun onClick() {
        super.onClick()
        //收起通知栏
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))

//        startForegroundService(Intent(this, SasukeAccessibilityService::class.java).apply {
//            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE,StaticVar.STRONG_SCRSHOT)
//        })
        sendBroadcast(Intent().apply {
            setAction(StaticVar.TILE_BROADCAST)
            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE,StaticVar.STRONG_SCRSHOT)
        })
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