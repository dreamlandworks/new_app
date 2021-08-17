package com.satrango.ui.user.user_dashboard.search_service_providers.models

import java.io.Serializable

data class Data(
    val about_me: String,
    val address: String,
    val category_id: String,
    val city: String,
    val country: String,
    val created_dts: String,
    val dob: String,
    val exp: String,
    val extra_charge: String,
    val fcm_token: String,
    val fname: String,
    val gender: String,
    val id: String,
    val latitude: String,
    val lname: String,
    val longitude: String,
    val min_charges: String,
    val mobile: String,
    val per_day: String,
    val per_hour: String,
    val distance_miles: String,
    val points_count: Any,
    val postcode: String,
    val profession: String,
    val profile_pic: Any,
    val qualification: String,
    val referral_id: String,
    val reg_status: String,
    val registered_on: String,
    val state: String,
    val subcategory_id: String,
    val users_id: String
): Serializable