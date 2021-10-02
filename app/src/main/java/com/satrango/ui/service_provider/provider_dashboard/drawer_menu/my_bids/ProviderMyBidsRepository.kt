package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models.ProviderMyBidsResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderBidEditReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderDeleteBidAttachmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderPostBidReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderPostBidResModel
import okhttp3.ResponseBody

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

    suspend fun deleteBidAttachment(requestBody: ProviderDeleteBidAttachmentReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().deleteBidAttachment(requestBody)
    }

    suspend fun editBids(requestBody: ProviderBidEditReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().editBid(requestBody)
    }

}