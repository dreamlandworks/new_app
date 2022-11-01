package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderSignUpFourReqModel(
    @SerializedName("about_me")
    val about_me: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("lang_responses")
    val lang_responses: List<LangResponse>,
    @SerializedName("profession_responses")
    val profession_responses: List<ProfessionResponseX>,
    @SerializedName("qualification_responses")
    val qualification_responses: List<QualificationResponse>,
    @SerializedName("timeslot_responses")
    val timeslot_responses: List<TimeslotResponse>,
    @SerializedName("user_id")
    val user_id: String
): Serializable