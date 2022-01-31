package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models

data class ProviderMyBidsResModel(
    val job_post_details: List<JobPostDetail>,
    val message: String,
    val status: Int
)