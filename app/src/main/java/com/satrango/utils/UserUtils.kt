package com.satrango.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.satrango.R
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import java.io.ByteArrayOutputStream

object UserUtils {

    var keywordId = 0
    var FORGOT_PWD = false
    var USER_ID = ""
    var scheduled_date = ""
    var time_slot_from = ""
    var time_slot_to = ""
    var started_at = ""
    var address_id = "0"
    var temp_address_id = "0"
    var job_description = ""
    var addressList = ArrayList<MonthsModel>()
    var finalAddressList = ArrayList<Addresses>()

    fun setPassword(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.password), password)
        editor.apply()
        editor.commit()
    }

    fun getPassword(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.password), "")!!
    }

    fun setFirstName(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.first_name), password)
        editor.apply()
        editor.commit()
    }

    fun getFirstName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.first_name), "")!!
    }

    fun setLastName(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.last_name), password)
        editor.apply()
        editor.commit()
    }

    fun getLastName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.last_name), "")!!
    }

    fun setPhoneNo(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.phoneNo), password)
        editor.apply()
        editor.commit()
    }

    fun getPhoneNo(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.phoneNo), "")!!
    }

    fun setGender(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.gender), password)
        editor.apply()
        editor.commit()
    }

    fun getGender(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.gender), "")!!
    }

    fun setLatitude(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.latitude), password)
        editor.apply()
        editor.commit()
    }

    fun getLatitude(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.latitude), "")!!
    }

    fun setLongitude(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.longitude), password)
        editor.apply()
        editor.commit()
    }

    fun getLongitude(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.longitude), "")!!
    }

    fun setCity(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.city_name), password)
        editor.apply()
        editor.commit()
    }

    fun getCity(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.city_name), "")!!
    }

    fun setState(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.state), password)
        editor.apply()
        editor.commit()
    }

    fun getState(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.state), "")!!
    }

    fun setCountry(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.country), password)
        editor.apply()
        editor.commit()
    }

    fun getCountry(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.country), "")!!
    }

    fun setAddress(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.address), password)
        editor.apply()
        editor.commit()
    }

    fun getAddress(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.address), "")!!
    }

    fun setPostalCode(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.postal_code), password)
        editor.apply()
        editor.commit()
    }

    fun getPostalCode(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.postal_code), "")!!
    }

    fun setDateOfBirth(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.date_of_birth), password)
        editor.apply()
        editor.commit()
    }

    fun getDateOfBirth(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.date_of_birth), "")!!
    }

    fun setMail(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.email), password)
        editor.apply()
        editor.commit()
    }

    fun getMail(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.email), "")!!
    }

    fun setFacebookId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.facebook_id), password)
        editor.apply()
        editor.commit()
    }

    fun getFacebookId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.facebook_id), "")!!
    }

    fun setGoogleId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.google_id), password)
        editor.apply()
        editor.commit()
    }

    fun getGoogleId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.google_id), "")!!
    }

    fun setTwitterId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.twitter), password)
        editor.apply()
        editor.commit()
    }

    fun getTwitterId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.twitter), "")!!
    }

    fun setSearchResultsId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.search_results_id), password)
        editor.apply()
        editor.commit()
    }

    fun getSearchResultsId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.search_results_id), "")!!
    }

    fun setTempAddressId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.temp_address_id), password)
        editor.apply()
        editor.commit()
    }

    fun getTempAddressId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.temp_address_id), "")!!
    }

    fun setUserLoggedInVia(context: Context, type: String, userId: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
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
        editor.putString(context.resources.getString(R.string.password), getPassword(context))
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