package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models

data class Data(
    val amount: Int,
    val booking_id: String,
    val date: String,
    val payment_status: String,
    val reference_id: String,
    val transaction_method: String,
    val transaction_name: String,
    val transaction_type: String
)