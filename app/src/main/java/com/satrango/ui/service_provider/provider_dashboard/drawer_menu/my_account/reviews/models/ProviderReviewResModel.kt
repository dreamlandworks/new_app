package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models

data class ProviderReviewResModel(
    val message: String,
    val sp_reviews: List<SpReview>,
    val status: Int
)