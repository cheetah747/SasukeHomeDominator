package com.sibyl.sasukehomeDominator.util

/**
 * @author Sasuke on 2019/6/23.
 */
class StaticVar {
    companion object {
        const val SCREEN_SHOT = "SCREEN_SHOT"
        const val LOCK_SCREEN = "LOCK_SCREEN"
        const val POWER_LONGPRESS = "POWER_LONGPRESS"

        /**手机型号*/
        const val KEY_PHONE_MODEL = "KEY_PHONE_MODEL"

        /**用户名*/
        const val KEY_USER_NAME = "KEY_USER_NAME"

        /**水印开关*/
        const val KEY_IS_SHOW_WATERMARK = "KEY_IS_SHOW_WATERMARK"

        /**截图延时时长*/
        const val KEY_TIME_TO_SCRSHOT = "KEY_TIME_TO_SCRSHOT"

        /**用于Preference的键值*/
        const val KEY_SELECTED_ITEM = "KEY_SELECTED_ITEM"

        /**【瓷贴截屏】启动Service时传Intent的传值 */
        const val STRONG_SCRSHOT = "STRONG_SCRSHOT"

        /**【瓷贴锁屏】启动Service时传Intent的传值 */
        const val STRONG_LOCKSCREEN = "STRONG_LOCKSCREEN"

        /**【瓷贴长按电源键】启动Service时传Intent的传值 */
        const val STRONG_POWER_LONGPRESS = "STRONG_POWER_LONGPRESS"
    }
}