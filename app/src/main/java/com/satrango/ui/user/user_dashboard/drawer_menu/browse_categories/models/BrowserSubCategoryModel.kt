package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BrowserSubCategoryModel(
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("sub_name")
    val sub_name: String
): Serializable