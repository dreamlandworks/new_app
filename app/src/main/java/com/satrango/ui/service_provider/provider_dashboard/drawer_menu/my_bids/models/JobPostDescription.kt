package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models

data class JobPostDescription(
    val address_id: String,
    val city: String,
    val country: String,
    val id: String,
    val job_description: String,
    val latitude: String,
    val locality: String,
    val longitude: String,
    val state: String,
    val zipcode: String
)