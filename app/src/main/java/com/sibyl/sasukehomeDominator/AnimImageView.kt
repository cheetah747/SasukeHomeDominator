package com.sibyl.sasukehomeDominator

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.cardview.widget.CardView

/**
 * @author Sasuke on 2020/9/21.
 */
class AnimImageView (context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    fun setParamHeight(height: Int){
        layoutParams = layoutParams.apply {
            this.height = height
            invalidate()
        }
    }

    fun getParamWidth() = layoutParams.width
}