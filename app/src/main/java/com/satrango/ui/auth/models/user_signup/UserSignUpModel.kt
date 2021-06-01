package com.satrango.ui.auth.models.user_signup

data class UserSignUpModel(
    val address: String,
    val city: String,
    val country: String,
    val dob: String,
    val email_id: String,
    val facebook_id: String,
    val first_name: String,
    val google_id: String,
    val last_name: String,
    val mobile_no: String,
    val password: String,
    val postal_code: String,
    val state: String,
    val twitter_id: String,
    val user_lat: String,
    val user_long: String,
    val referral_id: String
)