package com.sibyl.sasukehomeDominator

import android.content.Intent
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * @author Sasuke on 2019-6-24 0024.
 */
class MyScreenShotTile: TileService() {

    override fun onClick() {
        super.onClick()
        startActivityAndCollapse(Intent(this,BridgeAct::class.java).apply {
            putExtra(StaticVar.KEY_IS_FROM_SCRSHOT_TILE,true)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }


    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.state = Tile.STATE_ACTIVE
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
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}