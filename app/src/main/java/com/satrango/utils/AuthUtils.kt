package com.satrango.utils

import android.content.Context
import com.satrango.R

object AuthUtils {

    fun setFirstTimeLaunch(context: Context) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.firstLaunch), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.firstLaunch), false)
        editor.apply()
        editor.commit()
    }

    fun getFirstTimeLaunch(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.firstLaunch), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.firstLaunch), true)
    }

}