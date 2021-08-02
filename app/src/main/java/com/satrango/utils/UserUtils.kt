package com.satrango.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.satrango.R
import java.io.ByteArrayOutputStream

object UserUtils {

    var password = ""
    var firstName = ""
    var lastName = ""
    var phoneNo = ""
    var gender = ""
    var latitude = "0.0"
    var longitute = "0.0"
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

    var FORGOT_PWD = false
    var USER_ID = ""

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

    fun encodeToBase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun saveUserProfilePic(context: Context, url: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.userImageUrl), url)
        editor.apply()
        editor.commit()
    }

    fun getUserProfilePic(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.userImageUrl), "")!!
    }

    fun getUserName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.userName), "")!!
    }

    fun saveUserName(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.userName), fullName)
        editor.apply()
        editor.commit()
    }

    fun saveReferralId(context: Context, referralId: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.userReferralId), referralId)
        editor.apply()
        editor.commit()
    }

    fun saveLoginCredentials(context: Context, phoneNo: String, pwd: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.phoneNo), phoneNo)
        editor.putString(context.resources.getString(R.string.password), password)
        editor.apply()
        editor.commit()
    }

    fun deleteUserCredentials(context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.phoneNo), "")
        editor.putString(context.resources.getString(R.string.password), "")
        editor.apply()
        editor.commit()
    }

    fun getLoginCredentials(context: Context): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        sharedPreferences.getString(context.resources.getString(R.string.password), "")
        map.put(context.resources.getString(R.string.phoneNo), sharedPreferences.getString(context.resources.getString(R.string.phoneNo), "")!!)
        map.put(context.resources.getString(R.string.password), sharedPreferences.getString(context.resources.getString(R.string.password), "")!!)
        return map
    }

}