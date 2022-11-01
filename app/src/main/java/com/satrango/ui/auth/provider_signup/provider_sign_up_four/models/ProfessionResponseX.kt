package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProfessionResponseX(
    @SerializedName("keywords_responses")
    var keywords_responses: ArrayList<KeywordsResponse>,
    @SerializedName("tariff_extra_charges")
    var tariff_extra_charges: String,
    @SerializedName("tariff_min_charges")
    var tariff_min_charges: String,
    @SerializedName("tariff_per_day")
    var tariff_per_day: String,
    @SerializedName("tariff_per_hour")
    var tariff_per_hour: String,
    @SerializedName("experience")
    var experience: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("prof_id")
    val prof_id: String,
    @SerializedName("category_id")
    var category_id: String,
    @SerializedName("subcategory_id")
    var subcategory_id: String,
): Serializable