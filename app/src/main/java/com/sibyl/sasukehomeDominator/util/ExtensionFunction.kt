package com.sibyl.sasukehomeDominator.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @author Sasuke on 2018/6/15.
 */

/**
 * EditText的addTextChangedListener扩展方法，扩展函数
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit){
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}