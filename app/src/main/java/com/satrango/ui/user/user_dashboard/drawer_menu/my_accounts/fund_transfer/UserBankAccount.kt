package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer

data class UserBankAccount(
    val account_name: String,
    val account_no: String,
    val bank_details_id: String,
    val ifsc_code: String,
    val ubd_id: String,
    val users_id: String,
    val isSelected: Boolean
)