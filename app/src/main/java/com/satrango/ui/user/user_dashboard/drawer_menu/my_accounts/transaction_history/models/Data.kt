package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("payment_status")
    val payment_status: String,
    @SerializedName("reference_id")
    val reference_id: String,
    @SerializedName("transaction_method")
    val transaction_method: String,
    @SerializedName("transaction_name")
    val transaction_name: String,
    @SerializedName("transaction_type")
    val transaction_type: String
): Serializable