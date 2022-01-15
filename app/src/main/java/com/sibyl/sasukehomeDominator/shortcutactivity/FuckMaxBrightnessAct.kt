package com.sibyl.sasukehomeDominator.shortcutactivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.sibyl.sasukehomeDominator.R

import com.sibyl.sasukehomeDominator.util.*
import kotlinx.android.synthetic.main.activity_fuck_max_brightness.*


/**
 * @author Sasuke on 2022/1/15.
 */
/**
 * @author Sasuke on 2021-09-04.
 */
class FuckMaxBrightnessAct: BaseActivity() {
    var resumeCount = 0

    var brightness = -1
    var newBrightness = -1
    var newBrightnessText = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuck_max_brightness)

//        startService(Intent(this, SasukeAccessibilityService::class.java).apply {
//            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE, StaticVar.STRONG_SHARINGAN)
//        })
//        sendBroadcast(Intent().apply {
//            setAction(StaticVar.TILE_BROADCAST)
//            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE,StaticVar.STRONG_SHARINGAN)
//        })
        brightness = PreferHelper.getInstance().getInt(StaticVar.DEFAULT_BRIGHTNESS, StaticVar.BRIGHTNESS_SYSTEM)
        if (brightness == StaticVar.BRIGHTNESS_SYSTEM){
            brightness = getSystemBrightness(this)
        }
        newBrightness = brightness
        setAppScreenBrightness(this, brightness)
//        finish()
    }


    override fun onResume() {
        super.onResume()
        resumeCount++
        //如果是联动亮你妈，并且第二次onResume了，那就关闭掉（联动状态下，不要长时间占用屏幕）
        if (resumeCount >= 2 && PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_WITH_FUCK_BRIGHTNESS,false)){
            finish()
        }
    }

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    var x1 = 0f
    var x2 = 0f
    var y1 = 0f
    var y2 = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        when(event.action ){
            MotionEvent.ACTION_DOWN ->{
                //当手指按下的时候
                x1 = event.x
                y1 = event.y
            }
            MotionEvent.ACTION_MOVE ->{
                x2 = event.x
                y2 = event.y
                newBrightness = brightness + ((y1 - y2) / getScreenHeight() * 255f).toInt()
                if (newBrightness > 255f){
                    newBrightness = 255
                }else if (newBrightness < 0){
                    newBrightness = 0
                }
                //设置
                setAppScreenBrightness(this, newBrightness)
                //UI显示
                brightnessTv.visibility = View.VISIBLE
                newBrightnessText = "${String.format("%.0f", (newBrightness / 255f) * 100f)}%"
                brightnessTv.text = newBrightnessText
            }
            MotionEvent.ACTION_UP ->{
                //当手指离开的时候
                brightnessTv.visibility = View.GONE
                brightness = newBrightness
                PreferHelper.getInstance().setInt(StaticVar.DEFAULT_BRIGHTNESS,newBrightness)
            }
        }
        return super.onTouchEvent(event)
    }
}