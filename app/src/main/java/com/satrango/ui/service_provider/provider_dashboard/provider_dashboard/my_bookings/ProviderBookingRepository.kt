package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingResModel
import okhttp3.RequestBody

class ProviderBookingRepository: BaseRepository() {

    suspend fun getBookingListWithDetails(requestBody: ProviderBookingReqModel): ProviderBookingResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().bookingListWithDetails(requestBody)
    }

}