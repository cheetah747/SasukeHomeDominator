package com.sibyl.sasukehomeDominator.services

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.sibyl.sasukehomeDominator.util.JumpActivityDominator
import com.sibyl.sasukehomeDominator.util.JumpWrapper
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar

/**
 * @author Sasuke on 2021/5/10.
 */
class AnyTileService : TileService() {
    val jumpWrapper by lazy { JumpWrapper(this) }

    override fun onClick() {
        super.onClick()
        //收起通知栏
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
//        jumpWrapper.jump()

        sendBroadcast(Intent().apply {
            setAction(StaticVar.TILE_BROADCAST)
            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE, StaticVar.STRONG_SHARINGAN)
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