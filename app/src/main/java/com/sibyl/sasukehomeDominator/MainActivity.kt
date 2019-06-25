package com.sibyl.sasukehomeDominator

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import com.sibyl.screenshotlistener.ScreenShotListenManager
import com.sibyl.screenshotlistener.WaterMarker
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {
    var checkDialog: AlertDialog? = null

    var manager: ScreenShotListenManager ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        setListeners()

        manager = ScreenShotListenManager.newInstance(applicationContext).apply {
            setListener(object : ScreenShotListenManager.OnScreenShotListener {
                override fun onShot(imagePath: String?) {
                    Log.i("SasukeLog","onShot")
                    doAsync {
                        Log.i("SasukeLog","doAsync")
                        Thread.sleep(1500)//有些垃圾系统截图时写入磁盘比较慢，所以这边要等一下。
                        WaterMarker(this@MainActivity).apply {
                            val bottomCard = drawWaterMark(imagePath, "Pixel 3XL @ WANGHAO", "2019-06-25 16:58")
                            var a = 1
//                        imagePath?.let {
//                            mergeScrShot2BottomCard(imagePath, bottomCard)
//                        }
                        }
                    }
                }
            })
        }

        manager?.startListen()
    }

    fun initUI() {
        (lockScreenCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.lock_screen_30dp)
        (lockScreenCard.findViewById<CardView>(R.id.cardText) as TextView).setText("スクリーンロック")

        (screenShotCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.screen_shot_30dp)
        (screenShotCard.findViewById<CardView>(R.id.cardText) as TextView).setText("スクリーンショット")

        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        changeBtnState(screenShotCard, selected == StaticVar.SCREEN_SHOT)
        changeBtnState(lockScreenCard, selected == StaticVar.LOCK_SCREEN)
    }

    /**根据选中状态不同，来显示不同颜色*/
    fun changeBtnState(cardView: View, isSelected: Boolean) {
        (cardView.findViewById<CardView>(R.id.cardIcon) as ImageView).setColorFilter(if (isSelected) Color.WHITE else Color.BLACK)
        (cardView.findViewById<CardView>(R.id.cardText) as TextView).setTextColor(if (isSelected) Color.WHITE else Color.BLACK)
        (cardView.findViewById<CardView>(R.id.cardContainer) as LinearLayout).setBackgroundColor(
            if (isSelected) resources.getColor(
                R.color.colorPrimary,
                null
            ) else Color.WHITE
        )
    }

    fun setListeners() {
        arrayOf(lockScreenCard, screenShotCard).forEach {
            it.setOnClickListener {
                PreferHelper.getInstance().setString(
                    StaticVar.KEY_SELECTED_ITEM,
                    when (it) {
                        lockScreenCard -> StaticVar.LOCK_SCREEN
                        screenShotCard -> StaticVar.SCREEN_SHOT
                        else -> ""
                    }
                )
                Snackbar.make(root, "【${it.findViewById<TextView>(R.id.cardText).text}】に設定しました", Snackbar.LENGTH_SHORT)
                    .show()
                initUI()//再刷新一下页面
            }
        }

    }

    override fun onResume() {
        super.onResume()
        //检查是否开启了辅助功能
        checkAccessibility()
    }

    fun checkAccessibility() {
        if (checkDialog == null) {
            checkDialog = AlertDialog.Builder(this).setMessage("本アプリは無障害の特性を利用するため、スイッチをONにして下さい")
                .setPositiveButton("今行く", DialogInterface.OnClickListener { dialog, which ->
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                })
                .setCancelable(false)
                .create()
        }

        if (!CheckAccessibility.isAccessibilitySettingsOn(this) && !checkDialog!!.isShowing) {
            checkDialog?.show()
        }
    }
}
