package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.sibyl.sasukehomeDominator.R


/**
 * @author HUANGSHI-PC on 2019-10-02 0002.
 */
open class BaseActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        //Public.Language 为 对应的资源格式后缀，比如"zh"      "
        super.attachBaseContext(MyContextWrapper.wrap(newBase, ""))
    }

    fun showBackButton(){
        findViewById<Toolbar>(R.id.toolbar)?.let {
            setSupportActionBar(it.apply {
                contentInsetStartWithNavigation = 0//真的烦，为了去掉那个自带箭头的留白
            })
        }
        //返回按钮
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //着色导航栏
        getWindow().setNavigationBarColor(resources.getColor(R.color.main_activity_background_color,null))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home  ->{
                finish()
                return true
            }
            else ->{}
        }
        return super.onOptionsItemSelected(item)
    }

    //适配黑暗模式 https://www.jianshu.com/p/ba62cbcc9075
    //当更改系统主题设置后，执行这里。
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 切换到 深色主题
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // 需调用 recreate() ，从而使更改生效
        recreate()

//        newConfig?.let {
//            val mSysThemeConfig = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
//            when (mSysThemeConfig) {
//                // 亮色主题
//                Configuration.UI_MODE_NIGHT_NO -> {
//                    // 切换到 深色主题
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                    // 需调用 recreate() ，从而使更改生效
//                    recreate()
//                }
//                // 深色主题
//                Configuration.UI_MODE_NIGHT_YES -> {
//                    // 切换到 深色主题
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    // 需调用 recreate() ，从而使更改生效
//                    recreate()
//                }
//            }
//        }
    }
}