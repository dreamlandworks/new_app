package com.satrango.ui.user.user_dashboard.user_offers.models

data class Data(
    val coupon_code: String,
    val id: String,
    val name: String,
    val offer_image: String,
    val offer_type_id: String,
    val offer_type_name: String,
    val valid_till: String,
    val value: String,
    val value_type_id: String,
    val value_type_name: String
)