package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

data class ProviderMemberShipPlanPaymentReqModel(
    val amount: Int,
    val date: String,
    val key: String,
    val payment_status: String,
    val period: Int,
    val plan_id: Int,
    val reference_id: String,
    val sp_id: Int
)