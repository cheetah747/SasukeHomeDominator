package com.sibyl.sasukehomeDominator.util

import android.os.Build
import java.util.*

/**
 * @author Sasuke on 2019/6/23.
 */
class StaticVar {
    companion object {
        const val SCREEN_SHOT = "SCREEN_SHOT"
        const val LOCK_SCREEN = "LOCK_SCREEN"
        const val POWER_LONGPRESS = "POWER_LONGPRESS"
        const val SHARINGAN = "SHARINGAN"
        const val NOTIFI = "NOTIFI"


        const val LEFT = "LEFT"
        const val RIGHT = "RIGHT"
        const val CENTER = "CENTER"


        /**手机型号*/
        const val KEY_PHONE_MODEL = "KEY_PHONE_MODEL"

        /**用户名*/
        const val KEY_USER_NAME = "KEY_USER_NAME"

        /**万能瓷贴*/
        const val KEY_ANY_TILE = "KEY_ANY_TILE"

        /**目标activity是否是ROOT*/
        const val KEY_ANY_TILE_IS_ROOT = "KEY_ANY_TILE_IS_ROOT"

        /**水印开关*/
        const val KEY_IS_SHOW_WATERMARK = "KEY_IS_SHOW_WATERMARK"

        /**水印卡片开关*/
        const val KEY_IS_SHOW_WATER_CARD = "KEY_IS_SHOW_WATER_CARD"

        /**水印文字颜色*/
        const val KEY_WATER_TEXT_IS_BLACK = "KEY_WATER_TEXT_IS_BLACK"


        /**截图延时时长*/
        const val KEY_TIME_TO_SCRSHOT = "KEY_TIME_TO_SCRSHOT"

        /**用于Preference的键值*/
        const val KEY_SELECTED_ITEM = "KEY_SELECTED_ITEM"

        /**跳转到Accessibility时的type*/
        const val KEY_ACCESSIBILITY_TYPE = "KEY_ACCESSIBILITY"

        /**【瓷贴截屏】启动Service时传Intent的传值 */
        const val STRONG_SCRSHOT = "STRONG_SCRSHOT"

        /**【瓷贴锁屏】启动Service时传Intent的传值 */
        const val STRONG_LOCKSCREEN = "STRONG_LOCKSCREEN"

        /**【瓷贴长按电源键】启动Service时传Intent的传值 */
        const val STRONG_POWER_LONGPRESS = "STRONG_POWER_LONGPRESS"

        /**【万能瓷贴】启动Service时传Intent的传值 */
        const val STRONG_SHARINGAN = "STRONG_SHARINGAN"

        /**【展开通知栏】启动Service时传Intent的传值 */
        const val STRONG_NOTIFI = "STRONG_NOTIFI"

        /**根据监听到的截屏，来获取截图目录，因为很多手机不一样*/
        const val KEY_SCREEN_SHOT_DIR = "KEY_SCREEN_SHOT_DIR"

        /**水印位置设置*/
        const val KEY_POS_SELECT = "KEY_POS_SELECT"

        /**从瓷贴点击时发送的广播*/
        const val TILE_BROADCAST = "TILE_BROADCAST"


        /**手机型号*/
        var deviceModel = ""
            get() {
                if (arrayOf("xiaomi","redmi","oneplus","google","nexus","pixel","huawei","meizu","lenovo","oppo","vivo","honor","samsung","realme",
                        "nokia","nubia","smartisan"
                    ).any { it in Build.MODEL.toLowerCase(Locale.US) }){
                    return android.os.Build.MODEL
                }
                return android.os.Build.DEVICE
            }
    }
}