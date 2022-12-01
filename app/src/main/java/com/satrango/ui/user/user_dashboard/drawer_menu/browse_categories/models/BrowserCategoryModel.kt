package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BrowserCategoryModel(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("selected")
    val selected: Boolean
): Serializable