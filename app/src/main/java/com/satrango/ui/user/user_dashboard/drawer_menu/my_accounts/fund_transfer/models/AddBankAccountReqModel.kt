package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class AddBankAccountReqModel(
    @SerializedName("account_name")
    val account_name: String,
    @SerializedName("account_no")
    val account_no: String,
    @SerializedName("ifsc_code")
    val ifsc_code: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable