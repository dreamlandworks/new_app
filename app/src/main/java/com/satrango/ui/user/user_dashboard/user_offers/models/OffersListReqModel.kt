package com.satrango.ui.user.user_dashboard.user_offers.models

data class OffersListReqModel(
    val city: String,
    val country: String,
    val key: String,
    val offer_type_id: Int,
    val postal_code: String,
    val state: String,
    val users_id: Int
)