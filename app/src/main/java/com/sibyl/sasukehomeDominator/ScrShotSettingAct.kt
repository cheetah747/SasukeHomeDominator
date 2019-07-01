package com.sibyl.sasukehomeDominator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import kotlinx.android.synthetic.main.screen_shot_setting_act.*
import org.jetbrains.anko.find


/**
 * @author Sasuke on 2019-6-27 0027.
 */
class ScrShotSettingAct : AppCompatActivity() {
    companion object {
        const val SECONDS_0 = 0
        const val SECONDS_3 = 3000
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
                setStringCommit(StaticVar.KEY_USER_NAME,userName.text.toString().trim())
                //保存手机型号
                setStringCommit(StaticVar.KEY_PHONE_MODEL,phoneInfo.text.toString().trim().run { if (isNotBlank()) this else android.os.Build.MODEL  })
                //保存截屏延迟
                setIntCommit(StaticVar.KEY_TIME_TO_SCRSHOT,when(true){
                    seconds0.isChecked -> SECONDS_0
                    seconds3.isChecked -> SECONDS_3
                    seconds5.isChecked -> SECONDS_5
                    else -> SECONDS_0
                })
            }
            Toast.makeText(this, "設定が保存されました", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    fun initUI() {
        //用户名
        userName.setText(PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME, ""))
        //手机型号
        phoneInfo.hint = android.os.Build.MODEL
        PreferHelper.getInstance().getString(StaticVar.KEY_PHONE_MODEL, "").run {
            phoneInfo.setText(if (isNotBlank()) this else android.os.Build.MODEL.apply {
                PreferHelper.getInstance().setStringCommit(StaticVar.KEY_PHONE_MODEL, android.os.Build.MODEL)
            })
        }
        //水印开关
        waterMarkCheck.isChecked = PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATERMARK, false)

        //初始化秒数选择
        refreshSecondsChecks(
            when (PreferHelper.getInstance().getInt(StaticVar.KEY_TIME_TO_SCRSHOT, 0)) {
                SECONDS_0 -> seconds0
                SECONDS_3 -> seconds3
                SECONDS_5 -> seconds5
                else -> seconds0
            }
        )
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