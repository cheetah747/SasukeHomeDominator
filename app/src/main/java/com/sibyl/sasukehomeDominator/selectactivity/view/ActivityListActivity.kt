package com.sibyl.fuckwelcomeactivity.selectactivity.view

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sibyl.fuckwelcomeactivity.selectactivity.adapter.ActivityListAdapter
import com.sibyl.fuckwelcomeactivity.selectapp.model.PackData
import com.sibyl.sasukehomeDominator.R
import com.sibyl.sasukehomeDominator.SasukeApplication
import com.sibyl.sasukehomeDominator.util.*
import kotlinx.android.synthetic.main.activity_list_activity.*
import java.util.*

/**
 * @author Sasuke on 2021-06-07.
 */
class ActivityListActivity: BaseActivity(), CountDownDominator.CountDownCallback {
    val loadWait: LoadWaitDominator by lazy { LoadWaitDominator(this, rootLayout) }

    val packData by lazy { intent.getSerializableExtra("packData") as PackData }

    var counter: CountDownDominator? = null
    var autoTimer: Timer? = null //用来在输入框发生改变时，自动倒数进行刷新的装置。
    private val SEARCH_PERIOD = 1 * 1000L//输入完成后过多少秒自动执行搜索

    var activities: List<ActivityInfo2>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_activity)
        showBackButton()


        initUI()
        setListeners()
    }

    fun initUI(){
        autoTimer = Timer()
        counter = CountDownDominator(this, this).apply { build(autoTimer) }
        loadWait.show(LoadWaitDominator.LOADING)
        dataRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        SasukeApplication.app.executor.submit {
            activities = getPkgActivities(this,packData.pkgName)
            runOnUiThread {
                if (isFinishing) return@runOnUiThread
                if (activities.isNullOrEmpty()){
                    loadWait.show(LoadWaitDominator.NO_DATA)
                }else{
                    dataRv.adapter = ActivityListAdapter(this).apply { setNewData(activities!!) }
                    loadWait.show(LoadWaitDominator.DISMISS)
                }
            }
        }

    }

    fun setListeners(){
        searchInput.onTextChanged {
            counter?.start(SEARCH_PERIOD)
        }
    }

    override fun doWorkWhenTimeUp(tag: Int) {
        activities?.let {
            loadWait.show(LoadWaitDominator.LOADING)
            (dataRv.adapter as ActivityListAdapter).run {
                val newList = it.filter { searchInput.text.toString().toLowerCase(Locale.getDefault()) in it.activityName.toLowerCase(Locale.getDefault()) }
                if (newList.isNullOrEmpty()){
                    loadWait.show(LoadWaitDominator.NO_DATA)
                }else{
                    setNewData(newList)
                    notifyDataSetChanged()
                    loadWait.show(LoadWaitDominator.DISMISS)
                }
            }
        }
    }

    override fun onDestroy() {
        counter?.cancel()
        super.onDestroy()
    }
}