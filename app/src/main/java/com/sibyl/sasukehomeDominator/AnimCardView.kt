package com.sibyl.sasukehomeDominator

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

/**
 * @author HUANGSHI-PC on 2020-07-28 0028.
 */
class AnimCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    fun setParamWidth(width: Int){
        layoutParams = layoutParams.apply {
            this.width = width
            invalidate()
        }
    }

    fun getParamWidth() = layoutParams.width
}