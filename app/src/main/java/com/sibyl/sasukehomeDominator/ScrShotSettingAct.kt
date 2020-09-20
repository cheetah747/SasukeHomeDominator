package com.sibyl.sasukehomeDominator

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sibyl.sasukehomeDominator.util.*
import com.sibyl.sasukehomeDominator.util.PhotoPickDominator.CROP_REQUEST
import com.sibyl.sasukehomeDominator.util.PhotoPickDominator.PHOTO_REQUEST_GALLERY
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.CENTER
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_SCREEN_SHOT_DIR
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.LEFT
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.RIGHT
import com.sibyl.screenshotlistener.WaterMarker
import kotlinx.android.synthetic.main.screen_shot_setting_act.*
import org.jetbrains.anko.find
import java.io.File
import java.nio.file.Files


/**
 * @author Sasuke on 2019-6-27 0027.
 */
class ScrShotSettingAct : BaseActivity() {
    companion object {
        const val SECONDS_0 = 0
        const val SECONDS_1 = 1000
        const val SECONDS_3 = 3000

        //水印卡片的高度倍数
        const val IMG_CARD_HEIGHT_FACTORS = 8f

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


    //图片选择器
    val imgPicker by lazy { PhotoPickDominator(this) }

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

        //是否开启水印卡片背景
        waterCardCheck.setOnCheckedChangeListener { buttonView, isShow ->
            waterCardImg.visibility = if (isShow) View.VISIBLE else View.GONE
        }

        //添加水印卡片图片
        waterCardImg.setOnClickListener {
            imgPicker.selectByLocal(PhotoPickDominator.PHOTO_REQUEST_GALLERY)
        }

        //截屏延时 时长选取
        arrayOf(seconds0, seconds1, seconds3).forEach {
            it.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    refreshSecondsChecks(it)
                }
            }
        }
        //监听文字变化
        arrayOf(userName, phoneInfo).forEach {
            it.afterTextChanged {
                refreshAtTextVisibility()
            }
        }
        //保存
        fab.setOnClickListener {
            PreferHelper.getInstance().run {
                //保存水印
                setBoolean(StaticVar.KEY_IS_SHOW_WATERMARK, waterMarkCheck.isChecked)
                //保存水印位置
                PreferHelper.getInstance().setStringCommit(
                    StaticVar.KEY_POS_SELECT, when (true) {
                        leftSelect.background != null -> StaticVar.LEFT
                        centerSelect.background != null -> StaticVar.CENTER
                        else -> StaticVar.RIGHT
                    }
                )
                //保存用户名
                setStringCommit(
                    StaticVar.KEY_USER_NAME,
                    userName.text.toString()
                        .run { if (isNotEmpty()) this else "Android ${android.os.Build.VERSION.RELEASE}" })
                //保存手机型号
                setStringCommit(
                    StaticVar.KEY_PHONE_MODEL,
                    phoneInfo.text.toString()
                        .run { if (isNotEmpty()) this else StaticVar.deviceModel })
                //保存水印卡片开关
                setBoolean(StaticVar.KEY_IS_SHOW_WATER_CARD, waterCardCheck.isChecked)
                //保存截屏延迟
                setIntCommit(
                    StaticVar.KEY_TIME_TO_SCRSHOT, when (true) {
                        seconds0.isChecked -> SECONDS_0
                        seconds1.isChecked -> SECONDS_1
                        seconds3.isChecked -> SECONDS_3
                        else -> SECONDS_0
                    }
                )
            }
            Toast.makeText(
                this,
                resources.getString(R.string.setting_success_toast),
                Toast.LENGTH_SHORT
            ).show()
            onBackPressed()
        }

        //选择水印位置
        arrayOf(rightSelect, centerSelect, leftSelect).forEach {
            it.setOnClickListener {
                //先刷新显示状态
                val pos = when (it) {
                    leftSelect -> LEFT
                    centerSelect -> CENTER
                    else -> RIGHT
                }
                refreshPosSelectState(pos)
//                //改记录
//                PreferHelper.getInstance().setStringCommit(StaticVar.KEY_POS_SELECT, pos)
            }
        }
    }

    /**根据填写内容来判断如何显示@字符的颜色*/
    fun refreshAtTextVisibility() {
        val isHideAt = arrayOf(userName, phoneInfo).any { it.text.isBlank() && it.text.isNotEmpty() }
        atTextView.visibility = if (isHideAt) View.GONE else View.VISIBLE
    }

    fun initUI() {
        //用户名
//        userName.setText(PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME, "Android ${android.os.Build.VERSION.RELEASE}"))
//        userName.hint = "Android ${android.os.Build.VERSION.RELEASE}"
        userNameLayout.hint = "Android ${android.os.Build.VERSION.RELEASE}"//"${resources.getString(R.string.water_mark_front)}(Android ${android.os.Build.VERSION.RELEASE})"
        PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME, "").run {
            userName.setText(if (isNotEmpty()) this else "Android ${android.os.Build.VERSION.RELEASE}".apply {
                PreferHelper.getInstance()
                    .setStringCommit(
                        StaticVar.KEY_USER_NAME,
                        "Android ${android.os.Build.VERSION.RELEASE}"
                    )
            })
        }

        //水印位置（默认右边
        val posSelect =
            PreferHelper.getInstance().getString(StaticVar.KEY_POS_SELECT, StaticVar.RIGHT)
        refreshPosSelectState(posSelect)
        //手机型号
        phoneInfoLayout.hint = "${StaticVar.deviceModel}"//"${resources.getString(R.string.water_mark_end)}(${StaticVar.deviceModel})"
