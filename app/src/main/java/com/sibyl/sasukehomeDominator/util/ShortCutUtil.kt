package com.sibyl.sasukehomeDominator.util

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import java.util.*


/**
 * @author Administrator on 2021-06-11 011.
 * @see //https://blog.csdn.net/yingaizhu/article/details/79699880
 *
 *
 * 往桌面创建快捷方式
 */
fun sendShortcut(context: Activity, name: String, icon: Bitmap?, goalActivity: String, intent: Intent) {
    intent.setAction(Intent.ACTION_VIEW);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        //  创建快捷方式的intent广播
        val shortcut = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
        // 添加快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        //  快捷图标是允许重复(不一定有效)
        shortcut.putExtra("duplicate", false)
        // 快捷图标
        // 使用资源id方式
//            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, R.mipmap.icon);
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // 使用Bitmap对象模式
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon)
        // 添加携带的下次启动要用的Intent信息
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
        // 发送广播
        context.sendBroadcast(shortcut)
    } else {
//        val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
//        val shortcutInfo = ShortcutInfo.Builder(context, name)
//            .setShortLabel(name)
//            .setIcon(Icon.createWithBitmap(icon))
//            .setIntent(intent)
//            .setLongLabel(name)
//            .build()
//        shortcutManager.requestPinShortcut(
//            shortcutInfo, PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT).intentSender
//        )


//        val shortcut = ShortcutInfoCompat.Builder(context, goalActivity)
//            .setShortLabel(name)
//            .setLongLabel(name)
//            .setIcon(IconCompat.createWithBitmap(icon))
//            .setIntent(intent)
//            .build()
//        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)


        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            val pinShortcutInfo = ShortcutInfoCompat.Builder(context, UUID.randomUUID().toString()).setShortLabel(name).setIntent(intent).setIcon(IconCompat.createWithBitmap(icon)).build()
            val successCallback = PendingIntent.getBroadcast(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
            ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo,successCallback.getIntentSender())
        }
    }
}
