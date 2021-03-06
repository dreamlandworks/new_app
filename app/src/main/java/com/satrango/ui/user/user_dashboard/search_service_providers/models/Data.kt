package com.satrango.ui.user.user_dashboard.search_service_providers.models

import java.io.Serializable

data class Data(
    val CGST_amount: String,
    val CGST_percentage: String,
    val SGST_amount: String,
    val SGST_percentage: String,
    val about_me: String,
    val address: String,
    val category_id: String,
    val sp_id: String,
    val city: String,
    val country: String,
    val present_status: String,
    val profession_id: String,
    val created_dts: String,
    val discount: Int,
    val distance_kms: String,
    val distance_miles: String,
    val dob: String,
    val exp: String,
    val extra_charge: String,
    val fcm_token: String,
    val final_amount: Int,
    val fname: String,
    val gender: String,
    val id: String,
    val jobs_count: Int,
    val keywords: String,
    val languages_known: String,
    val latitude: String,
    val lname: String,
    val longitude: String,
    val min_charges: String,
    val minimum_amount: String,
    val mobile: String,
    val per_day: String,
    val per_hour: String,
    val points_count: String,
    val postcode: String,
    val profession: String,
    val professionalism: String,
    val behaviour: String,
    val satisfaction: String,
    val skill: String,
    val profile_pic: String,
    val qualification: String,
    val rank: Int,
    val rating: String,
    val referral_id: String,
    val reg_status: String,
    val registered_on: String,
    val state: String,
    val subcategory_id: String,
    val total_people: Int,
    val users_id: String
): Serializable