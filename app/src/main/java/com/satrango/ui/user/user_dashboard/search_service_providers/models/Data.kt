package com.satrango.ui.user.user_dashboard.search_service_providers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("CGST_amount")
    val CGST_amount: String,
    @SerializedName("CGST_percentage")
    val CGST_percentage: String,
    @SerializedName("SGST_amount")
    val SGST_amount: String,
    @SerializedName("SGST_percentage")
    val SGST_percentage: String,
    @SerializedName("about_me")
    val about_me: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("present_status")
    val present_status: String,
    @SerializedName("profession_id")
    val profession_id: String,
    @SerializedName("created_dts")
    val created_dts: String,
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("distance_kms")
    val distance_kms: String,
    @SerializedName("distance_miles")
    val distance_miles: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("exp")
    val exp: String,
    @SerializedName("extra_charges")
    val extra_charge: String,
    @SerializedName("fcm_token")
    val fcm_token: String,
    @SerializedName("final_amount")
    val final_amount: Int,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("jobs_count")
    val jobs_count: Int,
    @SerializedName("keywords")
    val keywords: String,
    @SerializedName("languages_known")
    val languages_known: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("min_charges")
    val min_charges: String,
    @SerializedName("minimum_amount")
    val minimum_amount: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("per_day")
    val per_day: String,
    @SerializedName("per_hour")
    val per_hour: String,
    @SerializedName("points_count")
    val points_count: String,
    @SerializedName("postcode")
    val postcode: String,
    @SerializedName("profession")
    val profession: String,
    @SerializedName("professionlism")
    val professionalism: String,
    @SerializedName("behaviour")
    val behaviour: String,
    @SerializedName("satisfaction")
    val satisfaction: String,
    @SerializedName("skill")
    val skill: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("qualification")
    val qualification: String,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("referral_id")
    val referral_id: String,
    @SerializedName("reg_status")
    val reg_status: String,
    @SerializedName("registered_on")
    val registered_on: String,
    @SerializedName("status")
    val state: String,
    @SerializedName("subcategory_id")
    val subcategory_id: String,
    @SerializedName("total_people")
    val total_people: Int,
    @SerializedName("users_id")
    val users_id: String
): Serializable