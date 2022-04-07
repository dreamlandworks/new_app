package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

data class ProviderMemberShipPlanPaymentReqModel(
    val amount: Int,
    val date: String,
    val key: String,
    val period: Int,
    val plan_id: Int,
    val sp_id: Int,
    val w_amount: String,
    val order_id: String
)