package com.sibyl.sasukehomeDominator.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

/**
 * @author Sasuke on 2021-06-06.
 */

/**Drawable转Bitmap*/
fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) {
        return null
    }
    // 取 drawable 的长宽
    val w = drawable.intrinsicWidth
    val h = drawable.intrinsicHeight
    // 取 drawable 的颜色格式
    val config =
        if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
    // 建立对应 bitmap
    val bitmap = Bitmap.createBitmap(w, h, config)
    // 建立对应 bitmap 的画布
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, w, h)
    // 把 drawable 内容画到画布中
    drawable.draw(canvas)
    return bitmap
}


/**bitmap转Bytes*/
fun bitmap2Bytes(bm: Bitmap?): ByteArray? {
    if (bm == null) {
        return null
    }
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
    return baos.toByteArray()
}