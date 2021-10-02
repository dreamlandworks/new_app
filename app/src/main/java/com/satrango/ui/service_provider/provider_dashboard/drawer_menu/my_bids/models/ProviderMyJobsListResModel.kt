package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models

data class ProviderMyJobsListResModel(
    val job_post_details: List<JobPostDetailX>,
    val message: String,
    val status: Int
)