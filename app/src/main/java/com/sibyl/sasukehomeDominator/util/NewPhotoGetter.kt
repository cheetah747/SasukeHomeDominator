package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.os.Handler
import android.provider.MediaStore

/**
 * @author Sasuke on 2019/9/29.
 */
/**获取最新一张图片*/
class NewPhotoGetter(val context: Context,val dealWork: (String) -> Unit) {
    var counter = 20

    var lastPathHistory = ""

    init {
        lastPathHistory = getLatest(context)
    }

    val handler by lazy { Handler() }

    /**循环检查*/
    fun checkAndDeal() {
        if (counter <= 0) return
        val latestPath = getLatest(context)
        if (latestPath != lastPathHistory && lastPathHistory.isNotBlank()) {//表示收到新图
            dealWork.invoke(latestPath)
            handler.removeCallbacksAndMessages(null)//既然得到了新图片，那消息队列里的消息就不需要了。
            return
        }
        //500毫秒循环发一次消息
        counter--
        handler.postDelayed({ checkAndDeal() }, 500)
    }

    companion object {
        @JvmStatic
        fun getLatest(context: Context): String {
            val mCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID
                ),
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " desc limit 1"
            )

            //读取扫描到的图片
            var pathTemp = ""
            if (mCursor.moveToNext()) {
                // 获取图片的路径
                val path = mCursor.getString(
                    mCursor.getColumnIndex(MediaStore.Images.Media.DATA)
                )
                pathTemp = path
                //获取图片名称
                val name = mCursor.getString(
                    mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                )
                //获取图片时间
                val time = mCursor.getLong(
                    mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                )
            }
            mCursor.close()
            return pathTemp
        }
    }
}
