package com.sibyl.sasukehomeDominator.selectapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.sibyl.fuckwelcomeactivity.selectapp.adapter.AppListAdapter
import com.sibyl.fuckwelcomeactivity.selectapp.presenter.AppListPre
import com.sibyl.sasukehomeDominator.R
import com.sibyl.sasukehomeDominator.SasukeApplication
import com.sibyl.sasukehomeDominator.util.BaseActivity
import com.sibyl.sasukehomeDominator.util.CountDownDominator
import com.sibyl.sasukehomeDominator.util.LoadWaitDominator
import com.sibyl.sasukehomeDominator.util.onTextChanged
import kotlinx.android.synthetic.main.app_list_activity.*


/**
 * @author Sasuke on 2021-06-05.
 * 展示所有已安装应用
 */
class AppListActivity : BaseActivity() , CountDownDominator.CountDownCallback{
    val loadWait: LoadWaitDominator by lazy { LoadWaitDominator(this, rootLayout) }

    /**是否包含系统应用*/
    var isIncludeSystem = false

    val pre = AppListPre(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_list_activity)
        showBackButton()

        initUI()

    }

    fun initUI() {
        loadWait.show(LoadWaitDominator.LOADING)
        dataRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        //返回按钮
//        supportActionBar?.run {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeButtonEnabled(true)
//        }
        //加载全部数据&&刷新显示
        refreshAllDataRv()
        setListeners()
    }

    fun setListeners(){
        //搜索
        searchInput.onTextChanged {
            pre.startCountDownSearch()
        }
        searchInput.setOnEditorActionListener { textView, i, keyEvent -> /*KeyboardUtil.dismissKeyboard(this,searchInput);*/true }
    }

    /**刷新列表显示*/
    fun refreshAllDataRv() {
        SasukeApplication.app.executor.submit {
            pre.queryAllActivity(isIncludeSystem)
            //查找完，处理UI
            if (isFinishing) return@submit
            if (pre.packages.isEmpty()) {
                loadWait.show(LoadWaitDominator.NO_DATA)
                return@submit
            }
            //刷新界面显示
            runOnUiThread {
                dataRv.adapter = AppListAdapter(this).apply { setNewData(pre.realSearchApp("", isIncludeSystem)) }
                loadWait.show(LoadWaitDominator.DISMISS)
            }
        }


    }

    /**1秒延时后才真正开始搜索*/
    override fun doWorkWhenTimeUp(tag: Int) {
        if (isFinishing) return
        loadWait.show(LoadWaitDominator.LOADING)
        SasukeApplication.app.executor.submit {
            val newData = pre.realSearchApp(searchInput.text.toString(),isIncludeSystem)
            runOnUiThread {
                (dataRv.adapter as AppListAdapter).apply {
                    setNewData(newData)
                    notifyDataSetChanged()
                }
                loadWait.show(if (newData.isEmpty()) LoadWaitDominator.NO_DATA else LoadWaitDominator.DISMISS)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
//            android.R.id.home  ->{
//                finish()
//                return true
//            }
            R.id.isSysAppShow ->{
                item.isChecked = !item.isChecked
                isIncludeSystem = item.isChecked
                doWorkWhenTimeUp(0)
            }
            else ->{}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            if (data.getBooleanExtra("close",false)){
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        pre.counter?.cancel()
        super.onDestroy()
    }
}