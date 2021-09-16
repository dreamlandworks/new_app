package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import okhttp3.RequestBody
import okhttp3.ResponseBody

class ProviderBookingRepository: BaseRepository() {

    suspend fun getBookingListWithDetails(requestBody: ProviderBookingReqModel): ProviderBookingResModel {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().bookingListWithDetails(requestBody)
    }

    suspend fun postExtraDemand(requestBody: ExtraDemandReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().postExtraDemand(requestBody)
    }

    suspend fun expenditureIncurred(requestBody: ExpenditureIncurredReqModel): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().expenditureIncurred(requestBody)
    }

}