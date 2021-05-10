package com.sibyl.sasukehomeDominator.shortcutactivity

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.sibyl.sasukehomeDominator.R
import com.sibyl.sasukehomeDominator.util.BaseActivity
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_SCREEN_SHOT_DIR
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileNotFoundException

/**
 * @author Sasuke on 2019-6-28 0028.
 * 修改文件名，*.png --->*.jpg
 */
class ChangeFileNameAct: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doAsync {
            val scrShotDir = File(PreferHelper.getInstance().getString(KEY_SCREEN_SHOT_DIR,
                Environment.getExternalStorageDirectory().path +"/Pictures/Screenshots/"))
            try {
                var count = 0
                scrShotDir.listFiles { dir, name ->
                    name.endsWith(".png")
                }.forEach {
                    count++
                    it.renameTo(File(it.path?.replace(".png",".jpg")))
                }
                uiThread { Toast.makeText(this@ChangeFileNameAct,if (count == 0) resources.getString(R.string.no_file_can_change)
                                            else "${count}${resources.getString(R.string.file_changed_success)}" ,Toast.LENGTH_SHORT).show() }
            }catch (e: FileNotFoundException){
                e.printStackTrace()
                uiThread { Toast.makeText(this@ChangeFileNameAct,e.toString(),Toast.LENGTH_SHORT).show() }
            }
            finish()
        }

    }
}