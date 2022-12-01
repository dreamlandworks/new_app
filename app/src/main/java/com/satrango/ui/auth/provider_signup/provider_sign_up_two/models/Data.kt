package com.satrango.ui.auth.provider_signup.provider_sign_up_two.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("keyword_id")
    val keyword_id: String,
    @SerializedName("keyword")
    val keyword: String,
    @SerializedName("subcategory_id")
    val subcategory_id: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("profession_id")
    val profession_id: String,
): Serializable