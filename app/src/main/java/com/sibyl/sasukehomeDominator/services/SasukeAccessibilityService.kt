package com.sibyl.sasukehomeDominator.services

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import com.sibyl.sasukehomeDominator.util.*
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_IS_SHOW_WATERMARK
import com.sibyl.sasukehomeDominator.util.StaticVar.Companion.KEY_SCREEN_SHOT_DIR
import com.sibyl.screenshotlistener.ScreenShotListenManager
import com.sibyl.screenshotlistener.WaterMarker
import org.jetbrains.anko.doAsync
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Sasuke on 2019/6/22.
 */
class SasukeAccessibilityService : AccessibilityService() {
//    private var mContext: Context? = null

    val manager: ScreenShotListenManager by lazy {
        ScreenShotListenManager.newInstance(this).apply {
            setListener { imagePath: String? ->
                screenShotCallback(imagePath)
            }
        }
    }

    val handler by lazy { Handler() }
    //跳转封装
    val jumpWrapper by lazy { JumpWrapper(this) }

//    val waterMarkTypeface by lazy { TextView(this).typeface }


    override fun onCreate() {
        super.onCreate()
//        mContext = applicationContext
        manager.startListen()
//        EventBus.getDefault().register(this)
    }





    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {



        //截图延时
        val scrShotDelay = PreferHelper.getInstance().getInt(StaticVar.KEY_TIME_TO_SCRSHOT, 0).toLong()

        //从通知栏瓷贴点击过来的（默认false）
        when (intent.getStringExtra(StaticVar.KEY_ACCESSIBILITY_TYPE)) {
            //是从瓷贴截屏按钮点击过来的，就强行执行，忽略掉selected主界面的选择
            StaticVar.STRONG_SCRSHOT -> {
//                if (android.os.Build.VERSION.RELEASE.toDouble() >= 10) {//专门为安卓10开启循环检测
//                    NewPhotoGetter(this, { imagePath: String -> screenShotCallback(imagePath) }).checkAndDeal()
//                }
                handler.postDelayed(
                    { performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) },
                    500 + scrShotDelay/*(if (scrShotDelay != 0L) scrShotDelay else 0)*/
                );return Service.START_STICKY
            }
            StaticVar.STRONG_LOCKSCREEN -> {performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);return Service.START_STICKY }
            StaticVar.STRONG_POWER_LONGPRESS -> { performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);return Service.START_STICKY }
            StaticVar.STRONG_SHARINGAN ->{ jumpWrapper.jump();return Service.START_STICKY  }
        }

        var selected = PreferHelper.getInstance().getString(StaticVar.KEY_SELECTED_ITEM)
        //常规长按HOME键过来的
        when (selected) {
            StaticVar.LOCK_SCREEN -> performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            StaticVar.SCREEN_SHOT -> {
//                if (android.os.Build.VERSION.RELEASE.toDouble() >= 10) {//专门为安卓10开启循环检测
//                    NewPhotoGetter(this, { imagePath: String -> screenShotCallback(imagePath) }).checkAndDeal()
//                }
                handler.postDelayed( { performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT) },
                    /*(if(android.os.Build.VERSION.RELEASE.toDouble() >= 10) 700 else 1500)*/if (scrShotDelay == 1000L) 1500 else (700 + scrShotDelay)//安卓10的语音助手比较快，不需要1500秒
                )
            }
            StaticVar.POWER_LONGPRESS -> performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
            StaticVar.SHARINGAN -> jumpWrapper.jump()
        }
        return Service.START_STICKY
    }

    /**截图监听响应回调*/
    fun screenShotCallback(imagePath: String?){
        //记录下当前手机的截图目录
        PreferHelper.getInstance().setString(KEY_SCREEN_SHOT_DIR, File(imagePath).parent)
        //如果水印开关关掉了，那就不画水印
        if (!PreferHelper.getInstance().getBoolean(KEY_IS_SHOW_WATERMARK, true)) return
        doAsync {
//            Thread.sleep(1500)//有些垃圾系统截图时写入磁盘比较慢，所以这边要等一下。
            WaterMarker(this@SasukeAccessibilityService,TextView(this@SasukeAccessibilityService).typeface).run {
                var userName = PreferHelper.getInstance().getString(StaticVar.KEY_USER_NAME,"Android ${android.os.Build.VERSION.RELEASE}")
                                                            .run { if (isNotBlank()) "${this}@" else "" }
                val phoneModel = PreferHelper.getInstance().getString(StaticVar.KEY_PHONE_MODEL, StaticVar.deviceModel)
                if (phoneModel.isBlank() && userName.endsWith("@")) userName = userName.substring(0,userName.length - 1)

                val phoneInfo = userName + phoneModel
                //开始把水印画上去
                val psResultShot =
                    drawWaterMark(imagePath, phoneInfo, SimpleDateFormat("yyyy-MM-dd  HH:mm:ss",Locale.ENGLISH).format(Date()))
                //画完了，保存（如果为空就不保存）
                //不知道为什么，一截屏马上就删除的话，就很容易未响应。。这里加一个 doAsync 会稍微好一点。
                doAsync { psResultShot?.let { saveBmp2File(it, File(imagePath), getFormatByPath(imagePath))} }
//                        File(imagePath).renameTo(File(imagePath?.replace(".png",".jpg")))//改文件名，png改成jpg

//                        imagePath?.let {
//                            mergeScrShot2BottomCard(imagePath, bottomCard)
//                        }
            }
        }
    }


    fun getFormatByPath(imagePath: String?) = imagePath?.let {
        when(File(it).extension.toUpperCase(Locale.getDefault())) {
            "PNG" -> Bitmap.CompressFormat.PNG
            "WEBP" ->Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }
    } ?: Bitmap.CompressFormat.JPEG


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        //锁屏(安卓9.0适用)
//        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        //按下返回键
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_BACK)
        //向下拉出状态栏
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
        //向下拉出状态栏并显示出所有的快捷操作按钮
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
        //按下HOME键
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_HOME)
        //显示最近任务
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_RECENTS)
        //长按电源键
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        //分屏
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
        //截屏(安卓9.0适用)
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
        //打开快速设置
//        accessibilityService.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        handler.removeCallbacksAndMessages(null)
        return super.onUnbind(intent)
    }

    //    class LockScreenMsg


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: LockScreenMsg) {
//
//    }
}