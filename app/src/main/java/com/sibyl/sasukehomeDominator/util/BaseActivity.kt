package com.sibyl.sasukehomeDominator.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity



/**
 * @author HUANGSHI-PC on 2019-10-02 0002.
 */
open class BaseActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        //Public.Language 为 对应的资源格式后缀，比如"zh"      "
        super.attachBaseContext(MyContextWrapper.wrap(newBase, ""))
    }

}