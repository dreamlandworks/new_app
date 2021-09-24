package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.models

data class ProviderMyJobsListResModel(
    val job_post_details: List<JobPostDetailX>,
    val message: String,
    val status: Int
)