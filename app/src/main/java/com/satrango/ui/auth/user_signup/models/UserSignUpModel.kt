package com.satrango.ui.auth.user_signup.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserSignUpModel(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("email_id")
    val email_id: String,
    @SerializedName("facebook_id")
    val facebook_id: String,
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("google_id")
    val google_id: String,
    @SerializedName("last_name")
    val last_name: String,
    @SerializedName("mobile_no")
    val mobile_no: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("postal_code")
    val postal_code: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("twitter_id")
    val twitter_id: String,
    @SerializedName("user_lat")
    val user_lat: String,
    @SerializedName("user_long")
    val user_long: String,
    @SerializedName("referral_id")
    val referral_id: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("key")
    val key: String
): Serializable