package com.sibyl.sasukehomeDominator.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @author Sasuke on 2021-06-06.
 */
fun EditText.onTextChanged(callback: (String) ->Unit) {
    this.addTextChangedListener(object: TextWatcher{
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback.invoke(s.toString())
        }
    })
}