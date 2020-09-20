package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * @author Sasuke on 2020/9/20.
 */
object FileData {
        val fileProviderAuth = "com.sibyl.sasukehomeDominator.fileProvider"
        val sdStorageDir =  Environment.getExternalStorageDirectory().getAbsolutePath()

        //存放水印卡片缓存
        fun waterCardFile(context: Context) :File= File(context.externalCacheDir?.absolutePath + "/WATER_CARD.jpg")
}