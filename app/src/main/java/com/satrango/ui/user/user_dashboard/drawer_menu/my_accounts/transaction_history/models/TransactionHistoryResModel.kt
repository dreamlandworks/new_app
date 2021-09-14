package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models

data class TransactionHistoryResModel(
    val data: List<Data>,
    val message: String,
    val status: Int
)