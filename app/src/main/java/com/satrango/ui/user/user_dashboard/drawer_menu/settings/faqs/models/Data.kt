package com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("answer")
    val answer: String,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("question")
    val question: String
): Serializable