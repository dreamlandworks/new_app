package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SaveInstallmentResModel(
    @SerializedName("installment_det_id")
    val installment_det_id: Int,
    @SerializedName("wallet_balance")
    val wallet_balance: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable