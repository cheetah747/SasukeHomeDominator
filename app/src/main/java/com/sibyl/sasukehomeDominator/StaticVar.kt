package com.sibyl.sasukehomeDominator

/**
 * @author Sasuke on 2019/6/23.
 */
class StaticVar {
    companion object {
        const val SCREEN_SHOT = "SCREEN_SHOT"
        const val LOCK_SCREEN = "LOCK_SCREEN"

        /**用于Preference的键值*/
        const val KEY_SELECTED_ITEM = "KEY_SELECTED_ITEM"

        /**启动Service时传Intent的传值 */
        const val KEY_IS_FROM_SCRSHOT_TILE = "KEY_IS_FROM_SCRSHOT_TILE"
    }
}