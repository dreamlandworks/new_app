package com.satrango.ui.service_provider.provider_dashboard.plans.models

data class ProviderPlanTxnResModel(
    val amount: Int,
    val message: String,
    val order_id: String,
    val status: Int,
    val txn_id: String
)