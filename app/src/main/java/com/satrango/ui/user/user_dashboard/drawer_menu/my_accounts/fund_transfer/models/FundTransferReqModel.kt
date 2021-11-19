package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models

data class FundTransferReqModel(
    val amount: String,
    val date: String,
    val key: String,
    val payment_status: String,
    val reference_id: String,
    val users_id: Int
)