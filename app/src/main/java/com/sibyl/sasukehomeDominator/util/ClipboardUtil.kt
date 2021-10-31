package com.sibyl.sasukehomeDominator.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/**
 * @author Sasuke on 2021/10/31.
 */
class ClipboardUtil {
    companion object{
        /**清空剪切板*/
        @JvmStatic
        fun clear(context: Context) = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
            ClipData.newPlainText("","")
        )
    }
}