package com.sibyl.sasukehomeDominator

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var checkDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        setListeners()
        grantPermissions()

    }

    fun initUI() {
        (lockScreenCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.lock_screen_30dp)
        (lockScreenCard.findViewById<CardView>(R.id.cardText) as TextView).setText("スクリーンロック")

        (screenShotCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.screen_shot_30dp)
        (screenShotCard.findViewById<CardView>(R.id.cardText) as TextView).setText("スクリーンショット")

        (powerLongPressCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.power_longpress_30dp)
        (powerLongPressCard.findViewById<CardView>(R.id.cardText) as TextView).setText("パワー長押し")

        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        changeBtnState(screenShotCard, selected == StaticVar.SCREEN_SHOT)
        changeBtnState(lockScreenCard, selected == StaticVar.LOCK_SCREEN)
        changeBtnState(powerLongPressCard, selected == StaticVar.POWER_LONGPRESS)
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
        arrayOf(lockScreenCard, screenShotCard,powerLongPressCard).forEach {
            it.setOnClickListener {
                PreferHelper.getInstance().setString(
                    StaticVar.KEY_SELECTED_ITEM,
                    when (it) {
                        lockScreenCard -> StaticVar.LOCK_SCREEN
                        screenShotCard -> StaticVar.SCREEN_SHOT
                        powerLongPressCard ->StaticVar.POWER_LONGPRESS
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

    /**6.0的权限*/
    fun grantPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            XXPermissions.with(this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                //                    .permission(Permission.Group.STORAGE, Permission.Group.CALENDAR) //不指定权限则自动获取清单中的危险权限
                .permission(//存储
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
//                    //电话
//                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.READ_PHONE_STATE,
//                    //短信
//                    Manifest.permission.SEND_SMS,
//                    //通讯录
//                    Manifest.permission.READ_PHONE_NUMBERS,
//                    Manifest.permission.GET_ACCOUNTS,
//                    //定位
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    //相机
//                    Manifest.permission.CAMERA

//                                Manifest.permission.CHANGE_NETWORK_STATE,
//                                Manifest.permission.WRITE_SETTINGS
                    //安装APK
                    /*Manifest.permission.REQUEST_INSTALL_PACKAGES*/)
                .request(object : OnPermission {
                    override fun hasPermission(granted: List<String>, isAll: Boolean) {}

                    override fun noPermission(denied: List<String>, quick: Boolean) {
                        if (quick) {
                            android.app.AlertDialog.Builder(this@MainActivity)
                                .setMessage("パーミッションを許可してください！")
                                .setPositiveButton("今行く") { dialog, which -> XXPermissions.gotoPermissionSettings(this@MainActivity) }
                                .show()
                        }
                    }
                })
        }
    }
}