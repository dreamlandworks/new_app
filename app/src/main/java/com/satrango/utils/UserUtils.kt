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
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.NotificationX
import com.satrango.remote.fcm.models.*
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.Constants.datetime_format
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
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.from_job_post, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPost(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.from_job_post,
            false
        )
    }

    fun isForgetPassword(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_forget_password, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun isForgetPassword(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_forget_password, false)
    }

    fun saveSpId(context: Context, spId: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.sp_id, spId)
        editor.apply()
        editor.commit()
    }

    fun getSpId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.sp_id, "")!!
    }

    fun savePostJobId(context: Context, postJobId: Int) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(StorageConstants.post_job_id, postJobId)
        editor.apply()
        editor.commit()
    }

    fun getPostJobId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(StorageConstants.post_job_id, 0)
    }

    fun saveInVoiceDetails(context: Context, invoiceDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.invoice_details, invoiceDetails)
        editor.apply()
        editor.commit()
    }

    fun getInvoiceDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.invoice_details,
            ""
        )!!
    }

    fun isProvider(context: Context, isProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.from_provider, isProvider)
        editor.apply()
        editor.commit()
    }

    fun isProvider(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.from_provider,
            false
        )
    }

    fun isPending(context: Context, isPending: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.from_pending, isPending)
        editor.apply()
        editor.commit()
    }

    fun isPending(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.from_pending,
            false
        )
    }

    fun isProgress(context: Context, inProgress: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.from_progress, inProgress)
        editor.apply()
        editor.commit()
    }

    fun isProgress(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.from_progress,
            false
        )
    }

    fun isCompleted(context: Context, isCompleted: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.from_completed, isCompleted)
        editor.apply()
        editor.commit()
    }

    fun isCompleted(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.from_completed,
            false
        )
    }

    fun isReschedule(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.re_schedule, fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isReschedule(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.re_schedule,
            false
        )
    }

    fun isFromUserPlans(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_from_user_plans, fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isFromUserPlans(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_from_user_plans, false)
    }

    fun isFromProviderPlans(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_from_provider_plans, fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isFromProviderPlans(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_from_provider_plans, false)
    }

    fun isFromUserSetGoals(context: Context, fromProvider: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_from_user_set_goals, fromProvider)
        editor.apply()
        editor.commit()
    }

    fun isFromUserSetGoals(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_from_user_set_goals, false)
    }

    fun isFromUserBookingAddress(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_from_user_booking_address, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun isFromUserBookingAddress(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_from_user_booking_address, false)
    }

    fun isFromCompleteBooking(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_from_complete_booking, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun isFromCompleteBooking(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_from_complete_booking, false)
    }

    fun isFromProviderBookingResponse(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.is_from_provider_booking_response, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun isFromProviderBookingResponse(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.is_from_provider_booking_response, false)
    }

    fun setPayableAmount(context: Context, amount: Int) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(StorageConstants.set_payable_amount, amount)
        editor.apply()
        editor.commit()
    }

    fun getPayableAmount(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(StorageConstants.set_payable_amount, 0)
    }

    fun setFinalWalletBalance(context: Context, amount: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.set_final_wallet_balance, amount)
        editor.apply()
        editor.commit()
    }

    fun getFinalWalletBalance(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.set_final_wallet_balance, "0")!!
    }

    fun getSpStatus(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.sp_status, true)
    }

    fun setOnline(context: Context, spStatus: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.sp_status, spStatus)
        editor.apply()
        editor.commit()
    }

    fun saveInstallmentDetId(context: Context, fromJobPost: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.save_installment_det_id, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getInstallmentDetId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.save_installment_det_id,
            "0"
        )
    }

    fun setFromJobPostSingleMove(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.single_move, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostSingleMove(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.single_move,
            false
        )
    }

    fun setFromJobPostMultiMove(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.multi_move, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostMultiMove(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(StorageConstants.multi_move, false)
    }

    fun setFromJobPostBlueCollar(context: Context, fromJobPost: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.blue_collar, fromJobPost)
        editor.apply()
        editor.commit()
    }

    fun getFromJobPostBlueCollar(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.blue_collar,
            false
        )
    }

    fun setPassword(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.password, password)
        editor.apply()
        editor.commit()
    }

    fun getPassword(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.password, "")!!
    }

    fun setFirstName(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.first_name, password)
        editor.apply()
        editor.commit()
    }

    fun getFirstName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.first_name, "")!!
    }

    fun setLastName(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.last_name, password)
        editor.apply()
        editor.commit()
    }

    fun getLastName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.last_name, "")!!
    }

    fun setPhoneNo(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.phone_no, password)
        editor.apply()
        editor.commit()
    }

    fun getPhoneNo(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.phone_no, "")!!
    }

    fun setGender(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.gender, password)
        editor.apply()
        editor.commit()
    }

    fun getGender(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.gender, "")!!
    }

    fun setLatitude(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.latitude, password)
        editor.apply()
        editor.commit()
    }

    fun getLatitude(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.latitude, "")!!
    }

    fun setLongitude(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.longitude, password)
        editor.apply()
        editor.commit()
    }

    fun getLongitude(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.longitude, "")!!
    }

    fun setAppLanguage(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.app_language, password)
        editor.apply()
        editor.commit()
    }

    fun getAppLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        return sharedPreferences.getString(StorageConstants.app_language, "en")!!
    }

    fun setCity(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.city_name, password)
        editor.apply()
        editor.commit()
    }

    fun getCity(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.city_name, "")!!
    }

    fun setState(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.state, password)
        editor.apply()
        editor.commit()
    }

    fun getState(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.state, "")!!
    }

    fun setCountry(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.country, password)
        editor.apply()
        editor.commit()
    }

    fun getCountry(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.country, "")!!
    }

    fun setAddress(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.address, password)
        editor.apply()
        editor.commit()
    }

    fun getAddress(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.address, "")!!
    }

    fun saveFCMToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.fcm_token, token)
        editor.apply()
        editor.commit()
    }

    fun getFCMToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.fcm_token, "")!!
    }

    fun setPostalCode(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.postal_code, password)
        editor.apply()
        editor.commit()
    }

    fun getPostalCode(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.postal_code, "")!!
    }

    fun setDateOfBirth(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.date_of_birth, password)
        editor.apply()
        editor.commit()
    }

    fun getDateOfBirth(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.date_of_birth,
            ""
        )!!
    }

    @SuppressLint("ApplySharedPref")
    fun saveBookingPauseResumeStatus(context: Context, status: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.pause_resume_booking, status)
        editor.apply()
        editor.commit()
    }

    fun getBookingPauseResumeStatus(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.pause_resume_booking,
            ""
        )!!
    }

    fun setMail(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.email, password)
        editor.apply()
        editor.commit()
    }

    fun getMail(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.email, "")!!
    }

    fun setFacebookId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.facebook_id, password)
        editor.apply()
        editor.commit()
    }

    fun getFacebookId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.facebook_id, "")!!
    }

    fun setGoogleId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.google_id, password)
        editor.apply()
        editor.commit()
    }

    fun getGoogleId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.google_id, "")!!
    }

    fun setTwitterId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.twitter, password)
        editor.apply()
        editor.commit()
    }

    fun getTwitterId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.twitter, "")!!
    }

    fun setSearchResultsId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.search_results_id, password)
        editor.apply()
        editor.commit()
    }

    fun getSearchResultsId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.search_results_id,
            ""
        )!!
    }

    fun setTempAddressId(context: Context, password: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.temp_address_id, password)
        editor.apply()
        editor.commit()
    }

    fun getTempAddressId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.temp_address_id,
            ""
        )!!
    }

    fun selectedChat(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.selected_chat, userId)
        editor.apply()
        editor.commit()
    }

    fun getSelectedChat(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.selected_chat,
            ""
        )!!
    }

    fun setUserLoggedInVia(context: Context, type: String, userId: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.user_login_via, type)
        editor.putString(StorageConstants.user_id, userId)
        editor.apply()
        editor.commit()
    }

    fun getUserLoggedInVia(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.user_login_via,
            ""
        )!!
    }

    fun getUserId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.user_id, "0")!!
    }

