package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.content.DialogInterface
import android.os.Environment
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.sibyl.sasukehomeDominator.R
import org.jetbrains.anko.find
import java.io.File

/**
 * @author Sasuke on 2019-9-29 0029.
 * 紧急加的一个破烂选择路径弹窗
 */
class FolderSelectorDialog {
    companion object {
        @JvmStatic
        fun showDialog(context: Context, okCallback: (File) -> Unit) {
            val view = LayoutInflater.from(context).inflate(R.layout.folder_selector_layout, null)
            val listView = view.find<ListView>(R.id.fileLv)
            val goPre = view.find<TextView>(R.id.goPre)
            val currDirTv = view.find<TextView>(R.id.currDirTv)
            //先读取路径记录
            var currFileDir = File(
                PreferHelper.getInstance().getString(
                    StaticVar.KEY_SCREEN_SHOT_DIR,
                    Environment.getExternalStorageDirectory().path + "/Pictures/Screenshots/"
                )
            )
            //显示当前位置标题：
            currDirTv.text = "ディレクトリ：${currFileDir.canonicalPath}"

            //再填充列表
            listView.adapter = ArrayAdapter(
                context,
                android.R.layout.simple_expandable_list_item_1,
                currFileDir.list { file, str ->  !("." in str )&&file.isDirectory })

            //点击监听
            listView.setOnItemClickListener { adapterView, view, pos, id ->
                currFileDir = currFileDir.listFiles { file, str ->!("." in str )&&file.isDirectory }[pos]
                currDirTv.text = "ディレクトリ：${currFileDir.canonicalPath}"
                listView.adapter = ArrayAdapter(//妈的懒得抽出来了，直接复制粘贴，烦死了
                    context,
                    android.R.layout.simple_expandable_list_item_1,
                    currFileDir.list { file, str ->  !("." in str )&&file.isDirectory })
            }
            //上一级
            goPre.setOnClickListener {
                currFileDir = currFileDir.parentFile
                currDirTv.text = "ディレクトリ：${currFileDir.canonicalPath}"
                listView.adapter = ArrayAdapter(//妈的懒得抽出来了，直接复制粘贴，烦死了
                    context,
                    android.R.layout.simple_expandable_list_item_1,
                    currFileDir.list { file, str -> !("." in str )&&file.isDirectory })
            }

            androidx.appcompat.app.AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    okCallback.invoke(currFileDir)
                }).show()
        }
    }

}

