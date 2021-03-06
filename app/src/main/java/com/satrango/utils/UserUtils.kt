package com.satrango.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.satrango.R
import com.satrango.remote.Data
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMMessageReqModel
import com.satrango.remote.fcm.NotificationX
import com.satrango.remote.fcm.SendFCMReqModel
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.service_provider.provider_dashboard.ProviderRejectBookingScreen
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object UserUtils {

    var EDIT_MY_JOB_POST_DETAILS = ""
    var EDIT_MY_JOB_POST = false
    var POST_JOB_AGAIN = false
    var title = ""
    var bids_period = 0
    var bid_range_id = 0
    var estimate_time = 0
    var bid_per = 0

    var estimateTypeId = 0
    var FORGOT_PWD = false
    var USER_ID = ""
    var scheduled_date = ""
    var re_scheduled_date = ""
    var time_slot_from = ""
    var re_scheduled_time_slot_from = ""
    var time_slot_to = ""
    var started_at = ""
    var address_id = "0"
    var temp_address_id = "0"
    var job_description = ""
    var addressList = ArrayList<MonthsModel>()
    var finalAddressList = ArrayList<Addresses>()
    var data: com.satrango.ui.user.user_dashboard.search_service_providers.models.Data? = null
    var bookingType = ""

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

    fun saveSpId(context: Context, spId: String) {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.sp_id), spId)
        editor.apply()
        editor.commit()
    }

    fun getSpId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.resources.getString(R.string.userDetails), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.resources.getString(R.string.sp_id), "")!!
    }

    fun savePostJobId(context: Context, postJobId: Int) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(context.resources.getString(R.string.post_job_id), postJobId)
        editor.apply()
        editor.commit()
    }

    fun getPostJobId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(context.resources.getString(R.string.post_job_id), 0)
    }

    fun saveInVoiceDetails(context: Context, invoiceDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.invoice_details), invoiceDetails)
        editor.apply()
        editor.commit()
    }

    fun getInvoiceDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.invoice_details),
            ""
        )!!
    }

    fun isProvider(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.from_provider), fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isProvider(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.from_provider),
            false
        )
    }

    fun isPending(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.from_pending), fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isPending(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.from_pending),
            false
        )
    }

    fun isProgress(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.from_progress), fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isProgress(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.from_progress),
            false
        )
    }

    fun isCompleted(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.from_completed), fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isCompleted(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.from_completed),
            false
        )
    }

    fun isReschedule(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.re_schedule), fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isReschedule(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.re_schedule),
            false
        )
    }

    fun setInstantBookingSpCount(context: Context, fromJobPost: Int) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(context.resources.getString(R.string.instant_booking_sp_count), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getInstantBookingSpCount(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(
            context.resources.getString(R.string.instant_booking_sp_count),
            0
        )
    }

    fun setOffline(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.sp_status), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getSpStatus(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(context.resources.getString(R.string.sp_status), true)
    }

    fun setOnline(context: Context, spStatus: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.sp_status), spStatus)
        editor.apply()
        editor.commit()
    }

    fun saveInstallmentDetId(context: Context, fromJobPost: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.saveInstallmentDetId), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getInstallmentDetId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.saveInstallmentDetId),
            "0"
        )
    }

    fun setFromJobPostSingleMove(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.single_move), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostSingleMove(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.single_move),
            false
        )
    }

    fun setFromJobPostMultiMove(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.multi_move), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostMultiMove(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(context.resources.getString(R.string.multi_move), false)
    }

    fun setFromJobPostBlueCollar(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.blue_collar), fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostBlueCollar(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.blue_collar),
            false
        )
    }

    fun setPassword(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.password), password)
        editor.apply()
        editor.commit()
    }

    fun getPassword(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.password), "")!!
    }

    fun setFirstName(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.first_name), password)
        editor.apply()
        editor.commit()
    }

    fun getFirstName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.first_name), "")!!
    }

    fun setLastName(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.last_name), password)
        editor.apply()
        editor.commit()
    }

    fun getLastName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.last_name), "")!!
    }

    fun setPhoneNo(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.phoneNo), password)
        editor.apply()
        editor.commit()
    }

    fun getPhoneNo(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.phoneNo), "")!!
    }

    fun setGender(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.gender), password)
        editor.apply()
        editor.commit()
    }

    fun getGender(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.gender), "")!!
    }

    fun setLatitude(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.latitude), password)
        editor.apply()
        editor.commit()
    }

    fun getLatitude(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.latitude), "")!!
    }

    fun setLongitude(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.longitude), password)
        editor.apply()
        editor.commit()
    }

    fun getLongitude(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.longitude), "")!!
    }

    fun setCity(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.city_name), password)
        editor.apply()
        editor.commit()
    }

    fun getCity(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.city_name), "")!!
    }

    fun setState(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.state), password)
        editor.apply()
        editor.commit()
    }

    fun getState(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.state), "")!!
    }

    fun setCountry(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.country), password)
        editor.apply()
        editor.commit()
    }

    fun getCountry(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.country), "")!!
    }

    fun setAddress(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.address), password)
        editor.apply()
        editor.commit()
    }

    fun getAddress(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.address), "")!!
    }

    fun saveFCMToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.fcm_token), token)
        editor.apply()
        editor.commit()
    }

    fun getFCMToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.fcm_token), "")!!
    }

    fun setPostalCode(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.postal_code), password)
        editor.apply()
        editor.commit()
    }

    fun getPostalCode(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.postal_code), "")!!
    }

    fun setDateOfBirth(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.date_of_birth), password)
        editor.apply()
        editor.commit()
    }

    fun getDateOfBirth(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.date_of_birth),
            ""
        )!!
    }

    @SuppressLint("ApplySharedPref")
    fun saveBookingPauseResumeStatus(context: Context, status: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.pause_resume_booking), status)
        editor.apply()
        editor.commit()
    }

    fun getBookingPauseResumeStatus(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.pause_resume_booking),
            ""
        )!!
    }

    fun setMail(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.email), password)
        editor.apply()
        editor.commit()
    }

    fun getMail(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.email), "")!!
    }

    fun setFacebookId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.facebook_id), password)
        editor.apply()
        editor.commit()
    }

    fun getFacebookId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.facebook_id), "")!!
    }

    fun setGoogleId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.google_id), password)
        editor.apply()
        editor.commit()
    }

    fun getGoogleId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.google_id), "")!!
    }

    fun setTwitterId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.twitter), password)
        editor.apply()
        editor.commit()
    }

    fun getTwitterId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.twitter), "")!!
    }

    fun setSearchResultsId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.search_results_id), password)
        editor.apply()
        editor.commit()
    }

    fun getSearchResultsId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.search_results_id),
            ""
        )!!
    }

    fun setTempAddressId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.temp_address_id), password)
        editor.apply()
        editor.commit()
    }

    fun getTempAddressId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.temp_address_id),
            ""
        )!!
    }

    fun selectedChat(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.selected_chat), userId)
        editor.apply()
        editor.commit()
    }

    fun getSelectedChat(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.selected_chat),
            ""
        )!!
    }

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