//    fun setReferralId(context: Context, referralId: String) {
//        val sharedPreferences = context.getSharedPreferences(
//            StorageConstants.user_details,
//            Context.MODE_PRIVATE
//        )
//        val editor = sharedPreferences.edit()
//        editor.putString(StorageConstants.userReferralId, referralId)
//        editor.apply()
//        editor.commit()
//    }

    fun saveSelectedSPDetails(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(
            StorageConstants.selected_service_provider_single,
            spDetails
        )
        editor.apply()
        editor.commit()
    }

    fun getSelectedSPDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.selected_service_provider_single,
            ""
        )!!
    }

    fun saveReferralLink(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.save_referral_link, spDetails)
        editor.apply()
        editor.commit()
    }

    fun getReferralLink(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.save_referral_link, "")!!
    }

    fun saveSelectedAllSPDetails(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.selected_service_provider, spDetails)
        editor.apply()
        editor.commit()
    }

    fun getSelectedAllSPDetails(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.selected_service_provider,
            ""
        )!!
    }

    @SuppressLint("ApplySharedPref")
    fun saveSelectedKeywordCategoryId(context: Context, spDetails: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.selected_keyword_id, spDetails)
        editor.apply()
        editor.commit()
    }

    fun getSelectedKeywordCategoryId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.selected_keyword_id,
            ""
        )!!
    }

    fun getReferralId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.user_referral_id,
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
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.user_image_url, url)
        editor.apply()
        editor.commit()
    }

    fun getUserProfilePic(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.user_image_url, "")!!
    }

    fun getUserName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.user_name, "")!!
    }

    fun saveUserName(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.user_name, fullName)
        editor.apply()
        editor.commit()
    }

    private fun getFCMServerKey(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.fcm_server_key,
            ""
        )!!
    }

    fun saveFCMServerKey(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.fcm_server_key, fullName)
        editor.apply()
        editor.commit()
    }

    fun getGoogleMapsKey(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.google_maps_key,
            ""
        )!!
    }

    fun saveGoogleMapsKey(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.google_maps_key, fullName)
        editor.apply()
        editor.commit()
    }

    fun getBookingId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.booking_id, "")!!
    }

    fun saveBookingId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.booking_id, fullName)
        editor.apply()
        editor.commit()
    }

    fun getBookingRefId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.booking_ref_id,
            ""
        )!!
    }

    fun saveBookingRefId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.booking_ref_id, fullName)
        editor.apply()
        editor.commit()
    }

    fun getOrderId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.order_id, "")!!
    }

    fun saveOrderId(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.order_id, fullName)
        editor.apply()
        editor.commit()
    }

    fun getTxnToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(StorageConstants.txn_id, "")!!
    }

    fun saveTxnToken(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.txn_id, fullName)
        editor.apply()
        editor.commit()
    }

//    fun getInstantBooking(context: Context): Boolean {
//        val sharedPreferences = context.getSharedPreferences(
//            StorageConstants.user_details,
//            Context.MODE_PRIVATE
//        )
//        return sharedPreferences.getBoolean(
//            StorageConstants.book_instantly,
//            false
//        )
//    }
//
//    fun saveInstantBooking(context: Context, fullName: Boolean) {
//        val sharedPreferences = context.getSharedPreferences(
//            StorageConstants.user_details,
//            Context.MODE_PRIVATE
//        )
//        val editor = sharedPreferences.edit()
//        editor.putBoolean(StorageConstants.book_instantly, fullName)
//        editor.apply()
//        editor.commit()
//    }

    fun getProviderAction(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.provider_action,
            ""
        )!!
    }

    private fun saveProviderAction(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.booking_id, fullName)
        editor.apply()
        editor.commit()
    }

    fun getFromFCMService(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(
            StorageConstants.from_fcm_service,
            false
        )
    }

    fun saveFromFCMService(context: Context, fullName: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean(StorageConstants.from_fcm_service, fullName)
        editor.apply()
        editor.commit()
    }

    fun getBookingType(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.booking_type,
            ""
        )
    }

    fun saveBookingType(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.booking_type, fullName)
        editor.apply()
        editor.commit()
    }

    fun isExtraDemandRaised(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.is_extra_demand_raised,
            "0"
        )!!
    }

    fun isExtraDemandRaised(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.provider_action,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.is_extra_demand_raised, fullName)
        editor.apply()
        editor.commit()
    }

    fun getSearchFilter(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            StorageConstants.search_filter,
            ""
        )!!
    }

    fun saveSearchFilter(context: Context, fullName: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.search_filter, fullName)
        editor.apply()
        editor.commit()
    }

    fun saveReferralId(context: Context, referralId: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.user_referral_id, referralId)
        editor.apply()
        editor.commit()
    }

    fun saveLoginCredentials(context: Context, phoneNo: String, pwd: String) {
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.phone_no, phoneNo)
        editor.putString(StorageConstants.password, getPassword(context))
        editor.apply()
        editor.commit()
    }

    fun deleteUserCredentials(context: Context) {
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.phone_no, "")
        editor.putString(StorageConstants.password, "")
        editor.apply()
        editor.commit()
    }

    fun getLoginCredentials(context: Context): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        val sharedPreferences = context.getSharedPreferences(
            StorageConstants.user_details,
            Context.MODE_PRIVATE
        )
        sharedPreferences.getString(StorageConstants.password, "")
        map[StorageConstants.phone_no] = sharedPreferences.getString(StorageConstants.phone_no, "")!!
        map[StorageConstants.password] = sharedPreferences.getString(StorageConstants.password, "")!!
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = SendFCMReqModel(
                    NotificationX(
                        "$bookingId|${getSelectedKeywordCategoryId(context)}|${
                            getUserId(context)
                        }|$type",
                        "$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type",
                        from)
                    , "high", token
                )
//                Toast.makeText(context, Gson().toJson(requestBody, Toast.LENGTH_SHORT).show()
                val fcmResponse = RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
//                Toast.makeText(context, Gson().toJson(fcmResponse, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
            }
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
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(
                NotificationX(
                    "$bookingId|$otp|${getUserId(context)}",
                    "$bookingId|$otp|${getUserId(context)}",
                    "otp")
                , "high", token
            )
            RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
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
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(
                NotificationX(bookingDetails, bookingDetails, "otpResponse"),
                "high",
                token
            )
            RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
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
        CoroutineScope(Dispatchers.Main).launch {
            val requestBody = SendFCMReqModel(
                NotificationX(
                    "$bookingId|$categoryId|$userId",
                    "$bookingId|$categoryId|$userId",
                    "extraDemand")
                , "high", token
            )
            RetrofitBuilder.getUserRetrofitInstance().sendFcm(requestBody)
        }
    }

    fun sendFCMtoSelectedServiceProvider(
        context: Context,
        bookingId: String,
        from: String
    ): String {
        val spDetails = Gson().fromJson(
            getSelectedSPDetails(context),
            com.satrango.ui.user.user_dashboard.search_service_providers.models.Data::class.java)
        return if (spDetails.fcm_token.isEmpty()) {
            "Service Provider not able to receive notifications"
        } else {
            sendFCM(context, spDetails.fcm_token, bookingId, from, getBookingType(context)!!)
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
        if (getSelectedSPDetails(context).isNotEmpty()) {
            val data = Gson().fromJson(
                getSelectedSPDetails(context),
                com.satrango.ui.user.user_dashboard.search_service_providers.models.Data::class.java
            )
            if (data.fcm_token.isNotEmpty()) {
                sendFCM(context, data.fcm_token, bookingId, from, type)
            } else {
                return tokenEmpty
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val fcms = ArrayList<To>()
                    val sps = Gson().fromJson(
                        getSelectedAllSPDetails(context),
                        SearchServiceProviderResModel::class.java
                    )
                    for (sp in sps.data) {
                        fcms.add(To(sp.fcm_token))
                    }
                    val notification = Notification(
                        "$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type",
                        "$bookingId|${getSelectedKeywordCategoryId(context)}|${getUserId(context)}|$type",
                        from
                    )
                    val fcmRequestBody = SendFcmToAllReqModel(notification, "high", fcms)
                    val response =
                        RetrofitBuilder.getUserRetrofitInstance().sendFcmToAll(fcmRequestBody)
//                    Toast.makeText(context, response.status.toString(, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
        val df = DecimalFormat("#.###")
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

    @SuppressLint("SimpleDateFormat")
    fun checktimings(time: String, endtime: String): Boolean {
        val pattern = "HH:mm"
        val sdf = SimpleDateFormat(pattern)
        try {
            val date1: Date = sdf.parse(time)!!
            val date2: Date = sdf.parse(endtime)!!
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
                    Log.w("FCM ERROR:", "Fetching FCM registration token failed", task.exception)
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
                        return@launch
                    }
                }
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

    @SuppressLint("Range")
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
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.instant_booking_id, bookingId)
        editor.apply()
        editor.commit()
    }

    fun getInstantBookingId(context: Context): String {
        return context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
            .getString(StorageConstants.instant_booking_id, "")!!
    }

    fun saveProfessionIdForBookInstant(
        context: Context,
        professionId: String
    ) {
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.instant_profession_id, professionId)
        editor.apply()
        editor.commit()
    }

    fun getProfessionIdForBookInstant(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        return sharedPreferences.getString(StorageConstants.instant_profession_id, "")!!
    }

    fun saveInstantBookingCategoryId(
        context: Context,
        categoryId: String
    ) {
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(StorageConstants.instant_category_id, categoryId)
        editor.apply()
        editor.commit()
    }

    fun getInstantBookingCategoryId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(StorageConstants.user_details, Context.MODE_PRIVATE)
        return sharedPreferences.getString(StorageConstants.instant_category_id, "")!!
    }

    fun currentDateTime(): String {
        return SimpleDateFormat(datetime_format).format(Date())
    }

}