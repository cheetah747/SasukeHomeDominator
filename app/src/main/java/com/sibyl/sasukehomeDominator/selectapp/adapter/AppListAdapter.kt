package com.sibyl.fuckwelcomeactivity.selectapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sibyl.fuckwelcomeactivity.selectactivity.view.ActivityListActivity
import com.sibyl.fuckwelcomeactivity.selectapp.model.PackData
import com.sibyl.sasukehomeDominator.R
import com.sibyl.sasukehomeDominator.SasukeApplication
import com.sibyl.sasukehomeDominator.util.bitmap2Bytes
import com.sibyl.sasukehomeDominator.util.drawable2Bitmap
import com.sibyl.sasukehomeDominator.util.loadNoCache
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @author Sasuke on 2021-06-05.
 */
class AppListAdapter(val context: Activity): RecyclerView.Adapter<AppListAdapter.AppListVH>() {
    val dataList: MutableList<PackData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListVH {
        return AppListVH(LayoutInflater.from(context).inflate(R.layout.app_list_item,parent,false))
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: AppListVH, pos: Int) {
        val data = dataList[pos]
        holder.apply {
            if (data.appIcon == null){
                SasukeApplication.app.executor.submit {
                    data.appIcon = bitmap2Bytes(drawable2Bitmap(data.appInfo?.loadIcon(context.packageManager)))
                    context.runOnUiThread { Glide.with(context).loadNoCache(data.appIcon).into(appIconIv) }
                }
            }else{
                Glide.with(context).loadNoCache(data.appIcon).into(appIconIv)
            }

            appNameTv.text = data.appName
            pkgNameTv.text = data.pkgName
            //点击跳转
            rootLayout.setOnClickListener {
                //SasukeTodo 回头要记得处理这里的关闭回调情况
                context.startActivityForResult(Intent(context, ActivityListActivity::class.java).apply {
                    putExtra("packData",data)
//                    putExtra("isAddNew",true)
                },0)
            }
        }
    }

    fun setNewData(newList: List<PackData>){
        dataList.clear()
        dataList.addAll(newList)
    }

    class AppListVH(view: View): RecyclerView.ViewHolder(view){
        val appIconIv = view.findViewById<CircleImageView>(R.id.appIconIv)
        val appNameTv = view.findViewById<TextView>(R.id.appNameTv)
        val pkgNameTv = view.findViewById<TextView>(R.id.pkgNameTv)
        val rootLayout = view.findViewById<CardView>(R.id.rootLayout)
    }

}