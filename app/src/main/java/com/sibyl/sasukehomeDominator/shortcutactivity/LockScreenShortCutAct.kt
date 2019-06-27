package com.sibyl.sasukehomeDominator.shortcutactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sibyl.sasukehomeDominator.services.SasukeAccessibilityService
import com.sibyl.sasukehomeDominator.util.StaticVar

/**
 * @author Sasuke on 2019-6-27 0027.
 */
class LockScreenShortCutAct: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService(Intent(this, SasukeAccessibilityService::class.java).apply {
            putExtra(StaticVar.KEY_ACCESSIBILITY_TYPE, StaticVar.STRONG_LOCKSCREEN)
        })
        finish()
    }
}