//    fun setReferralId(context: Context, referralId: String) {
//        val sharedPreferences = context.getSharedPreferences(
//            context.resources.getString(R.string.userDetails),
//            Context.MODE_PRIVATE
//        )
//        val editor = sharedPreferences.edit()
//        editor.putString(context.resources.getString(R.string.userReferralId), referralId)
//        editor.apply()
//        editor.commit()
//    }

    fun saveSelectedSPDetails(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(
            context.resources.getString(R.string.selected_service_provider_single),
            spDetails
        )
        editor.apply()
        editor.commit()
    }

    fun getSelectedSPDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.selected_service_provider_single),
            ""
        )!!
    }

    fun saveSelectedAllSPDetails(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.selected_service_provider), spDetails)
        editor.apply()
        editor.commit()
    }

    fun getSelectedAllSPDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.selected_service_provider),
            ""
        )!!
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
        return sharedPreferences.getString(
            context.resources.getString(R.string.selected_keyword_id),
            ""
        )!!
    }

    fun getReferralId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.userReferralId),
            ""
        )!!
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
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.userImageUrl), "")!!
    }

    fun getUserName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.userName), "")!!
    }

    fun saveUserName(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.userName), fullName)
        editor.apply()
        editor.commit()
    }

    private fun getFCMServerKey(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.fcm_server_key),
            ""
        )!!
    }

    fun saveFCMServerKey(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.fcm_server_key), fullName)
        editor.apply()
        editor.commit()
    }

    fun getGoogleMapsKey(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.google_maps_key),
            ""
        )!!
    }

    fun saveGoogleMapsKey(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.google_maps_key), fullName)
        editor.apply()
        editor.commit()
    }

    fun getBookingId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.booking_id), "")!!
    }

    fun saveBookingId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.booking_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getBookingRefId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.booking_ref_id),
            ""
        )!!
    }

    fun saveBookingRefId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.booking_ref_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getOrderId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.order_id), "")!!
    }

    fun saveOrderId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.order_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getTxnToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(context.resources.getString(R.string.txn_id), "")!!
    }

    fun saveTxnToken(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.txn_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getInstantBooking(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.book_instantly),
            false
        )
    }

    fun saveInstantBooking(context: Context, fullName: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.book_instantly), fullName)
        editor.apply()
        editor.commit()
    }

    fun getFromInstantBooking(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.is_book_instant),
            false
        )
    }

    fun saveFromInstantBooking(context: Context, fullName: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.is_book_instant), fullName)
        editor.apply()
        editor.commit()
    }

    fun getProviderAction(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.provider_action),
            ""
        )!!
    }

    private fun saveProviderAction(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.provider_action),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(context.resources.getString(R.string.booking_id), fullName)
        editor.apply()
        editor.commit()
    }

    fun getFromFCMService(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.provider_action),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            context.resources.getString(R.string.from_fcm_service),
            false
        )
    }

    fun saveFromFCMService(context: Context, fullName: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.provider_action),
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.resources.getString(R.string.from_fcm_service), fullName)
        editor.apply()
        editor.commit()
    }

    fun getSearchFilter(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            context.resources.getString(R.string.search_filter),
            ""
        )!!
    }

    fun saveSearchFilter(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.resources.getString(R.string.userDetails),
            Context.MODE_PRIVATE
        )
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
        map.put(
            context.resources.getString(R.string.phoneNo),
            sharedPreferences.getString(context.resources.getString(R.string.phoneNo), "")!!
        )
        map.put(
            context.resources.getString(R.string.password),
            sharedPreferences.getString(context.resources.getString(R.string.password), "")!!
        )
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
        from: String,
        type: String
    ): String {
        saveProviderAction(context, "")
        val map = mutableMapOf<String, String>()
        map["Content-Type"] = "application/json"
        map["Authorization"] = "key=${getFCMServerKey(context)}"
//        val request = SendFCMReqModel(NotificationX("${ProviderRejectBookingScreen.bookingId}|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|reject|${ProviderRejectBookingScreen.bookingType}|$finalReason", "${ProviderRejectBookingScreen.bookingId}|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|reject|${ProviderRejectBookingScreen.bookingType}|$finalReason", "reject"), "high", ProviderRejectBookingScreen.response!!.booking_details.fcm_token)
//        val requestBody = FCMMessageReqModel(Data("$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type", "$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type", from), "high", token)

//        Toast.makeText(context, "Send FCM $token", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = SendFCMReqModel(NotificationX("$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type", "$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type",from), "high", token)
                val fcmResponse = RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
                Toast.makeText(context, Gson().toJson(fcmResponse), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
            }

//            val response = RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
//            Toast.makeText(context, Gson().toJson(response.string()), Toast.LENGTH_SHORT).show()
//            Log.e("FCM RESPONSE:", token)
        }
        return token
    }

    fun sendOTPFCM(
        context: Context,
        token: String,
        bookingId: String,
        otp: String
    ) {
        val map = mutableMapOf<String, String>()
        map["Content-Type"] = "application/json"
        map["Authorization"] = "key=${getFCMServerKey(context)}"
        val requestBody = FCMMessageReqModel(Data("$bookingId|$otp|${getUserId(context)}", "$bookingId|$otp|${getUserId(context)}", "otp"), "high", token)
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(NotificationX("$bookingId|$otp|${getUserId(context)}", "$bookingId|$otp|${getUserId(context)}","otp"), "high", token)
            val fcmResponse = RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
//            val response = RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
//            Toast.makeText(context, Gson().toJson(response.string()), Toast.LENGTH_SHORT).show()
//            Log.e("FCM RESPONSE:", token)
        }
    }

    fun sendOTPResponseFCM(
        context: Context,
        token: String,
        bookingDetails: String
    ) {
        val map = mutableMapOf<String, String>()
        map["Content-Type"] = "application/json"
        map["Authorization"] = "key=${getFCMServerKey(context)}"
//        val requestBody = FCMMessageReqModel(Data(bookingDetails, bookingDetails, "otpResponse"), "high", token)
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(NotificationX(bookingDetails, bookingDetails,"otpResponse"), "high", token)
            val fcmResponse = RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
//            val response = RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
//            Toast.makeText(context, Gson().toJson(response.string()), Toast.LENGTH_SHORT).show()
//            Log.e("FCM RESPONSE:", token)
        }
    }

    fun sendExtraDemandFCM(
        context: Context,
        token: String,
        bookingId: String,
        categoryId: String,
        userId: String,
    ) {
        val map = mutableMapOf<String, String>()
        map["Content-Type"] = "application/json"
        map["Authorization"] = "key=${getFCMServerKey(context)}"
        val requestBody = FCMMessageReqModel(Data("$bookingId|$categoryId|$userId", "$bookingId|$categoryId|$userId", "extraDemand"), "high", token)
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(NotificationX("$bookingId|$categoryId|$userId", "$bookingId|$categoryId|$userId","extraDemand"), "high", token)
            val fcmResponse = RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
//            val response = RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
//            Toast.makeText(context, Gson().toJson(response.string()), Toast.LENGTH_SHORT).show()
//            Log.e("FCM RESPONSE:", token)
        }
    }

