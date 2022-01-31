package com.satrango.ui.auth.provider_signup.provider_sign_up_three

data class ProviderTimeSlotModel(
    val fromDate: String,
    val toDate: String,
    val daysType: String,
    val days: List<String>
)
