package com.sibyl.sasukehomeDominator.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/***
 * 专门用来接收从瓷贴点击跳转过来的
 */
class ForTileBroadcastReceiver(val accessibilityService: SasukeAccessibilityService?) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        accessibilityService?.tilesClick(intent)
    }
}