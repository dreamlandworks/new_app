package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Profession(
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("subcategory_id")
    val subcategory_id: String,
    @SerializedName("exp")
    val exp: String,
    @SerializedName("profession_name")
    val profession_name: String,
    @SerializedName("profession_id")
    val profession_id: String,
    @SerializedName("skills")
    val skills: List<Skill>,
    @SerializedName("tariff_extra_charges")
    val tariff_extra_charges: String,
    @SerializedName("tariff_id")
    val tariff_id: String,
    @SerializedName("tariff_min_charges")
    val tariff_min_charges: String,
    @SerializedName("tariff_per_day")
    val tariff_per_day: String,
    @SerializedName("tariff_per_hour")
    val tariff_per_hour: String
): Serializable