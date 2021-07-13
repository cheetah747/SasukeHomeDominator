package com.sibyl.fuckwelcomeactivity.selectactivity.adapter

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sibyl.sasukehomeDominator.R
import com.sibyl.sasukehomeDominator.SasukeApplication
import com.sibyl.sasukehomeDominator.util.ActivityInfo2
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import com.sibyl.sasukehomeDominator.util.loadNoCache
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @author Sasuke on 2021-06-05.
 */
class ActivityListAdapter(val context: Activity): RecyclerView.Adapter<ActivityListAdapter.ActivityListVH>() {
    val dataList: MutableList<ActivityInfo2> = mutableListOf()
    var appIcon: ByteArray? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityListVH {
        return ActivityListVH(LayoutInflater.from(context).inflate(R.layout.activity_list_item,parent,false))
    }

    override fun getItemCount() = dataList?.size ?: 0

    override fun onBindViewHolder(holder: ActivityListVH, pos: Int) {
        val data = dataList?.get(pos)

        holder.apply {
            data?.let {
                containerLayout.setBackgroundColor(
                    context.resources.getColor(
                        if (data.exported) R.color.white else R.color.dark,
                        null
                    )
                )

                if (appIcon == null){
                    SasukeApplication.app.executor.submit {
                        appIcon = data.createAppIcon(context)
                        context.runOnUiThread { Glide.with(context).loadNoCache(appIcon).into(appIconIv) }
                    }
                }else{
                    Glide.with(context).loadNoCache(appIcon).into(appIconIv)
                }
                activityNameTv.text = data.activityName
                activityNameTv.setTextColor(context.getColor(if (data.exported) R.color.black else R.color.white))
                rootImg.visibility = if (data.exported) View.INVISIBLE else View.VISIBLE
                //点击关闭返回数据
                containerLayout.setOnClickListener {
                    PreferHelper.getInstance().setString(StaticVar.KEY_ANY_TILE,"${data.appInfo?.packageName}/${data.activityName}")
                    PreferHelper.getInstance().setBoolean(StaticVar.KEY_ANY_TILE_IS_ROOT, !data.exported)
                    context.setResult(RESULT_OK,Intent().putExtra("close",true))
                    context.finish()
                }
            }
        }
    }

    fun setNewData(newList: List<ActivityInfo2>){
        dataList?.clear()
        dataList?.addAll(newList)
    }

    class ActivityListVH(view: View): RecyclerView.ViewHolder(view){
        val appIconIv = view.findViewById<CircleImageView>(R.id.appIconIv)
        val activityNameTv = view.findViewById<TextView>(R.id.activityNameTv)
//        val rootLayout = view.findViewById<CardView>(R.id.rootLayout)
        val containerLayout = view.findViewById<LinearLayout>(R.id.containerLayout)
        var rootImg = view.findViewById<ImageView>(R.id.rootImg)
    }

}