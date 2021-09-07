package com.satrango.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.satrango.R
import com.satrango.remote.Notification
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMMessageReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object UserUtils {

    var title = ""
    var bids_period = 0
    var bid_range_id = 0
    var estimate_time = 0
    var bid_per = 0

    var keywordId = 0
    var estimateTypeId = 0
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


    fun setFromJobPost(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.from_job_post), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPost(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.from_job_post), false)
    }

    fun saveInstallmentDetId(context: Context, fromJobPost: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.saveInstallmentDetId), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getInstallmentDetId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.saveInstallmentDetId), "0")
    }

    fun setFromJobPostSingleMove(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.single_move), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostSingleMove(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.single_move), false)
    }

    fun setFromJobPostMultiMove(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.multi_move), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostMultiMove(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.multi_move), false)
    }

    fun setFromJobPostBlueCollar(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.blue_collar), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostBlueCollar(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.blue_collar), false)
    }

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

    fun saveSelectedSPDetails(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.selected_service_provider), spDetails)
        editor.apply()
        editor.commit()
    }

    fun getSelectedSPDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.selected_service_provider), "")!!
    }

    fun saveSelectedKeywordCategoryId(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.selected_keyword_id), spDetails)
        editor.apply()
        editor.commit()
    }

    fun getSelectedKeywordCategoryId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.selected_keyword_id), "")!!
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

    fun getBookingId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.booking_id), "")!!
    }

    fun saveBookingId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.booking_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getBookingRefId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.booking_ref_id), "")!!
    }

    fun saveBookingRefId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.booking_ref_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getInstantBooking(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.book_instantly), false)
    }

    fun saveInstantBooking(context: Context, fullName: Boolean) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.book_instantly), fullName)
        editor.apply()
        editor.commit()
    }

    fun getFromInstantBooking(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(context.resources.getString(R.string.book_instantly), false)
    }

    fun saveFromInstantBooking(context: Context, fullName: Boolean) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.is_book_instant), fullName)
        editor.apply()
        editor.commit()
    }

    fun getProviderAction(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.provider_action), "")!!
    }

    private fun saveProviderAction(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.provider_action), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.booking_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getSearchFilter(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.search_filter), "")!!
    }

    fun saveSearchFilter(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.search_filter), fullName)
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

    fun getComingHour(): Int {
        val calender = Calendar.getInstance()
        return calender.get(Calendar.HOUR_OF_DAY) + 1
    }

    fun sendFCM(
        context: Context,
        token: String,
        bookingId: String,
        from: String
    ) {
        saveProviderAction(context, "")
        val map = mutableMapOf<String, String>()
        map["Content-Type"] = "application/json"
        map["Authorization"] = "key=${context.getString(R.string.fcm_server_key)}"
        val requestBody = FCMMessageReqModel(Notification("$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}", "$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}", from), "high", token)
        CoroutineScope(Dispatchers.Main).launch {
            RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
        }
    }

    fun sendFCMtoSelectedServiceProvider(
        context: Context,
        bookingId: String,
        from: String
    ) {
        val spDetails = Gson().fromJson(getSelectedSPDetails(context), SearchServiceProviderResModel::class.java)
        for (sp in spDetails.data) {
            for (spSlot in spDetails.slots_data) {
                if (sp.users_id == spSlot.user_id) {
                    Log.e("SEND FCM TO", sp.fcm_token)
                    sendFCM(context, sp.fcm_token, bookingId, from)
                }
            }
        }
    }

    fun sendFCMtoAllServiceProviders(
        context: Context,
        bookingId: String,
        from: String
    ) {
        val spDetails = Gson().fromJson(getSelectedSPDetails(context), SearchServiceProviderResModel::class.java)
        for (sp in spDetails.data) {
            for (spSlot in spDetails.slots_data) {
                for (booking in spSlot.blocked_time_slots) {
                    if (getComingHour() == booking.time_slot_from.split(":")[0].toInt()) {
                        sendFCM(context, sp.fcm_token, bookingId, from)
                    }
                }
            }
        }
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return roundOffDecimal(dist * 1.609344) // miles to kms
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

}