package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.TimeslotResponse
import java.io.Serializable

@Keep
data class UpdateTariffReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("timeslot_responses")
    val timeslot_responses: List<TimeslotResponse>,
    @SerializedName("user_id")
    val user_id: String
): Serializable