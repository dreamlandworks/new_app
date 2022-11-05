package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("address")
    val address: List<Address>,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("email_id")
    val email_id: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("referral_id")
    val referral_id: String,
    @SerializedName("maps_key")
    val maps_key: String,
    @SerializedName("fcm_key")
    val fcm_key: String,
    @SerializedName("sp_activated")
    val sp_activated: String, // 1 = Not Activated, 2 = Approval Waiting, 3 = Activated, 4 = Not Approved, 5 = Banned
    @SerializedName("activation_code")
    val activation_code: String // 0 = Not Applied Yet, 1 = ID Card Uploaded, 2 = 1st Video Uploaded, 3 = 2nd Video Uploaded , 4 = 3rd Video Uploaded, 5 = Activated
): Serializable