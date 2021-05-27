package com.satrango.utils

import android.content.Context
import com.satrango.R

object UserUtils {

    var password = ""
    var firstName = ""
    var lastName = ""
    var phoneNo = ""
    var latitude = ""
    var longitute = ""
    var city = ""
    var state = ""
    var country = ""
    var address = ""
    var postalCode = ""
    var dateOfBirth = ""
    var mailId = ""
    var facebookId = ""
    var googleId = ""
    var twitterId = ""

    fun setUserLoggedInVia(context: Context, type: String, userId: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.userLoggedVia), type)
        editor.putString(context.resources.getString(R.string.userId), userId)
        editor.apply()
        editor.commit()
    }

    fun getUserLoggedInVia(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.userLoggedVia),
            ""
        )!!
    }

    fun getUserId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.userId), "")!!
    }

    fun setReferralId(context: Context, referralId: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.userReferralId), referralId)
        editor.apply()
        editor.commit()
    }

    fun getReferralId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.userReferralId), "")!!
    }

}