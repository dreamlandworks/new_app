package com.satrango.ui.user.user_dashboard.user_home_screen.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("id")
    val id: String,
    @SerializedName("phrase")
    val phrase: String,
    @SerializedName("keywords_id")
    val keywords_id: String,
    @SerializedName("subcategory_id")
    val subcategory_id: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("created_on")
    val created_on: String
): Serializable