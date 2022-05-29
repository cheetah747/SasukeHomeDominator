package com.sibyl.sasukehomeDominator

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Sasuke on 2021-07-06.
 */
class MainPre(val activity: MainActivity,val cardViews: List<View>) {
    val cardDataList by lazy {
        listOf(
            CardData(null,R.string.screen_lock,R.drawable.lock_screen_30dp, null,R.mipmap.lock_screen_shotcut),
            CardData(null,R.string.screen_shot,R.drawable.screen_shot_30dp,null),
            CardData(null,R.string.settings,R.drawable.screen_shot_setting_30dp,R.color.big_btn_text_color),
            CardData(null,R.string.power_menu,R.drawable.power_longpress_30dp, null,R.mipmap.power_longpress_shotcut),
            CardData(null,R.string.any_tile,R.mipmap.ic_any_tile,null,R.mipmap.any_tile_shotcut),
            CardData(null,R.string.settings,R.drawable.screen_shot_setting_30dp,R.color.big_btn_text_color),
            CardData(null,R.string.fuck_brightness_tile,R.drawable.ic_fuck_brightness_30, null,R.mipmap.fuck_brightness_shotcut),
            CardData(null,R.string.notifi,R.drawable.notifi_icon_30dp, null, R.mipmap.notifi_shotcut)
         )
    }


    init {
        cardDataList.forEachIndexed { index, cardData ->
            cardData.cardView = cardViews[index]
        }
    }


    fun setupCardView(){
        cardDataList.forEach { cardData ->
            (cardData.cardView?.findViewById(R.id.cardIcon) as ImageView).setImageResource(cardData.iconId)
            (cardData.cardView?.findViewById(R.id.cardText) as TextView).run {
                setText(activity.resources.getString( cardData.titleTextId))
                if (cardData.textColor != null){//当文字有颜色，说明是设置按钮
                    setTextColor(activity.resources.getColor(cardData.textColor,null))
                    (cardData.cardView?.findViewById(R.id.cardContainer) as LinearLayout).setBackgroundColor(activity.resources.getColor(R.color.red, null ))
                }
            }
        }
    }

    data class CardData(var cardView: View?,val titleTextId: Int,val iconId: Int, val textColor: Int? = null, val shortcutIconId: Int? = null)


}