package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals

import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.models.GoalsInstallmentsDetail

interface ProviderReleaseGoalsInterface {

    fun sendRequest(data: GoalsInstallmentsDetail)

}