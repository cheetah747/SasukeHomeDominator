package com.sibyl.sasukehomeDominator.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.sibyl.sasukehomeDominator.R
import java.io.DataOutputStream

/**
 * @author Administrator on 2021-07-13 013.
 */
class JumpActivityDominator(val context: Context) {
    companion object{
        @JvmStatic
        fun jumpByRoot(context: Context,pkgName: String,goalActivity: String){
            try {
                val exec = Runtime.getRuntime().exec("su")
                val dataOutputStream = DataOutputStream(exec.outputStream)
                val sb = StringBuilder().apply {
                    append("am start -n ")
                    append(pkgName)
                    append("/")
                    append(goalActivity)
                    append("\n")
                }

                dataOutputStream.write(sb.toString().toByteArray(charset("UTF-8")))
                dataOutputStream.write("exit\n".toByteArray(charset("UTF-8")))
                dataOutputStream.flush()
                dataOutputStream.close()
                exec.waitFor()
            } catch (e: Exception) {
                Toast.makeText(context,context.resources.getString(R.string.root_pls), Toast.LENGTH_SHORT).show()
            }
        }

        @JvmStatic
        fun jumpNoRoot(context: Context,pkgName: String,goalActivity: String){
            context.startActivity(Intent().apply {
                setClassName(pkgName, goalActivity)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

}