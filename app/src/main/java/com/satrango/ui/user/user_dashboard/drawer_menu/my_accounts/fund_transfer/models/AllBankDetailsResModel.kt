package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models

import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.UserBankAccount

data class AllBankDetailsResModel(
    val message: String,
    val status: Int,
    val user_bank_accounts: List<UserBankAccount>
)