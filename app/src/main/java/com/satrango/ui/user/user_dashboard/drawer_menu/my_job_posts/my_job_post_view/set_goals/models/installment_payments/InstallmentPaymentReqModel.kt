package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class InstallmentPaymentReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("payment_status")
    val payment_status: String,
    @SerializedName("reference_id")
    val reference_id: String,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("bid_id")
    val bid_id: Int,
    @SerializedName("sp_id")
    val sp_id: Int
): Serializable