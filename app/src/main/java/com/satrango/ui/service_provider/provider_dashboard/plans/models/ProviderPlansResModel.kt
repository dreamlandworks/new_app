package com.satrango.ui.service_provider.provider_dashboard.plans.models

data class ProviderPlansResModel(
    val activated_plan: Int,
    val data: List<Data>,
    val message: String,
    val status: Int
)