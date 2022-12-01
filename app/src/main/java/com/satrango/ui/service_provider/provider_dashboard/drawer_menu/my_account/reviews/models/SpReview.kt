package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SpReview(
    @SerializedName("behaviour")
    val behaviour: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("feedback")
    val feedback: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("overall_rating")
    val overall_rating: String,
    @SerializedName("created_dts")
    val created_dts: String,
    @SerializedName("professionalism")
    val professionalism: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("satisfaction")
    val satisfaction: String,
    @SerializedName("skill")
    val skill: String,
    @SerializedName("sp_id")
    val sp_id: String
): Serializable