package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

data class AddFundsResModel(
    val message: String,
    val order_id: String,
    val status: Int,
    val txn_id: String
)