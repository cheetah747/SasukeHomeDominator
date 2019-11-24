package com.sibyl.sasukehomeDominator

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import com.sibyl.sasukehomeDominator.util.BaseActivity
import com.sibyl.sasukehomeDominator.util.FolderSelectorDialog
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_SCREEN_SHOT_DIR
import kotlinx.android.synthetic.main.screen_shot_setting_act.*
import org.jetbrains.anko.find


/**
 * @author Sasuke on 2019-6-27 0027.
 */
class ScrShotSettingAct : BaseActivity() {
    companion object {
        const val SECONDS_0 = 0
        const val SECONDS_2 = 2000
        const val SECONDS_5 = 5000

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_shot_setting_act)

        initToolbar()
        //过渡动画
        initTransitionName()
        setListeners()
        initUI()
    }

    fun initTransitionName() {
        find<View>(R.id.toolbarBackView).transitionName = "cardView"
        find<View>(R.id.toolbarIcon).transitionName = "cardIcon"
        find<View>(R.id.toolbarText).transitionName = "cardText"
    }

    fun setListeners() {
        find<View>(R.id.toolbarIcon).setOnClickListener { onBackPressed() }
        //是否开启水印 点击监听
        waterMarkCheck.setOnCheckedChangeListener { compoundButton, isShow ->
            //            PreferHelper.getInstance().setBoolean(StaticVar.KEY_IS_SHOW_WATERMARK, isShow)
            waterMarkInputLayout.visibility = if (isShow) View.VISIBLE else View.GONE
        }
        //截屏延时 时长选取
        arrayOf(seconds0, seconds3, seconds5).forEach {
            it.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    refreshSecondsChecks(it)
                }
            }
        }
        //保存
        fab.setOnClickListener {
            PreferHelper.getInstance().run {
                //保存水印
                setBoolean(StaticVar.KEY_IS_SHOW_WATERMARK, waterMarkCheck.isChecked)
                //保存用户名
                setStringCommit(
                    StaticVar.KEY_USER_NAME,
                    userName.text.toString().trim().run { if (isNotBlank()) this else "Android ${android.os.Build.VERSION.RELEASE}" })
                //保存手机型号
                setStringCommit(
                    StaticVar.KEY_PHONE_MODEL,
                    phoneInfo.text.toString().trim().run { if (isNotBlank()) this else StaticVar.deviceModel })
                //保存截屏延迟
                setIntCommit(
                    StaticVar.KEY_TIME_TO_SCRSHOT, when (true) {
                        seconds0.isChecked -> SECONDS_0
                        seconds3.isChecked -> SECONDS_2
                        seconds5.isChecked -> SECONDS_5
                        else -> SECONDS_0
                    }
                )
            }
            Toast.makeText(this, resources.getString(R.string.setting_success_toast), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    fun initUI() {
        //用户名
//        userName.setText(PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME, "Android ${android.os.Build.VERSION.RELEASE}"))
//        userName.hint = "Android ${android.os.Build.VERSION.RELEASE}"
        userNameLayout.hint = "${resources.getString(R.string.water_mark_front)}(Android ${android.os.Build.VERSION.RELEASE})"
        PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME, "").run {
            userName.setText(if (isNotBlank()) this else "Android ${android.os.Build.VERSION.RELEASE}".apply {
                PreferHelper.getInstance()
                    .setStringCommit(StaticVar.KEY_USER_NAME, "Android ${android.os.Build.VERSION.RELEASE}")
            })
        }
        //手机型号
        phoneInfoLayout.hint = "${resources.getString(R.string.water_mark_end)}(${StaticVar.deviceModel})"
//        phoneInfo.hint = android.os.Build.MODEL
        PreferHelper.getInstance().getString(StaticVar.KEY_PHONE_MODEL, "").run {
            phoneInfo.setText(if (isNotBlank()) this else StaticVar.deviceModel.apply {
                PreferHelper.getInstance().setStringCommit(StaticVar.KEY_PHONE_MODEL, StaticVar.deviceModel)
            })
        }
        //水印开关
        waterMarkCheck.isChecked = PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATERMARK, true)

        //初始化秒数选择
        refreshSecondsChecks(
            when (PreferHelper.getInstance().getInt(StaticVar.KEY_TIME_TO_SCRSHOT, 0)) {
                SECONDS_0 -> seconds0
                SECONDS_2 -> seconds3
                SECONDS_5 -> seconds5
                else -> seconds0
            }
        )


        //针对安卓10，新增手动指定截屏路径（安卓10老子操你妈。。。。）
        dirSelectLayout.visibility = View.GONE/*if (android.os.Build.VERSION.RELEASE.toDouble() < 10) View.GONE else View.VISIBLE*/
        dirSelectTv.text = PreferHelper.getInstance().getString(
            KEY_SCREEN_SHOT_DIR,
            Environment.getExternalStorageDirectory().path + "/Pictures/Screenshots/"
        )
        //用户自行修改
        dirSelectTv.setOnClickListener {
            FolderSelectorDialog.showDialog(this) { file ->
                dirSelectTv.text = file.canonicalPath
                PreferHelper.getInstance().setString(KEY_SCREEN_SHOT_DIR, file.canonicalPath)
            }
        }
    }

    /**刷新秒数选择的单选框状态*/
    fun refreshSecondsChecks(checkBox: AppCompatCheckBox) {
        arrayOf(seconds0, seconds3, seconds5).forEach {
            it.isChecked = it == checkBox
        }
    }

    fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Either by calling
        supportActionBar!!.setTitle(null)
        // Or
        toolbar.setTitle(null)
        // Or
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            android.R.id.home -> {
//                supportFinishAfterTransition()
//                return true
//            }
//            android.R.id.title ->{
//                supportFinishAfterTransition()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}