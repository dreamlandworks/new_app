package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models

data class FundTransferReqModel(
    val amount: String,
    val key: String,
    val users_id: Int,
    val order_id: String,
)