//    fun sendRescheduleFCM(
//        context: Context,
//        token: String,
//        bookingId: String,
//        categoryId: String,
//        userId: String,
//    ) {
//        val map = mutableMapOf<String, String>()
//        map["Content-Type"] = "application/json"
//        map["Authorization"] = "key=${getFCMServerKey(context)}"
//        val requestBody = FCMMessageReqModel(Data("$bookingId|$categoryId|$userId", "$bookingId|$categoryId|$userId", "reschedule"), "high", token)
//        CoroutineScope(Dispatchers.Main).launch {
//            val response = RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
//            Log.e("FCM RESPONSE:", token)
//        }
//    }

    private fun sendCancelFCM(
        context: Context,
        token: String,
        bookingId: String,
        from: String,
        type: String
    ) {
        saveProviderAction(context, "")
        val map = mutableMapOf<String, String>()
        map["Content-Type"] = "application/json"
        map["Authorization"] = "key=${getFCMServerKey(context)}"
//        val requestBody = FCMMessageReqModel(Data("${bookingId}|$type", "accepted|$type", from), "high", token)
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(NotificationX("${bookingId}|$type", "accepted|$type",from), "high", token)
            val fcmResponse = RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
//            val response = RetrofitBuilder.getFCMRetrofitInstance().sendFCM(map, requestBody)
//            Toast.makeText(context, Gson().toJson(response.string()), Toast.LENGTH_SHORT).show()
//            Log.e("FCM RESPONSE CANCEL:", Gson().toJson(response))
        }

    }

    fun sendFCMtoSelectedServiceProvider(
        context: Context,
        bookingId: String,
        from: String
    ): String {
        val spDetails = Gson().fromJson(getSelectedSPDetails(context), com.satrango.ui.user.user_dashboard.search_service_providers.models.Data::class.java)
//        Log.e("SELECTED SP:", Gson().toJson(spDetails))
//        for (sp in spDetails.data) {
//            for (spSlot in spDetails.slots_data) {
//                if (sp.users_id == spSlot.user_id) {
//                    Log.e("SEND FCM TO", sp.fcm_token)
//                    sendFCM(context, sp.fcm_token, bookingId, from, bookingType)
//                }
//            }
//        }
        return if (spDetails.fcm_token.isEmpty()) {
            "Service Provider not able to receive notifications"
        } else {
            sendFCM(context, spDetails.fcm_token, bookingId, from, "accepted|$bookingType")
            ""
        }
    }

    fun sendFCMtoAllServiceProviders(
        context: Context,
        bookingId: String,
        from: String,
        type: String
    ): String {
        val tokenEmpty = "Service Provider not able to receive notifications"
        val spDetails = Gson().fromJson(
            getSelectedAllSPDetails(context),
            SearchServiceProviderResModel::class.java
        )
        if (getSelectedSPDetails(context).isNotEmpty()) {
            val data = Gson().fromJson(
                getSelectedSPDetails(context),
                com.satrango.ui.user.user_dashboard.search_service_providers.models.Data::class.java)
//            Log.e("SELECTED SP DETAILS:", Gson().toJson(data))
            if (data.fcm_token.isNotEmpty()) {
                sendFCM(context, data.fcm_token, bookingId, from, type)
            } else {
                return tokenEmpty
            }
        } else {
//            Log.e("SELECTED SP DETAILS:", Gson().toJson(spDetails))
            if (spDetails != null) {
                for (sp in spDetails.data) {
                    for (spSlot in spDetails.slots_data) {
                        if (spSlot.blocked_time_slots.isNotEmpty()) {
                            var count = 0
                            for (booking in spSlot.blocked_time_slots) {
                                if (getComingHour() == booking.time_slot_from.split(":")[0].toInt()) {
                                    count += 1
                                }
                            }
                            if (count == 0) {
//                            Log.e("FCM:", sp.fcm_token)
                                if (from == "accepted") {
                                    for (index in 1 until 5) {
                                        sendCancelFCM(context, sp.fcm_token, bookingId, from, type)
                                    }
                                } else {
                                    if (sp.fcm_token.isNotEmpty()) {
                                        sendFCM(context, sp.fcm_token, bookingId, from, type)
                                    } else {
                                        return tokenEmpty
                                    }
                                    Log.d("FCM SENT MULTIPLE:", sp.fcm_token)
                                }
                            }
                        } else {
//                        Log.e("FCM:", sp.fcm_token)
                            if (from == "accepted") {
                                for (index in 1 until 5) {
                                    sendCancelFCM(context, sp.fcm_token, bookingId, from, type)
                                }
                            } else {
                                if (sp.fcm_token.isNotEmpty()) {
                                    sendFCM(context, sp.fcm_token, bookingId, from, type)
                                } else {
                                    return tokenEmpty
                                }
                                Log.d("FCM SENT MULTIPLE:", sp.fcm_token)
                            }
                        }
                    }
                }
            }
        }
        return ""
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun isNowTimeBetween(startTime: String?, endTime: String?, compareTime: String): Boolean {
        val start: LocalTime =
            LocalTime.parse(startTime!!.split(":")[0] + ":" + startTime.split(":")[1]) //"22:00"
        val end: LocalTime =
            LocalTime.parse(endTime!!.split(":")[0] + ":" + endTime.split(":")[1]) //"10:00"
        val now: LocalTime =
            LocalTime.parse(compareTime.split(":")[0] + ":" + compareTime.split(":")[1])
//        Log.e("TIME:", "$start|$end|$now")
        if (start.isBefore(end)) return now.isAfter(start) && now.isBefore(end)
        return if (now.isBefore(start)) now.isBefore(start) && now.isBefore(end) else now.isAfter(
            start
        ) && now.isAfter(end)
    }

    fun checktimings(time: String, endtime: String): Boolean {
        val pattern = "HH:mm"
        val sdf = SimpleDateFormat(pattern)
        try {
            val date1: Date = sdf.parse(time)
            val date2: Date = sdf.parse(endtime)
            return date1.after(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun updateNewFCMToken(context: Context): Boolean {
        if (PermissionUtils.isNetworkConnected(context)) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
//                    Toast.makeText(context, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show()
                    Log.w("FCM ERROR:", "Fetching FCM registration token failed", task.exception)
//                    Toast.makeText(context, task.exception!!.message, Toast.LENGTH_SHORT).show()
//                    updateNewFCMToken(context)
                    return@OnCompleteListener
                }
                val token = task.result
                saveFCMToken(context, token)
                CoroutineScope(Dispatchers.Main).launch {
                    val response = RetrofitBuilder.getUserRetrofitInstance().updateFCMToken(
                        FCMReqModel(
                            token,
                            RetrofitBuilder.USER_KEY,
                            getUserId(context)
                        )
                    )
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") != 200) {
//                        Toast.makeText(context, "Token update Failed!!!", Toast.LENGTH_SHORT).show()
                        return@launch
                    } else {
//                        Log.e("FCM TOKEN UPDATED:", token)
//                        Toast.makeText(context, "Token updated!!!", Toast.LENGTH_SHORT).show()
                    }
                }
//                Log.e("FCM TOKEN", token)
            })
            return true
        }
        return true
    }

    fun makePhoneCall(context: Context, phoneNo: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+91$phoneNo", null)))
    }

    fun makeMessage(context: Context, phoneNo: String) {
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("sms:+91$phoneNo")
        context.startActivity(sendIntent)
    }

    fun getBase64FromFile(context: Context, filepath: Uri?): String? {
        var inputStream: InputStream? = null
        var byteArrayOutputStream = ByteArrayOutputStream()
        try {
            inputStream = context.contentResolver.openInputStream(filepath!!)
            val buffer = ByteArray(1024)
            byteArrayOutputStream = ByteArrayOutputStream()
            var bytesRead: Int
            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        val pdfByteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(pdfByteArray, Base64.DEFAULT)
    }

    fun getFileExtension(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result.substring(result.lastIndexOf(".") + 1)
    }

    fun saveInstantBookingId(context: Context, bookingId: String) {
        val sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("instantBookingId", bookingId)
        editor.apply()
        editor.commit()
    }

    fun getInstantBookingId(context: Context) : String {
        return context.getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("instantBookingId", "")!!
    }

}