//        phoneInfo.hint = android.os.Build.MODEL
        PreferHelper.getInstance().getString(StaticVar.KEY_PHONE_MODEL, "").run {
            phoneInfo.setText(if (isNotEmpty()) this else StaticVar.deviceModel.apply {
                PreferHelper.getInstance()
                    .setStringCommit(StaticVar.KEY_PHONE_MODEL, StaticVar.deviceModel)
            })
        }
        //水印开关
        waterMarkCheck.isChecked = PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATERMARK, true)

        //水印卡片开关
        waterCardCheck.isChecked = PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATER_CARD, false)
        FileData.waterCardFile(this).takeIf { it.exists() }?.let {
            Glide.with(this).load(it)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(waterCardImg)
        }

        //初始化秒数选择
        refreshSecondsChecks(
            when (PreferHelper.getInstance().getInt(StaticVar.KEY_TIME_TO_SCRSHOT, 0)) {
                SECONDS_0 -> seconds0
                SECONDS_1 -> seconds1
                SECONDS_3 -> seconds3
                else -> seconds0
            }
        )
        //根据填写内容来判断如何显示@字符的颜色
        refreshAtTextVisibility()
        //水印卡片（高度设为宽的8分之一）
        setWaterCardHeight()

        //针对安卓10，新增手动指定截屏路径（安卓10老子操你妈。。。。）
//        dirSelectLayout.visibility = if (android.os.Build.VERSION.RELEASE.toDouble() < 10) View.GONE else View.VISIBLE
//        dirSelectTv.text = PreferHelper.getInstance().getString(
//            KEY_SCREEN_SHOT_DIR,
//            Environment.getExternalStorageDirectory().path + "/Pictures/Screenshots/"
//        )
//        //用户自行修改
//        dirSelectTv.setOnClickListener {
//            FolderSelectorDialog.showDialog(this) { file ->
//                dirSelectTv.text = file.canonicalPath
//                PreferHelper.getInstance().setString(KEY_SCREEN_SHOT_DIR, file.canonicalPath)
//            }
//        }
    }

    //设置水印卡片背景的自定义高度
    private fun setWaterCardHeight() {
        waterCardImg.post {
            waterCardImg.layoutParams =waterCardImg.layoutParams.apply { height = (waterCardImg.measuredWidth / IMG_CARD_HEIGHT_FACTORS).toInt() }
            waterCardImg.visibility = if (waterCardCheck.isChecked) View.VISIBLE else View.GONE
        }
    }

    /**刷新位置选择显示状态*/
    fun refreshPosSelectState(pos: String) {
        leftSelect.background = null
        centerSelect.background = null
        rightSelect.background = null
        when (pos) {
            LEFT -> leftSelect
            CENTER -> centerSelect
            else -> rightSelect
        }.background = resources.getDrawable(R.drawable.rect_white_select, null)
    }

    var photoPathTemp = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //裁剪返回======================================
            CROP_REQUEST ->{
//                if (data != null) {
//                    var photo: Bitmap? = null
//                    if (data.extras != null) {
//                        photo = data.extras.get("data") as Bitmap?
//                    }
//                    if (photo == null && data.data != null) {
//                        photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.data))
//                    }
//                    //删掉中间缓存图片
//                    photoPathTemp?.takeIf { File(it).exists() }?.let { File(it).delete() }
//                    Handler().postDelayed({
//                    }, 800)
//                }
            }
            //选图返回======================================
            else ->{
                photoPathTemp = imgPicker.onPhotoPathResult(this, requestCode, resultCode, data)
                photoPathTemp?.takeIf { it.isNotBlank() }?.let {
                    val photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(File(it).toUri()),null,BitmapFactory.Options().apply {
                        inMutable = true
                    })
                    val cardBmp = WaterMarker.trasBmp2Card(photo, IMG_CARD_HEIGHT_FACTORS)
                    photoPathTemp?.takeIf { File(it).exists() }?.let { File(it).delete() }
                    FileCache.saveBitmap(cardBmp,FileData.waterCardFile(this))
                    waterCardImg.setImageBitmap(cardBmp)

//                    imgPicker?.cropPhoto(this,
//                        FuckGoogleAdaptUtil.android7AdaptUri(
//                            this,
//                            FileData.fileProviderAuth, File(photoPathTemp)
//                        ),
//                        if (requestCode == PHOTO_REQUEST_GALLERY) Resources.getSystem().displayMetrics.widthPixels else 1,
//                        if (requestCode == PHOTO_REQUEST_GALLERY) Resources.getSystem().displayMetrics.widthPixels / IMG_CARD_HEIGHT_FACTORS else 1
//                    )
                }
            }
        }

    }

    /**刷新秒数选择的单选框状态*/
    fun refreshSecondsChecks(checkBox: AppCompatCheckBox) {
        arrayOf(seconds0, seconds1, seconds3).forEach {
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