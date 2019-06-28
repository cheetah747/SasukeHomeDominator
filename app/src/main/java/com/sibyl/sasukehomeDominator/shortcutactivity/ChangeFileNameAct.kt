package com.sibyl.sasukehomeDominator.shortcutactivity

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_SCREEN_SHOT_DIR
import java.io.File
import java.io.FileNotFoundException

/**
 * @author Sasuke on 2019-6-28 0028.
 * 修改文件名，*.png --->*.jpg
 */
class ChangeFileNameAct: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            Toast.makeText(this,if (count == 0) "変換するファイルはありません"
                                                                else "${count} 個ファイル変換完了" ,Toast.LENGTH_LONG).show()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }
        finish()
    }
}