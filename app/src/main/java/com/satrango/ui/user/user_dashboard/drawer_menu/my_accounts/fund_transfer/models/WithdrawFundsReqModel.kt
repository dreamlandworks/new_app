package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models

data class WithdrawFundsReqModel(
    val amount: String,
    val date: String,
    val key: String,
    val ubd_id: Int,
    val users_id: Int
)