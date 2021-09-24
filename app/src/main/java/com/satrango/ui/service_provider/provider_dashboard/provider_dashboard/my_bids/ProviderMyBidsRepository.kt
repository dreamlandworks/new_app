package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.models.ProviderMyBidsResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.place_bid.models.ProviderPostBidReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.place_bid.models.ProviderPostBidResModel

class ProviderMyBidsRepository: BaseRepository() {

    suspend fun getBidsList(requestBody: ProviderBookingReqModel): ProviderMyBidsResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getBidsList(requestBody)
    }

    suspend fun getBidJobsList(requestBody: ProviderBookingReqModel): ProviderMyBidsResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().getBidJobsList(requestBody)
    }

    suspend fun postBid(requestBody: ProviderPostBidReqModel): ProviderPostBidResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().postBid(requestBody)
    }

}