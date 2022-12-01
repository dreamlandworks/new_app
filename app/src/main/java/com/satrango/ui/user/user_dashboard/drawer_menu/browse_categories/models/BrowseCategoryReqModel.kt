package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BrowseCategoryReqModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String
) : Serializable
