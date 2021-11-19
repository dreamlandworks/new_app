package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models

data class AddBankAccountReqModel(
    val account_name: String,
    val account_no: String,
    val ifsc_code: String,
    val key: String,
    val users_id: Int
)