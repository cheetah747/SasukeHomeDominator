package com.sibyl.sasukehomeDominator

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.sibyl.sasukehomeDominator.selectapp.view.AppListActivity
import com.sibyl.sasukehomeDominator.util.BaseActivity
import com.sibyl.sasukehomeDominator.util.CheckAccessibility
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sharingan_dialog_view.view.*
import org.jetbrains.anko.*


class MainActivity : BaseActivity() {
    var checkDialog: AlertDialog? = null

    var pre: MainPre? = null

    val sharinganDialogView by lazy {
        LayoutInflater.from(this).inflate(R.layout.sharingan_dialog_view,null).apply {
            //点击跳转选取APP
            findViewById<CardView>(R.id.selectActivityCard).setOnClickListener {
                startActivity(Intent(this@MainActivity, AppListActivity::class.java))
            }
            //点击切换联动【亮你妈】状态
            findViewById<CardView>(R.id.withFuckBrightness). setOnClickListener {
                    val newState = !PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_WITH_FUCK_BRIGHTNESS, false)
                    PreferHelper.getInstance().setBoolean(StaticVar.KEY_IS_WITH_FUCK_BRIGHTNESS, newState)
                    (it as CardView).setCardBackgroundColor(if (newState) getColor(R.color.progressbar_color) else getColor(R.color.gray))
                    showSnackbar(if (newState) getString(R.string.fuck_brightness_setting_success) else getString(R.string.fuck_brightness_setting_cancel) )
            }
            //长按按钮清除设置
            findViewById<CardView>(R.id.selectActivityCard).setOnLongClickListener {
                    PreferHelper.getInstance().setString(StaticVar.KEY_ANY_TILE,"")
                    findViewById<TextView>(R.id.selectActivityTv).text = getString(R.string.select_activity)
                    findViewById<TextView>(R.id.appNameTv).visibility = View.GONE
                    findViewById<CircleImageView>(R.id.appIconIv).setImageResource(R.color.gray)
                    true
            }
        }
    }

    val sharinganDialog by lazy {
        AlertDialog.Builder(this)
            .setCancelable(true)
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pre = MainPre(this,listOf(
            lockScreenCard.apply { setTag(StaticVar.LOCK_SCREEN) },
            screenShotCard.apply { setTag(StaticVar.SCREEN_SHOT) },
            screenShotSettingCard,
            powerLongPressCard.apply { setTag(StaticVar.POWER_LONGPRESS) },
            sharinganCard.apply { setTag(StaticVar.SHARINGAN) },
            sharinganSettingCard,
            fuckBrightnessCard.apply { setTag(StaticVar.FUCKBRIGHTNESS) },
            notifiCard.apply { setTag(StaticVar.NOTIFI) }
        ))

        setupUI()
        setListeners()
        refreshUIbySelected(false)
        grantPermissions()

    }

    fun setupUI(){
        pre?.setupCardView()
    }


    fun refreshUIbySelected(isAnime: Boolean) {
        //刷新卡片激活状态
        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        arrayOf(screenShotCard,lockScreenCard,powerLongPressCard,sharinganCard,fuckBrightnessCard,notifiCard).forEach {
            changeBtnColor(it,selected == it.tag as String)
        }


        arrayOf(screenShotCard, sharinganCard).forEach {
            calculateHasSettingCards(it,selected,isAnime)
        }

//        screenShotSettingCard.visibility = if (selected == StaticVar.SCREEN_SHOT) View.VISIBLE else View.GONE
    }

    private fun calculateHasSettingCards(cardView: View, selected: String,isAnime: Boolean){
        cardView.post {
            val widthAmount = ((cardView.parent) as LinearLayout).measuredWidth
            //10是卡片的margin，存在3个或2个margin空隙，4是4等份，分配两个按钮
            val cardWidth =  if (selected == cardView.tag as String) (widthAmount - dip(10 * 3)) / 4 * 3 else widthAmount - dip(10 * 2)
            val settingCardWidth = (widthAmount - dip(10 * 3)) / 4
            if (isAnime){//动画显示，则用动画
                animateCard(cardView,cardView.layoutParams.width,cardWidth)
            }else{//禁动画，则直接赋值
                ((cardView.parent) as LinearLayout).getChildAt(1).run {layoutParams = layoutParams.apply { width = settingCardWidth }}
                cardView.run {layoutParams = layoutParams.apply { width = cardWidth}}
            }
        }
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
    fun changeBtnColor(cardView: View, isSelected: Boolean) {
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
        arrayOf(lockScreenCard, screenShotCard, powerLongPressCard, sharinganCard,fuckBrightnessCard,notifiCard).forEach {
            it.setOnClickListener {
                PreferHelper.getInstance().setStringCommit(
                    StaticVar.KEY_SELECTED_ITEM,
                    it.tag as String
                )
                showSnackbar("【${it.findViewById<TextView>(R.id.cardText).text}】${resources.getString(R.string.setting_success)}")
                refreshUIbySelected(true)//再刷新一下页面
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

        //写轮眼
        sharinganSettingCard.setOnClickListener {
            refreshSharinganDialogData()
            sharinganDialog.show()
            sharinganDialog.window?.apply {
                setContentView(sharinganDialogView)
                attributes = attributes.apply { width = dip(300) }
                refreshSharinganDialogData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //检查是否开启了辅助功能
        checkAccessibility()
        if (sharinganDialog.isShowing){
            refreshSharinganDialogData()
        }

    }

    fun refreshSharinganDialogData(){
        PreferHelper.getInstance().getString(StaticVar.KEY_ANY_TILE, "")?.takeIf { it.isNotEmpty() && it.split("/").size == 2 }?.run {
            val pkgName =  this.split("/")[0]
            val activityName = this.split(".").run { this[this.size - 1] }
            val isRoot = PreferHelper.getInstance().getBoolean(StaticVar.KEY_ANY_TILE_IS_ROOT,false)

            //显示按钮文字
            sharinganDialogView.findViewById<TextView>(R.id.selectActivityTv).apply {
                text = activityName
            }
            //设置 联动亮你妈
            sharinganDialogView.findViewById<CardView>(R.id.withFuckBrightness).apply {
                val withFuckBrightness = PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_WITH_FUCK_BRIGHTNESS, false)
                setCardBackgroundColor(if (withFuckBrightness) getColor(R.color.progressbar_color) else getColor(R.color.gray))
            }
            //显示ROOT背景
            sharinganDialogView.findViewById<ImageView>(R.id.rootImg).visibility = if (isRoot) View.VISIBLE else View.GONE
            //显示按钮颜色
            sharinganDialogView.findViewById<CardView>(R.id.selectActivityCard).setCardBackgroundColor(resources.getColor(if (isRoot) R.color.dark else R.color.progressbar_color,null))
            //显示图标
            doAsync {
                val iconDrawable = packageManager.getPackageInfo(pkgName, 0).applicationInfo.loadIcon(packageManager)
                val appName = packageManager.getPackageInfo(pkgName, 0).applicationInfo.loadLabel(packageManager).toString()
                val iconImg = sharinganDialogView.findViewById<CircleImageView>(R.id.appIconIv)
                uiThread {
                    //显示APP名称
                    sharinganDialogView.findViewById<TextView>(R.id.appNameTv).apply {
                        text = appName
                        visibility = View.VISIBLE
                    }
                    Glide.with(this@MainActivity)
                        .load(iconDrawable)
                        .into(iconImg)
                }
            }
        }
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
