package com.satrango.ui.auth.provider_signup.provider_sign_up_one.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Profession(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("subcategory_id")
    val subcategory_id: String,
)