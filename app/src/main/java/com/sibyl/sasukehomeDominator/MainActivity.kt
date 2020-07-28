package com.sibyl.sasukehomeDominator

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TypeEvaluator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.sibyl.sasukehomeDominator.util.BaseActivity
import com.sibyl.sasukehomeDominator.util.CheckAccessibility
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.find


class MainActivity : BaseActivity() {
    var checkDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI(false)
        setListeners()
        grantPermissions()

    }


    fun initUI(isAnime: Boolean) {
        //锁屏卡片
        (lockScreenCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.lock_screen_30dp)
        (lockScreenCard.findViewById<CardView>(R.id.cardText) as TextView).setText(resources.getString(R.string.screen_lock))

        //截屏卡片
        (screenShotCard.findViewById<AnimCardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.screen_shot_30dp)
        (screenShotCard.findViewById<AnimCardView>(R.id.cardText) as TextView).setText(resources.getString(R.string.screen_shot))

        //截屏设置卡片
        (screenShotSettingCard.findViewById<AnimCardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.screen_shot_setting_30dp)
        (screenShotSettingCard.findViewById<AnimCardView>(R.id.cardText) as TextView).run { setText(resources.getString(R.string.settings));setTextColor(resources.getColor(R.color.big_btn_text_color,null)) }
        (screenShotSettingCard.findViewById<AnimCardView>(R.id.cardContainer) as LinearLayout).setBackgroundColor(resources.getColor(R.color.red, null ) )

        //电源键长按卡片
        (powerLongPressCard.findViewById<CardView>(R.id.cardIcon) as ImageView).setImageResource(R.drawable.power_longpress_30dp)
        (powerLongPressCard.findViewById<CardView>(R.id.cardText) as TextView).setText(resources.getString(R.string.power_menu))

        //刷新卡片激活状态
        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        changeBtnState(screenShotCard, selected == StaticVar.SCREEN_SHOT)
        changeBtnState(lockScreenCard, selected == StaticVar.LOCK_SCREEN)
        changeBtnState(powerLongPressCard, selected == StaticVar.POWER_LONGPRESS)


        screenShotContainer.post {
            val widthAmount = screenShotContainer.measuredWidth
            //10是卡片的margin，存在3个或2个margin空隙，4是4等份，分配两个按钮
            val scrCardWidth =  if (selected == StaticVar.SCREEN_SHOT) (widthAmount - dip(10 * 3)) / 4 * 3 else widthAmount - dip(10 * 2)
            val scrSettingCardWidth = (widthAmount - dip(10 * 3)) / 4
            if (isAnime){//动画显示，则用动画
                animateCard(screenShotCard,screenShotCard.layoutParams.width,scrCardWidth)
            }else{//禁动画，则直接赋值
                screenShotSettingCard.run {layoutParams = layoutParams.apply { width = scrSettingCardWidth }}
                screenShotCard.run {layoutParams = layoutParams.apply { width = scrCardWidth}}
            }
        }

//        screenShotSettingCard.visibility = if (selected == StaticVar.SCREEN_SHOT) View.VISIBLE else View.GONE
    }

    fun animateCard(card: View ,startValue: Int, endValue: Int) =
        ObjectAnimator.ofObject(card,"paramWidth",object : TypeEvaluator<Int>{
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                return (startValue + (endValue - startValue) * fraction).toInt()
            }
        }, startValue, endValue).setDuration(300).start()

    fun ViewGroup.LayoutParams.setWidth(width: Int){
        this.width = width
    }

    /**根据选中状态不同，来显示不同颜色*/
    fun changeBtnState(cardView: View, isSelected: Boolean) {
        (cardView.findViewById<CardView>(R.id.cardIcon) as ImageView).setColorFilter(if (isSelected) resources.getColor(R.color.big_btn_text_color,null) else resources.getColor(R.color.black,null))
        (cardView.findViewById<CardView>(R.id.cardText) as TextView).setTextColor(if (isSelected) resources.getColor(R.color.big_btn_text_color,null) else resources.getColor(R.color.black,null))
        (cardView.findViewById<CardView>(R.id.cardContainer) as LinearLayout).setBackgroundColor(
            if (isSelected) resources.getColor(
                R.color.colorPrimary,
                null
            ) else resources.getColor(R.color.big_btn_color,null)
        )
    }

    fun setListeners() {
        arrayOf(lockScreenCard, screenShotCard, powerLongPressCard).forEach {
            it.setOnClickListener {
                PreferHelper.getInstance().setStringCommit(
                    StaticVar.KEY_SELECTED_ITEM,
                    when (it) {
                        lockScreenCard -> StaticVar.LOCK_SCREEN
                        screenShotCard -> StaticVar.SCREEN_SHOT
                        powerLongPressCard -> StaticVar.POWER_LONGPRESS
                        else -> ""
                    }
                )
                Snackbar.make(root, "【${it.findViewById<TextView>(R.id.cardText).text}】${resources.getString(R.string.setting_success)}", Snackbar.LENGTH_SHORT)
                    .show()
                initUI(true)//再刷新一下页面
            }
        }

        //截屏设置按钮
        screenShotSettingCard.setOnClickListener {
            val cardView: Pair<View, String> = Pair.create(screenShotSettingCard, "cardView")
            val cardIcon: Pair<View, String> = Pair.create(screenShotSettingCard.find(R.id.cardIcon), "cardIcon")
            val cardText: Pair<View, String> = Pair.create(screenShotSettingCard.find(R.id.cardText), "cardText")


            startActivity(
                Intent(this, ScrShotSettingAct::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, cardView, cardIcon, cardText).toBundle()
            )

//            val settingDialog = AlertDialog.Builder(this)
//                .setView(R.layout.scrshot_setting_dialog)
//                .create()
//            settingDialog.show()
//            settingDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
//            settingDialog.window.setWindowAnimations(R.style.alertDialogAnim)
        }
    }

    override fun onResume() {
        super.onResume()
        //检查是否开启了辅助功能
        checkAccessibility()
    }

    fun checkAccessibility() {
        if (checkDialog == null) {
            checkDialog = AlertDialog.Builder(this).setMessage(resources.getString(R.string.turn_on_dialog_alert))
                .setPositiveButton(resources.getString(R.string.go_now), { dialog, which ->
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
                })
                .setCancelable(false)
                .create()
        }

        if (!CheckAccessibility.isAccessibilitySettingsOn(this) && !checkDialog!!.isShowing) {
            checkDialog?.show()
        }
    }

    /**6.0的权限*/
    fun grantPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            XXPermissions.with(this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                //                    .permission(Permission.Group.STORAGE, Permission.Group.CALENDAR) //不指定权限则自动获取清单中的危险权限
                .permission(//存储
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    //电话
//                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE
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
                    /*Manifest.permission.REQUEST_INSTALL_PACKAGES*/
                )
                .request(object : OnPermission {
                    override fun hasPermission(granted: List<String>, isAll: Boolean) {}

                    override fun noPermission(denied: List<String>, quick: Boolean) {
                        if (quick) {
                            android.app.AlertDialog.Builder(this@MainActivity)
                                .setMessage(resources.getString(R.string.permission_allow))
                                .setPositiveButton(resources.getString(R.string.go_now)) { dialog, which -> XXPermissions.gotoPermissionSettings(this@MainActivity) }
                                .show()
                        }
                    }
                })
        }
    }
}
